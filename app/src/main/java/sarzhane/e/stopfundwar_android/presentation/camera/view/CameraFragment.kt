package sarzhane.e.stopfundwar_android.presentation.camera.view


import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.Size
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import by.kirich1409.viewbindingdelegate.viewBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.core.navigation.Navigator
import sarzhane.e.stopfundwar_android.core.navigation.PermissionsScreen
import sarzhane.e.stopfundwar_android.databinding.FragmentCameraBinding
import sarzhane.e.stopfundwar_android.domain.companies.Company
import sarzhane.e.stopfundwar_android.presentation.PermissionsFragment
import sarzhane.e.stopfundwar_android.presentation.camera.info.InfoDialogFragment
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CameraViewModel
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CompaniesResult
import sarzhane.e.stopfundwar_android.tflite.ObjectDetectionHelper
import sarzhane.e.stopfundwar_android.util.dpToPx
import sarzhane.e.stopfundwar_android.util.exhaustive
import sarzhane.e.stopfundwar_android.util.toGone
import sarzhane.e.stopfundwar_android.util.toVisible
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : Fragment(R.layout.fragment_camera) {

    @Inject
    lateinit var navigator: Navigator
    private lateinit var bitmapBuffer: Bitmap
    private lateinit var cameraControl: CameraControl
    private lateinit var countDownTimer: CountDownTimer
    private val binding by viewBinding(FragmentCameraBinding::bind)
    private val viewModel: CameraViewModel by viewModels()
    private var flashFlag: Boolean = false
    private var pauseAnalysis = false
    private val executor = Executors.newSingleThreadExecutor()
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var imageRotationDegrees: Int = 0
    private val tfImageBuffer = TensorImage(DataType.FLOAT32)
    private var colors = mapOf<Int,Int>()

    private val tfImageProcessor by lazy {
        ImageProcessor.Builder()
            .add(ResizeOp(
                tfInputSize.height, tfInputSize.width, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(Rot90Op(-imageRotationDegrees / 90))
            .add(NormalizeOp(127.5f, 127.5f))
            .build()
    }

    private val nnApiDelegate by lazy { NnApiDelegate() }

    private val tflite by lazy {
        val options: Interpreter.Options = Interpreter.Options()
        options.numThreads = 5
        options.useNNAPI = true
        Interpreter(FileUtil.loadMappedFile(requireContext(), MODEL_PATH), options)
    }

    private val tfInputSize by lazy {
        val inputIndex = 0
        val inputShape = tflite.getInputTensor(inputIndex).shape()
        Size(inputShape[2], inputShape[1])
    }
    private val detector by lazy {
        ObjectDetectionHelper(tflite, FileUtil.loadLabels(requireContext(), LABELS_PATH))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        colors = viewModel.getColors()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivInfo.setOnClickListener { showInfoDialogFragment() }
        viewModel.searchResult.observe(viewLifecycleOwner, ::handleCompanies)
        setupCameraFlash()
        pauseAnalysis = false
    }

    private fun handleCompanies(state: CompaniesResult) {
        when (state) {
            is CompaniesResult.SuccessResult -> {
                val rate = state.result.first().statusRate
                if (::countDownTimer.isInitialized) { countDownTimer.cancel() }
                if (rate == "F" || rate == "D") binding.alert.itemAlert.toVisible()
                else binding.alert.itemAlert.toGone()
                binding.recognition.recognitionItem.toVisible()
                binding.skeleton.skeletonItem.toGone()
                bind(state.result.first())
            }
            is CompaniesResult.EmptyResult -> {
                countDownTimer = object : CountDownTimer(6000, 1000) {
                    override fun onTick(time: Long) {
                        if (time < 5000) {
                            binding.alert.itemAlert.toGone()
                            binding.recognition.recognitionItem.toGone()
                            binding.skeleton.skeletonItem.toVisible()
                            binding.skeleton.tvStatus.text = getString(R.string.skeleton_first_status)
                        }
                    }

                    override fun onFinish() {
                        binding.skeleton.tvStatus.text = getString(R.string.skeleton_second_status)
                    }
                }.start()
            }
            is CompaniesResult.ErrorResult -> {}
        }.exhaustive
    }


    override fun onResume() {
        super.onResume()
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            navigator.navigateTo(screen = PermissionsScreen(), addToBackStack = false)
        } else {
            bindCameraUseCases()
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindCameraUseCases() = binding.viewFinder.post {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({

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

            // Use the camera object to link our preview use case with the view
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun reportPrediction(
        predictions: List<ObjectDetectionHelper.ObjectPrediction>
    ) {
        val emptyCropSizeBitmap =
            Bitmap.createBitmap(
                binding.viewFinder.width,
                binding.viewFinder.height,
                Bitmap.Config.ARGB_8888
            )
        val cropCanvas = Canvas(emptyCropSizeBitmap)
        //                // Пограничная кисть
                val circlePaint = Paint()
                circlePaint.isAntiAlias = true
                circlePaint.style = Paint.Style.FILL
                circlePaint.color = Color.GREEN
//        val boxPaint = Paint()
//        boxPaint.strokeWidth = 5f
//        boxPaint.style = Paint.Style.STROKE
//        boxPaint.color = Color.GREEN
//        // Кисть шрифта
//        val textPain = Paint()
//        textPain.textSize = 50f
//        textPain.color = Color.RED
//        textPain.style = Paint.Style.FILL

        val labelIds = mutableSetOf<Int>()
        val radius:Number = 8
        val radiusInPx = radius.dpToPx().toInt()

        for (prediction in predictions) {
            val location = mapOutputCoordinates(prediction.location)
            labelIds.add(prediction.labelId)
//            val label: String = prediction.label
//            val confidence: Float = prediction.score
//            cropCanvas.drawRect(location, boxPaint.apply { boxPaint.color = colors.getValue(prediction.labelId) })
//            cropCanvas.drawText(
//                label + ":" + String.format("%.2f", confidence),
//                location.left,
//                location.top,
//                textPain.apply { textPain.color = colors.getValue(prediction.labelId) }
//            )
            cropCanvas.drawCircle(location.centerX(), location.centerY(), radiusInPx.toFloat(), circlePaint.apply { circlePaint.color = colors.getValue(prediction.labelId)})
        }
        viewModel.getCompany(labelIds.map { it.toString() })
        labelIds.clear()
        binding.boxLabelCanvas.post(kotlinx.coroutines.Runnable {
            binding.boxLabelCanvas.setImageBitmap(
                emptyCropSizeBitmap
            )
        })
    }

    private fun mapOutputCoordinates(location: RectF): RectF {

        val previewLocation = RectF(
            location.left * binding.viewFinder.width,
            location.top * binding.viewFinder.height,
            location.right * binding.viewFinder.width,
            location.bottom * binding.viewFinder.height
        )

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
    }

    override fun onDestroy() {
        executor.apply { shutdown();awaitTermination(1000, TimeUnit.MILLISECONDS) }
        tflite.close()
        nnApiDelegate.close()
        super.onDestroy()
    }

    private fun setupCameraFlash() {
        binding.ivFlash.setOnClickListener {
            flashFlag = !flashFlag
            when(flashFlag){
                true -> binding.ivFlash.setImageResource(R.drawable.ic_flashlight_disable)
                false -> binding.ivFlash.setImageResource(R.drawable.ic_flashlight_able)
            }
            cameraControl.enableTorch(flashFlag)
        }
    }

    private fun showInfoDialogFragment() {
        val dialogFragment = InfoDialogFragment.newInstance()
        dialogFragment.show(childFragmentManager, InfoDialogFragment.TAG)
    }

    override fun onPause() {
        super.onPause()
        if (::countDownTimer.isInitialized) { countDownTimer.cancel() }
    }

    fun bind(brand: Company) {
        binding.recognition.tvBrandName.text = brand.brandName
        binding.recognition.tvDescription.text = brand.description
        binding.recognition.tvStatus.text = brand.statusInfo
        setStatusColor(brand.statusRate)
        setThumbnail(brand.logo)
    }

    private fun setStatusColor(brand: String?) {
        val padding:Number = 8
        val paddingInPx = padding.dpToPx().toInt()
        when (brand) {
            "A","B" -> {
                binding.recognition.tvStatus.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_green,
                    null
                )
                binding.recognition.cvItem.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_green,
                    null
                )
                binding.recognition.tvDescription.setTextColor(Color.parseColor("#288818"))
                binding.recognition.tvStatus.setPadding(paddingInPx)
            }
            "C" -> {
                binding.recognition.tvStatus.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_orange,
                    null
                )
                binding.recognition.cvItem.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_orange,
                    null
                )
                binding.recognition.tvDescription.setTextColor(Color.parseColor("#C6811A"))
                binding.recognition.tvStatus.setPadding(paddingInPx)
            }
            "F","D" -> {
                binding.recognition.tvStatus.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_red,
                    null
                )

                binding.recognition.cvItem.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_red,
                    null
                )
                binding.recognition.tvDescription.setTextColor(Color.parseColor("#CF2424"))
                binding.recognition.tvStatus.setPadding(paddingInPx)
            }
        }
    }

    private fun setThumbnail(brand: String?) {
        if (brand!!.isEmpty()) return
        Picasso.get()
            .load(brand)
            .placeholder( R.drawable.progress_animation )
            .fit()
            .centerInside()
            .into(binding.recognition.ivBrand)
    }

    companion object {

        private const val ACCURACY_THRESHOLD = 0.70f
        private const val MODEL_PATH = "model.tflite"
        private const val LABELS_PATH = "coco_label.txt"
    }

}