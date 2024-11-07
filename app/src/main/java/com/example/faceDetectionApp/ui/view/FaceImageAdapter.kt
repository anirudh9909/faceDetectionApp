package com.example.faceDetectionApp.ui.view

import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.face.Face
import com.example.facedetectionapp.R

/**
 * Adapter for displaying images with detected faces in a RecyclerView.
 * Each face can have a bounding box and an associated tag drawn on the image.
 *
 * @param data List of bitmap and face pairs, where each bitmap represents an image,
 *             and each face represents a detected face in the bitmap.
 * @param faceTags Map of face IDs to tag strings, storing tags for each detected face.
 * @param onTagFace Callback function triggered when a face is clicked for tagging.
 */
class FaceImageAdapter(
    private val data: List<Pair<Bitmap, List<Face>>>,  // List of image and detected faces
    private val faceTags: Map<Int, String>,            // Tags for each face by unique ID
    private val onTagFace: (Face) -> Unit              // Callback to trigger tagging dialog
) : RecyclerView.Adapter<FaceImageAdapter.FaceViewHolder>() {

    /**
     * Inflates the item view and creates a ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return FaceViewHolder(view)
    }

    /**
     * Binds data to the ViewHolder by displaying the bitmap and drawing tags and bounding boxes.
     */
    override fun onBindViewHolder(holder: FaceViewHolder, position: Int) {
        val (bitmap, faces) = data[position]  // Get bitmap and detected faces for the position
        holder.bind(bitmap, faces)
    }

    /**
     * Returns the number of items in the data list.
     */
    override fun getItemCount() = data.size

    /**
     * ViewHolder class for holding and binding image data with face detection overlays.
     */
    inner class FaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        /**
         * Binds a bitmap and a list of detected faces to the ImageView.
         * Draws bounding boxes and tags for each face on the bitmap.
         *
         * @param bitmap The image bitmap where faces are detected.
         * @param faces The list of detected faces in the bitmap.
         */
        fun bind(bitmap: Bitmap, faces: List<Face>) {
            // Create a mutable bitmap copy to draw bounding boxes and tags
            val taggedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(taggedBitmap)

            // Paint configuration for drawing bounding boxes and text
            val paint = Paint().apply {
                color = Color.RED
                textSize = 60f
                typeface = Typeface.DEFAULT_BOLD
            }

            faces.forEach { face ->
                val boundingBox = face.boundingBox
                val tag = faceTags[face.trackingId ?: face.hashCode()] ?: ""

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 5f
                canvas.drawRect(boundingBox, paint)

                paint.style = Paint.Style.FILL
                canvas.drawText(tag, boundingBox.left.toFloat(), boundingBox.top.toFloat() - 10, paint)

                imageView.setOnClickListener {
                    onTagFace(face)
                }
            }

            imageView.setImageBitmap(taggedBitmap)
        }
    }
}