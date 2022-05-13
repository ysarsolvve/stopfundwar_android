package sarzhane.e.stopfundwar_android



import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.camera.core.*
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.schedulers.Schedulers
import sarzhane.e.stopfundwar_android.core.navigation.Navigator
import sarzhane.e.stopfundwar_android.core.navigation.PermissionsScreen
import sarzhane.e.stopfundwar_android.databinding.FragmentCameraBinding
import sarzhane.e.stopfundwar_android.presentation.PermissionsFragment
import sarzhane.e.stopfundwar_android.util.ImageProcess
import sarzhane.e.stopfundwar_android.util.Recognition
import sarzhane.e.stopfundwar_android.util.Yolov5TFLiteDetector
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : Fragment(R.layout.fragment_camera) {

    @Inject
    lateinit var navigator: Navigator

    private val binding by viewBinding(FragmentCameraBinding::bind)
    private lateinit var preview: Preview // Preview use case, fast, responsive view of the camera
    private lateinit var imageAnalyzer: ImageAnalysis // Analysis use case, for running ML code
    private lateinit var camera: Camera
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var yolov5TFLiteDetector: Yolov5TFLiteDetector? = null
    var rotation = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val viewFinder by lazy { binding.viewFinder }
    private val boxLabelCanvas by lazy { binding.boxLabelCanvas }

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            navigator.navigateTo(screen = PermissionsScreen(), addToBackStack = false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initModel("best")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFinder.scaleType = PreviewView.ScaleType.FILL_START
        startCamera()
    }

    private fun initModel(modelName: String) {
        try {
            yolov5TFLiteDetector = Yolov5TFLiteDetector()
            yolov5TFLiteDetector!!.modelFile = modelName
            yolov5TFLiteDetector!!.addGPUDelegate()
            yolov5TFLiteDetector!!.initialModel(requireActivity())
            Timber.i("Success loading model" + yolov5TFLiteDetector!!.modelFile)
        } catch (e: java.lang.Exception) {
            Timber.e("load model error: " + e.message + e.toString())
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysisUseCase: ImageAnalysis ->
                    analysisUseCase.setAnalyzer(
                        cameraExecutor,
                        ImageAnalyzer(
                            requireContext(),
                            viewFinder,
                            rotation,
                            yolov5TFLiteDetector!!,
                            boxLabelCanvas
                        )
                    )
                }

            val cameraSelector =
                if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA))
                    CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

                preview.setSurfaceProvider(viewFinder.surfaceProvider)
            } catch (exc: Exception) {
                Timber.e(exc, "Use case binding failed")
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private class ImageAnalyzer(
        val ctx: Context, val previewView: PreviewView, val rotation: Int,
        val yolov5TFLiteDetector: Yolov5TFLiteDetector, val boxLabelCanvas: ImageView
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
                // Пограничная кисть
                val circlePaint = Paint()
                circlePaint.isAntiAlias = true
                circlePaint.style = Paint.Style.FILL
                circlePaint.color = Color.GREEN

                for (res in recognitions!!) {
                    val location: RectF = res.getLocation()
                    val label: String? = res.labelName
                    val confidence: Float? = res.confidence
                    modelToPreviewTransform.mapRect(location)
                    cropCanvas.drawCircle(location.centerX(),location.centerY(),15f,circlePaint)
                }
                image.close()
                emitter.onNext(Result(emptyCropSizeBitmap))
            })
                .subscribeOn(Schedulers.io()) // Определите здесь watchee, который является потоком в коде выше, если он не определен,
                // то это главный поток синхронный, а не асинхронный
                // Здесь мы возвращаемся в основной поток, где наблюдатель получает данные, отправленные эмиттером, и обрабатывает их
                .observeOn(AndroidSchedulers.mainThread()) // Здесь мы возвращаемся в главный поток, чтобы обработать
                // данные обратного вызова из дочернего потока.
                ?.subscribe { result: Result? ->
                    boxLabelCanvas.setImageBitmap(result?.bitmap)
                }


        }
    }

}