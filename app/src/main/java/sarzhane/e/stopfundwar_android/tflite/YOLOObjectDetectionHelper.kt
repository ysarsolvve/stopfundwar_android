package sarzhane.e.stopfundwar_android.tflite

import android.graphics.RectF
import android.util.Log
import android.util.Size
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class YOLOObjectDetectionHelper(private val tflite: Interpreter,private val size: Size) {

    private val outputTensor = tflite.getOutputTensor(0).shape()
    private val inputSize = size

    fun predict(image: TensorImage): List<ObjectPrediction> {
        val probabilityBuffer = TensorBuffer.createFixedSize(outputTensor, DataType.FLOAT32)
        val outputs: MutableMap<Int?, Any?> = HashMap<Int?, Any?>()
        outputs[0] = probabilityBuffer.buffer
        Log.d("Speed","numOfClasses ${outputTensor}")
        Log.d("Speed","start")
        tflite.runForMultipleInputsOutputs(arrayOf(image.buffer), outputs)
        Log.d("Speed","finish")
        // Выходные данные выкладываются плиткой
        val recognitionArray = probabilityBuffer.floatArray
        // Здесь происходит повторный разбор уплощенного массива (xywh,obj,classes).
        val allRecognitions = ArrayList<ObjectPrediction>()
        for (i in 0 until outputTensor[1]) {
            val gridStride = i * outputTensor[2]
            // Поскольку при экспорте tflite автор yolov5 разделил вывод на размер изображения, здесь нужно умножить его обратно
            val x = recognitionArray[0 + gridStride] * inputSize.width
            val y = recognitionArray[1 + gridStride] * inputSize.height
            val w = recognitionArray[2 + gridStride] * inputSize.width
            val h = recognitionArray[3 + gridStride] * inputSize.height
            val xmin = Math.max(0.0, x - w / 2.0).toInt()
            val ymin = Math.max(0.0, y - h / 2.0).toInt()
            val xmax = Math.min(inputSize.width.toDouble(), x + w / 2.0).toInt()
            val ymax = Math.min(inputSize.height.toDouble(), y + h / 2.0).toInt()
            val confidence = recognitionArray[4 + gridStride]
            val classScores = Arrays.copyOfRange(recognitionArray, 5 + gridStride, outputTensor[2] + gridStride)
            var labelId = 0
            var maxLabelScores = 0f
            for (j in classScores.indices) {
                if (classScores[j] > maxLabelScores) {
                    maxLabelScores = classScores[j]
                    labelId = j
                }
            }
            val r = ObjectPrediction(
                RectF(xmin.toFloat(), ymin.toFloat(), xmax.toFloat(), ymax.toFloat()),
                "",
                confidence,
                labelId,
            )
            allRecognitions.add(
                r
            )
        }

//        // Неэкстремально подавленный выход
//        Log.d("Speed","nms start")
//        val nmsRecognitions = nms(allRecognitions)
//        Log.d("Speed","nms finish")
        // Второе неэкстремальное подавление, фильтрация тех, у которых более 2 границ одной цели идентифицированы как разные классы
        val nmsFilterBoxDuplicationRecognitions = nmsAllClass(allRecognitions)
        Log.d("Speed","nmsAllClass finish")
        return nmsFilterBoxDuplicationRecognitions
    }

    private fun nms(allRecognitions: ArrayList<ObjectPrediction>): ArrayList<ObjectPrediction> {
        val nmsRecognitions = ArrayList<ObjectPrediction>()

        // итерация по каждой категории, делая nms по каждой категории
        for (i in 0 until outputTensor[2] - 5) {
            // Создайте очередь для каждой категории, помещая в нее сначала те, у которых высокий показатель labelScore
            val pq = PriorityQueue<ObjectPrediction>(
                outputTensor[1]
            ) { l, r -> // Intentionally reversed to put high confidence at the head of the queue.
                (r.score).compareTo(l.score)
            }

            // отфильтровать одинаковые категории, при этом obj должен быть больше установленного порога
            for (j in allRecognitions.indices) {
                if (allRecognitions[j].labelId == i && allRecognitions[j].score > ACCURACY_THRESHOLD) {
                    pq.add(allRecognitions[j])
                }
            }

            // обход цикла nms
            while (pq.size > 0) {
                // Сначала убираются наиболее вероятные
                val a = arrayOfNulls<ObjectPrediction>(pq.size)
                val detections: Array<ObjectPrediction> = pq.toArray(a)
                val max = detections[0]
                nmsRecognitions.add(max)
                pq.clear()
                for (k in 1 until detections.size) {
                    val detection = detections[k]
                    if (boxIou(max.location, detection.location) < ACCURACY_THRESHOLD) {
                        pq.add(detection)
                    }
                }
            }
        }
        return nmsRecognitions
    }

    private fun nmsAllClass(allRecognitions: ArrayList<ObjectPrediction>): ArrayList<ObjectPrediction> {
        val nmsRecognitions = ArrayList<ObjectPrediction>()
        val pq = PriorityQueue<ObjectPrediction>(
            100
        ) { l, r -> // Intentionally reversed to put high confidence at the head of the queue.
            java.lang.Float.compare(r.score, l.score)
        }

        // отфильтровать одинаковые категории, при этом obj должен быть больше установленного порога
        for (j in allRecognitions.indices) {
            if (allRecognitions[j].score > ACCURACY_THRESHOLD) {
                pq.add(allRecognitions[j])
            }
        }
        while (pq.size > 0) {
            // Сначала убираются наиболее вероятные.
            val a = arrayOfNulls<ObjectPrediction>(pq.size)
            val detections: Array<ObjectPrediction> = pq.toArray(a)
            val max = detections[0]
            nmsRecognitions.add(max)
            pq.clear()
            for (k in 1 until detections.size) {
                val detection = detections[k]
                if (boxIou(
                        max.location,
                        detection.location
                    ) < ACCURACY_THRESHOLD
                ) {
                    pq.add(detection)
                }
            }
        }
        return nmsRecognitions
    }


    private fun boxIou(a: RectF, b: RectF): Float {
        val intersection = boxIntersection(a, b)
        val union = boxUnion(a, b)
        return if (union <= 0) 1f else intersection / union
    }

    private fun boxIntersection(a: RectF, b: RectF): Float {
        val maxLeft = if (a.left > b.left) a.left else b.left
        val maxTop = if (a.top > b.top) a.top else b.top
        val minRight = if (a.right < b.right) a.right else b.right
        val minBottom = if (a.bottom < b.bottom) a.bottom else b.bottom
        val w = minRight - maxLeft
        val h = minBottom - maxTop
        return if (w < 0 || h < 0) 0f else w * h
    }

    private fun boxUnion(a: RectF, b: RectF): Float {
        val i = boxIntersection(a, b)
        return (a.right - a.left) * (a.bottom - a.top) + (b.right - b.left) * (b.bottom - b.top) - i
    }

    companion object {
        private const val ACCURACY_THRESHOLD = 0.70f
    }
}