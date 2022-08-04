package sarzhane.e.stopfundwar_android.tflite

import android.graphics.RectF
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import sarzhane.e.stopfundwar_android.presentation.camera.view.CameraFragment

class ObjectDetectionHelper(private val tflite: Interpreter) {

    data class ObjectPrediction(val location: RectF, val label: String, val score: Float, val labelId: Int)

    private val locations = arrayOf(Array(OBJECT_COUNT) { FloatArray(4) })
    private val labelIndices =  arrayOf(FloatArray(OBJECT_COUNT))
    private val scores =  arrayOf(FloatArray(OBJECT_COUNT))

    private val outputBuffer = mapOf(
        0 to scores,
        1 to locations,
        2 to FloatArray(1),
        3 to labelIndices
    )

    private val predictions get() = (0 until OBJECT_COUNT).map {
        ObjectPrediction(
            location = locations[0][it].let { RectF(it[1], it[0], it[3], it[2]) },
            label = "",
            score = scores[0][it],
            labelId = labelIndices[0][it].toInt()
        )
    }

    fun predict(image: TensorImage): List<ObjectPrediction> {
        Log.d("Speed","start")
        tflite.runForMultipleInputsOutputs(arrayOf(image.buffer), outputBuffer)
        Log.d("Speed","finish")
        return predictions
    }

    companion object {
        const val OBJECT_COUNT = 10
    }
}