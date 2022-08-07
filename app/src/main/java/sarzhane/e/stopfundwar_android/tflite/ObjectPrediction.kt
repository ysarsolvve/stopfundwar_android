package sarzhane.e.stopfundwar_android.tflite

import android.graphics.RectF

data class ObjectPrediction(val location: RectF, val label: String, val score: Float, val labelId: Int)