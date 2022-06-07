package sarzhane.e.stopfundwar_android.presentation.camera.view



import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.core.navigation.Navigator
import sarzhane.e.stopfundwar_android.core.navigation.PermissionsScreen
import sarzhane.e.stopfundwar_android.databinding.FragmentCameraBinding
import sarzhane.e.stopfundwar_android.presentation.PermissionsFragment
import sarzhane.e.stopfundwar_android.presentation.camera.info.InfoDialogFragment
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CameraViewModel
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CompaniesResult
import sarzhane.e.stopfundwar_android.tflite.ObjectDetectionHelper
import sarzhane.e.stopfundwar_android.util.exhaustive
import sarzhane.e.stopfundwar_android.util.toVisible
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : Fragment(R.layout.fragment_camera) {

    @Inject
    lateinit var navigator: Navigator

    private val binding by viewBinding(FragmentCameraBinding::bind)
    private val brandAdapter = BrandAdapter()
    private val viewModel: CameraViewModel by viewModels()
    private lateinit var cameraControl: CameraControl
    private var flashFlag: Boolean = false
    private var pauseAnalysis = false

    private lateinit var bitmapBuffer: Bitmap

    private val executor = Executors.newSingleThreadExecutor()
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var imageRotationDegrees: Int = 0
    private val tfImageBuffer = TensorImage(DataType.FLOAT32)

    private val tfImageProcessor by lazy {
        val cropSize = minOf(bitmapBuffer.width, bitmapBuffer.height)
        Log.d("tfImageProcessor","cropSize $cropSize")
        ImageProcessor.Builder()
            .add(
                ResizeOp(
                tfInputSize.height, tfInputSize.width, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR)
            )
            .add(Rot90Op(-imageRotationDegrees / 90))
            .add(NormalizeOp(127.5f, 127.5f))
            .build()
    }

    private val nnApiDelegate by lazy  {
        NnApiDelegate()
    }

    private val tflite by lazy {
        val options: Interpreter.Options = Interpreter.Options()
        options.numThreads = 5
        options.useNNAPI = true
        Interpreter(
            FileUtil.loadMappedFile(requireContext(), MODEL_PATH),
            options)
    }

    private val tfInputSize by lazy {
        val inputIndex = 0
        val inputShape = tflite.getInputTensor(inputIndex).shape()
        Size(inputShape[2], inputShape[1]) // Order of axis is: {1, height, width, 3}
    }
    private val detector by lazy {
        ObjectDetectionHelper(
            tflite,
            FileUtil.loadLabels(requireContext(), LABELS_PATH)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate()" )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("onViewCreated()" )
        binding.ivInfo.setOnClickListener { showInfoDialogFragment() }
        viewModel.searchResult.observe(viewLifecycleOwner, ::handleCompanies)
        setupReviewsList()
        setupCameraFlash()
        pauseAnalysis = false
    }

    private fun handleCompanies(state: CompaniesResult) {
        when (state) {
            is CompaniesResult.SuccessResult -> {
                binding.rvRecognitions.toVisible()
                brandAdapter.submitList(state.result)
            }
            is CompaniesResult.ErrorResult -> {
            }
            is CompaniesResult.EmptyResult -> {
                binding.rvRecognitions.toVisible()
            }
            CompaniesResult.Loading -> TODO()
        }.exhaustive
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            navigator.navigateTo(screen = PermissionsScreen(), addToBackStack = false)
        }else {
            bindCameraUseCases()
        }
    }

    /** Declare and bind preview and analysis use cases */
    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindCameraUseCases() = binding.viewFinder.post {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener ({

            // Camera provider is now guaranteed to be available
            val cameraProvider = cameraProviderFuture.get()

            // Set up the view finder use case to display camera preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .build()

            // Set up the image analysis use case which will process frames in real time
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()

            var frameCounter = 0
            var lastFpsTimestamp = System.currentTimeMillis()
            imageAnalysis.setAnalyzer(executor) { image ->
                if (!::bitmapBuffer.isInitialized) {
                    // The image rotation and RGB image buffer are initialized only once
                    // the analyzer has started running
                    imageRotationDegrees = image.imageInfo.rotationDegrees
                    bitmapBuffer = Bitmap.createBitmap(
                        image.width, image.height, Bitmap.Config.ARGB_8888
                    )
                }

                // Early exit: image analysis is in paused state
                if (pauseAnalysis) {
                    image.close()
                    return@setAnalyzer
                }

                // Copy out RGB bits to our shared buffer
                image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

                // Process the image in Tensorflow
                val tfImage = tfImageProcessor.process(tfImageBuffer.apply { load(bitmapBuffer) })

                // Perform the object detection for the current frame
                val predictions = detector.predict(tfImage)

                val temp = predictions.filter { it.score > ACCURACY_THRESHOLD }


                if (!pauseAnalysis) {
                    reportPrediction(temp)
                }

                Log.d("Speed", "predictions $predictions")


                // Compute the FPS of the entire pipeline
                val frameCount = 10
                if (++frameCounter % frameCount == 0) {
                    frameCounter = 0
                    val now = System.currentTimeMillis()
                    val delta = now - lastFpsTimestamp
                    val fps = 1000 * frameCount.toFloat() / delta
                    Log.d(
                        "TAG",
                        "FPS: ${"%.02f".format(fps)} with tensorSize: ${tfImage.width} x ${tfImage.height}"
                    )
                    lastFpsTimestamp = now
                }
            }

            // Create a new camera selector each time, enforcing lens facing
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

            // Apply declared configs to CameraX using the same lifecycle owner
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, preview, imageAnalysis)

            cameraControl = camera.cameraControl
            cameraControl.enableTorch(flashFlag)

            // Use the camera object to link our preview use case with the view
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun reportPrediction(
        predictions: List<ObjectDetectionHelper.ObjectPrediction>
    ) {
        val emptyCropSizeBitmap =
            Bitmap.createBitmap(binding.viewFinder.width, binding.viewFinder.height, Bitmap.Config.ARGB_8888)
        val cropCanvas = Canvas(emptyCropSizeBitmap)
        //                // Пограничная кисть
//                val circlePaint = Paint()
//                circlePaint.isAntiAlias = true
//                circlePaint.style = Paint.Style.FILL
//                circlePaint.color = Color.GREEN
        val boxPaint = Paint()
        boxPaint.strokeWidth = 5f
        boxPaint.style = Paint.Style.STROKE
        boxPaint.color = Color.GREEN
        // Кисть шрифта
        val textPain = Paint()
        textPain.textSize = 50f
        textPain.color = Color.RED
        textPain.style = Paint.Style.FILL

        val labelIds = mutableSetOf<Int>()

        for (prediction in predictions) {
            // Location has to be mapped to our local coordinates
            val location = mapOutputCoordinates(prediction.location)
            labelIds.add(prediction.labelId)
            val label: String = prediction.label
            val confidence: Float = prediction.score
            cropCanvas.drawRect(location, boxPaint.apply { boxPaint.color = colors.getValue(prediction.labelId) })
            cropCanvas.drawText(
                label + ":" + String.format("%.2f", confidence),
                location.left,
                location.top,
                textPain.apply { textPain.color = colors.getValue(prediction.labelId) }
            )
//            cropCanvas.drawCircle(location.centerX(), location.centerY(), 15f, circlePaint.apply { circlePaint.color = colors.getValue(prediction.labelId)})
        }
        viewModel.getCompany(labelIds.map { it.toString() })
        labelIds.clear()
        binding.boxLabelCanvas.post(kotlinx.coroutines.Runnable {
            binding.boxLabelCanvas.setImageBitmap(
                emptyCropSizeBitmap
            )
        })
    }

    /**
     * Helper function used to map the coordinates for objects coming out of
     * the model into the coordinates that the user sees on the screen.
     */
    private fun mapOutputCoordinates(location: RectF): RectF {

        // Step 1: map location to the preview coordinates
        val previewLocation = RectF(
            location.left * binding.viewFinder.width,
            location.top * binding.viewFinder.height,
            location.right * binding.viewFinder.width,
            location.bottom * binding.viewFinder.height
        )

        // Step 2: compensate for camera sensor orientation and mirroring

        // Step 3: compensate for 1:1 to 4:3 aspect ratio conversion + small margin
        val margin = 0.1f
        val requestedRatio = 4f / 3f
        val midX = (previewLocation.left + previewLocation.right) / 2f
        val midY = (previewLocation.top + previewLocation.bottom) / 2f
        return if (binding.viewFinder.width < binding.viewFinder.height) {
            RectF(
                midX - (1f + margin) * requestedRatio * previewLocation.width() / 2f,
                midY - (1f - margin) * previewLocation.height() / 2f,
                midX + (1f + margin) * requestedRatio * previewLocation.width() / 2f,
                midY + (1f - margin) * previewLocation.height() / 2f
            )
        } else {
            RectF(
                midX - (1f - margin) * previewLocation.width() / 2f,
                midY - (1f + margin) * requestedRatio * previewLocation.height() / 2f,
                midX + (1f - margin) * previewLocation.width() / 2f,
                midY + (1f + margin) * requestedRatio * previewLocation.height() / 2f
            )
        }
    }

    override fun onDestroyView() {
        pauseAnalysis = true
        super.onDestroyView()
        Timber.i("onDestroyView()" )
    }

    override fun onDestroy() {
        // Terminate all outstanding analyzing jobs (if there is any).
        executor.apply {
            shutdown()
            awaitTermination(1000, TimeUnit.MILLISECONDS)
        }
        // Release TFLite resources.
        tflite.close()
        nnApiDelegate.close()
        super.onDestroy()
        Timber.i("onDestroy()" )
    }

    private fun setupReviewsList() {
        binding.rvRecognitions.adapter = brandAdapter
    }
    private fun setupCameraFlash() {
        binding.ivFlash.setOnClickListener {
            flashFlag = !flashFlag
            cameraControl.enableTorch(flashFlag)
        }
    }

    private fun showInfoDialogFragment() {
        val dialogFragment = InfoDialogFragment.newInstance()
        dialogFragment.show(childFragmentManager, InfoDialogFragment.TAG)
    }

    companion object {

        private const val ACCURACY_THRESHOLD = 0.75f
        private const val MODEL_PATH = "model.tflite"
        private const val LABELS_PATH = "coco_label.txt"
    }

    private val colors = mapOf(
        0	to	-256,
        1	to	-65536,
        2	to	-65536,
        3	to	-65536,
        4	to	-65536,
        5	to	-65536,
        6	to	-65536,
        7	to	-65536,
        8	to	-65536,
        9	to	-65536,
        10	to	-65536,
        11	to	-65536,
        12	to	-65536,
        13	to	-65536,
        14	to	-65536,
        15	to	-65536,
        16	to	-65536,
        17	to	-65536,
        18	to	-65536,
        19	to	-65536,
        20	to	-65536,
        21	to	-65536,
        22	to	-65536,
        23	to	-65536,
        24	to	-65536,
        25	to	-65536,
        26	to	-65536,
        27	to	-65536,
        28	to	-65536,
        29	to	-65536,
        30	to	-65536,
        31	to	-65536,
        32	to	-65536,
        33	to	-65536,
        34	to	-65536,
        35	to	-65536,
        36	to	-65536,
        37	to	-65536,
        38	to	-65536,
        39	to	-65536,
        40	to	-65536,
        41	to	-65536,
        42	to	-7829368,
        43	to	-7829368,
        44	to	-7829368,
        45	to	-7829368,
        46	to	-7829368,
        47	to	-7829368,
        48	to	-7829368,
        49	to	-7829368,
        50	to	-7829368,
        51	to	-7829368


    )

}