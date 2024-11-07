package com.example.faceDetectionApp.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.tasks.await

class FaceDetectionRepository(private val contentResolver: ContentResolver) {

    private val detectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(detectorOptions)

    fun loadImages(): List<Bitmap> {
        val images = mutableListOf<Bitmap>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext() && images.size < 30) {  // Limit to 30 images
                val id = it.getLong(idColumn)
                val imageUri = ContentUris.withAppendedId(uri, id)

                try {
                    contentResolver.openInputStream(imageUri)?.use { inputStream ->
                        // Set decoding options for larger images
                        val options = BitmapFactory.Options()
                            .apply { inSampleSize = 2 } // Adjust inSampleSize as needed
                        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)

                        if (bitmap != null) {
                            images.add(bitmap)
                            Log.d(
                                "FaceDetectionRepo",
                                "Bitmap loaded successfully for URI: $imageUri"
                            )
                        } else {
                            Log.e(
                                "FaceDetectionRepo",
                                "Bitmap decoding failed for image URI: $imageUri"
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("FaceDetectionRepo", "Failed to load image with ID: $id", e)
                }
            }
        }
        Log.d("FaceDetectionRepo", "Total images loaded: ${images.size}")
        return images
    }

    suspend fun detectFaces(bitmap: Bitmap): List<Face> {
        val image = InputImage.fromBitmap(bitmap, 0)
        return detector.process(image).await()
    }
}