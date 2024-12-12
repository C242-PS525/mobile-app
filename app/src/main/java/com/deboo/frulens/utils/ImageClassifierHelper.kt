package com.deboo.frulens.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.deboo.frulens.R
import com.deboo.frulens.ui.analyze.AnalyzeFragment
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.io.IOException

class ImageClassifierHelper(
    private var threshold: Float = 0.1f,
    private var maxResults: Int = 3,
    private val modelName: String = "model86.tflite",
    val context: Context,
    val classifierListener: AnalyzeFragment,
) {

    private var imageClassifier: ImageClassifier? = null


    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        } catch (e: IOException) {
            classifierListener.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, "Image classifier setup failed: ${e.message}", e)
        }

    }

    fun classifyStaticImage(imageUri: Uri) {

        if (imageClassifier == null) {
            classifierListener.onError(context.getString(R.string.image_classifier_not_initialized))
            return
        }

        try {
            // Load bitmap from the URI
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Create TensorImage from Bitmap
            val tensorImage = TensorImage.fromBitmap(bitmap)


            // Measure inference time
            var inferenceTime = SystemClock.uptimeMillis()
            val results = imageClassifier?.classify(tensorImage)
            inferenceTime = SystemClock.uptimeMillis() - inferenceTime
            classifierListener.onResults(
                results,
                inferenceTime
            )

        } catch (e: Exception) {
            classifierListener.onError("Failed to classify image: ${e.message}")
            Log.e(TAG, "Error classifying image", e)
        }
    }
    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}