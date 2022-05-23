package sarzhane.e.stopfundwar_android.presentation.camera.view



import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.schedulers.Schedulers
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.core.navigation.Navigator
import sarzhane.e.stopfundwar_android.core.navigation.PermissionsScreen
import sarzhane.e.stopfundwar_android.databinding.FragmentCameraBinding
import sarzhane.e.stopfundwar_android.presentation.PermissionsFragment
import sarzhane.e.stopfundwar_android.presentation.camera.info.InfoDialogFragment
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CameraViewModel
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CompaniesResult
import sarzhane.e.stopfundwar_android.tflite.ImageProcess
import sarzhane.e.stopfundwar_android.tflite.Recognition
import sarzhane.e.stopfundwar_android.tflite.Yolov5TFLiteDetector
import sarzhane.e.stopfundwar_android.util.exhaustive
import sarzhane.e.stopfundwar_android.util.toGone
import sarzhane.e.stopfundwar_android.util.toVisible
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : Fragment(R.layout.fragment_camera) {

    @Inject
    lateinit var navigator: Navigator

    private val binding by viewBinding(FragmentCameraBinding::bind)
    private lateinit var preview: Preview // Preview use case, fast, responsive view of the camera
    private lateinit var viewFinder: PreviewView // Preview use case, fast, responsive view of the camera
    private lateinit var boxLabelCanvas: ImageView
    private lateinit var imageAnalyzer: ImageAnalysis // Analysis use case, for running ML code
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider? = null
    private var yolov5TFLiteDetector: Yolov5TFLiteDetector? = null
    lateinit var cameraControl: CameraControl
    private var flashFlag: Boolean = false
    var rotation = 0
    private val brandAdapter = BrandAdapter()
    private val viewModel: CameraViewModel by viewModels()


    override fun onResume() {
        super.onResume()
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            navigator.navigateTo(screen = PermissionsScreen(), addToBackStack = false)
        }
        Timber.i("onResume()" )
        startCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("onDestroyView()" )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate()" )
        initModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("onViewCreated()" )
        viewFinder = binding.viewFinder
        boxLabelCanvas = binding.boxLabelCanvas
        viewFinder.scaleType = PreviewView.ScaleType.FILL_START
        viewModel.searchResult.observe(viewLifecycleOwner, ::handleCompanies)
        setupReviewsList()
        binding.ivFlash.setOnClickListener {
            flashFlag = !flashFlag
            cameraControl.enableTorch(flashFlag)
        }
        binding.ivInfo.setOnClickListener { showInfoDialogFragment() }
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

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy()" )
        cameraExecutor.shutdown()
        yolov5TFLiteDetector = null
        cameraProvider = null
    }

    private fun setupReviewsList() {
        binding.rvRecognitions.adapter = brandAdapter
    }

    private fun initModel() {
        try {
            yolov5TFLiteDetector = Yolov5TFLiteDetector()
            yolov5TFLiteDetector!!.addGPUDelegate()
            yolov5TFLiteDetector!!.initialModel(requireActivity())
        } catch (e: java.lang.Exception) {
            Timber.e("load model error: " + e.message + e.toString())
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener(Runnable {
            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()
            .also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

        imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysisUseCase: ImageAnalysis ->
                analysisUseCase.setAnalyzer(
                    cameraExecutor,
                    ImageAnalyzer(
                        viewFinder,
                        rotation,
                        yolov5TFLiteDetector!!,
                        boxLabelCanvas,
                        viewModel
                    )
                )
            }

        val cameraSelector =
            if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA))
                CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview,imageAnalyzer
            )
            cameraControl = camera.cameraControl
            cameraControl.enableTorch(flashFlag)
            Timber.e("preview " + preview + " ,imageAnalyzer " + imageAnalyzer + " this"+ this +" cameraProvider"+cameraProvider)
        } catch (exc: Exception) {
            Timber.e(exc, "Use case binding failed")
        }
    }

    private class ImageAnalyzer(
        val previewView: PreviewView, val rotation: Int,
        val yolov5TFLiteDetector: Yolov5TFLiteDetector, val boxLabelCanvas: ImageView,val viewModel: CameraViewModel
    ) :
        ImageAnalysis.Analyzer {
        val imageProcess: ImageProcess = ImageProcess()

        class Result(var bitmap: Bitmap)

        override fun analyze(image: ImageProxy) {

            val previewHeight = previewView.height
            val previewWidth = previewView.width

            Observable.create(ObservableOnSubscribe<Result> { emitter ->
                Timber.i("previewWidth " + previewWidth + "/" + previewHeight)
                Timber.e("rotation " + rotation + " ,previewHeight " + previewView.height + " ,previewWidth " + previewView.width)

                val yuvBytes = arrayOfNulls<ByteArray>(3)
                val planes = image.planes
                val imageHeight = image.height
                val imageWidth = image.width

                Timber.e("imageHeight " + image.height + " ,imageWidth " + image.width + " ")

                imageProcess.fillBytes(planes, yuvBytes)
                val yRowStride = planes[0].rowStride
                val uvRowStride = planes[1].rowStride
                val uvPixelStride = planes[1].pixelStride
                val rgbBytes = IntArray(imageHeight * imageWidth)
                imageProcess.YUV420ToARGB8888(
                    yuvBytes[0]!!,
                    yuvBytes[1]!!,
                    yuvBytes[2]!!,
                    imageWidth,
                    imageHeight,
                    yRowStride,
                    uvRowStride,
                    uvPixelStride,
                    rgbBytes
                )

                // Исходное изображение
                val imageBitmap =
                    Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
                imageBitmap.setPixels(rgbBytes, 0, imageWidth, 0, 0, imageWidth, imageHeight)

                Timber.e("imageBitmap H " + imageBitmap.height + " ,imageBitmap W " + imageBitmap.width + " ")

                // Изображение адаптировано к экрану fill_start формат bitmap
                val scale = Math.max(
                    previewHeight / (if (rotation % 180 == 0) imageWidth else imageHeight).toDouble(),
                    previewWidth / (if (rotation % 180 == 0) imageHeight else imageWidth).toDouble()
                )

                Timber.e("scale  " + scale + " ")
                val fullScreenTransform = imageProcess.getTransformationMatrix(
                    imageWidth, imageHeight,
                    (scale * imageHeight).toInt(), (scale * imageWidth).toInt(),
                    if (rotation % 180 == 0) 90 else 0, false
                )

                // Полноразмерное растровое изображение для предварительного просмотра
                val fullImageBitmap = Bitmap.createBitmap(
                    imageBitmap,
                    0,
                    0,
                    imageWidth,
                    imageHeight,
                    fullScreenTransform,
                    false
                )
                Timber.e("fullImageBitmap H " + fullImageBitmap.height + " ,fullImageBitmap W " + fullImageBitmap.width + " ")
                // Обрезаем растровое изображение до того же размера, что и предварительный просмотр на экране
                val cropImageBitmap = Bitmap.createBitmap(
                    fullImageBitmap, 0, 0,
                    previewWidth, previewHeight
                )

                Timber.e("cropImageBitmap H " + cropImageBitmap.height + " ,cropImageBitmap W " + cropImageBitmap.width + " ")

                // Растровое изображение входа модели
                val previewToModelTransform = imageProcess.getTransformationMatrix(
                    cropImageBitmap.width, cropImageBitmap.height,
                    yolov5TFLiteDetector.inputSize.width,
                    yolov5TFLiteDetector.inputSize.height,
                    0, false
                )
                val modelInputBitmap = Bitmap.createBitmap(
                    cropImageBitmap, 0, 0,
                    cropImageBitmap.width, cropImageBitmap.height,
                    previewToModelTransform, false
                )
                val modelToPreviewTransform = Matrix()
                previewToModelTransform.invert(modelToPreviewTransform)
                Timber.e("modelInputBitmap H " + modelInputBitmap.height + " ,cropImageBitmap W " + modelInputBitmap.width + " ")
                val recognitions: ArrayList<Recognition>? =
                    yolov5TFLiteDetector.detect(modelInputBitmap)
                val emptyCropSizeBitmap =
                    Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
                Timber.e("emptyCropSizeBitmap H " + emptyCropSizeBitmap.height + " ,cropImageBitmap W " + emptyCropSizeBitmap.width + " ")
                val cropCanvas = Canvas(emptyCropSizeBitmap)
                Timber.e("brands " + recognitions)
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
                for (res in recognitions!!) {
                    val location: RectF = res.getLocation()
                    val label: String? = res.labelName
                    val confidence: Float? = res.confidence
                    labelIds.add(res.labelId)
                    modelToPreviewTransform.mapRect(location)
//                    cropCanvas.drawCircle(location.centerX(), location.centerY(), 15f, circlePaint)
                    cropCanvas.drawRect(location, boxPaint)
                    cropCanvas.drawText(label + ":" + String.format("%.2f", confidence), location.left, location.top, textPain)
                }
                viewModel.getCompany(labelIds.map { it.toString() })
                labelIds.clear()
                image.close()
                emitter.onNext(Result(emptyCropSizeBitmap))
            })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result: Result? ->
                    boxLabelCanvas.setImageBitmap(result?.bitmap)
                }
        }
    }

    private fun showInfoDialogFragment() {
        val dialogFragment = InfoDialogFragment.newInstance()
        dialogFragment.show(childFragmentManager, InfoDialogFragment.TAG)
    }
}