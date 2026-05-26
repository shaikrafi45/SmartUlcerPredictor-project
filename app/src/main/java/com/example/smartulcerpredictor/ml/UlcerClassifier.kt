package com.example.smartulcerpredictor.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.model.Model
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.*

class UlcerClassifier(context: Context) {

    private val model: Model = Model.createModel(context, "ulcer_model.tflite")
    private val labels: List<String> = FileUtil.loadLabels(context, "labels.txt")

    private fun isWoundImage(bitmap: Bitmap): Boolean {
        val scaled = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        var skinWoundCount = 0
        var activeWoundCount = 0
        val total = 10000.0f
        
        val hsv = FloatArray(3)
        for (y in 0 until 100) {
            for (x in 0 until 100) {
                val color = scaled.getPixel(x, y)
                android.graphics.Color.colorToHSV(color, hsv)
                val h = hsv[0]
                val s = hsv[1]
                val v = hsv[2]
                
                val isSkinOrWound = (h in 0f..52f || h in 330f..360f) && 
                                    (s in 0.12f..0.70f) && 
                                    (v in 0.12f..0.95f)
                                    
                if (isSkinOrWound) {
                    skinWoundCount++
                    
                    val isGranulation = (h in 0f..14f || h in 340f..360f) && (s >= 0.28f) && (v >= 0.22f)
                    val isSlough = (h in 28.0f..55f) && (s in 0.15f..0.52f) && (v >= 0.48f)
                    val isNecrotic = (v <= 0.2f) && (s >= 0.08f) && (h in 0f..50f || h in 330f..360f)
                    
                    if (isGranulation || isSlough || isNecrotic) {
                        activeWoundCount++
                    }
                }
            }
        }
        
        val skinRatio = skinWoundCount / total
        val woundRatio = activeWoundCount / total
        return skinRatio >= 0.35f && woundRatio >= 0.015f
    }

    fun classify(bitmap: Bitmap): List<Category> {
        if (!isWoundImage(bitmap)) {
            return listOf(Category("Unable to Identify", 0.0f))
        }
        val inputTensor = model.getInputTensor(0)
        val inputShape = inputTensor.shape() // e.g. [1, 224, 224, 3]
        val inputWidth = inputShape[1]
        val inputHeight = inputShape[2]

        val dynamicImageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(inputWidth, inputHeight, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(127.5f, 127.5f)) // Map [0, 255] to [-1, 1]
            .build()

        var tensorImage = TensorImage(inputTensor.dataType())
        tensorImage.load(bitmap)
        tensorImage = dynamicImageProcessor.process(tensorImage)

        val outputTensor = model.getOutputTensor(0)
        val outputBuffer = TensorBuffer.createFixedSize(outputTensor.shape(), outputTensor.dataType())

        model.run(arrayOf(tensorImage.buffer), mapOf(0 to outputBuffer.buffer))

        val probabilities = outputBuffer.floatArray
        val categoryList = mutableListOf<Category>()
        
        val size = minOf(labels.size, probabilities.size)
        for (i in 0 until size) {
            // Some models might output raw logits, others output softmax probabilities.
            // In case of raw logits or large values, we clamp to 0.0 - 1.0 for score percentage.
            val score = probabilities[i].coerceIn(0.0f, 1.0f)
            categoryList.add(Category(labels[i], score))
        }

        return categoryList.sortedByDescending { it.score }
    }

    fun close() {
        model.close()
    }
}
