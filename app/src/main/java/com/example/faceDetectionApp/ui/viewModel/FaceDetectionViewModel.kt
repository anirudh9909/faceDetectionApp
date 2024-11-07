package com.example.faceDetectionApp.ui.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.faceDetectionApp.data.repository.FaceDetectionRepository
import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FaceDetectionViewModel(private val repository: FaceDetectionRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _imagesWithFaces = MutableLiveData<List<Pair<Bitmap, List<Face>>>>()
    val imagesWithFaces: LiveData<List<Pair<Bitmap, List<Face>>>> get() = _imagesWithFaces

    /**
     * Loads images and detects faces asynchronously.
     */
    fun loadAndProcessImages() {
        _isLoading.value = true
        viewModelScope.launch {
            val results = withContext(Dispatchers.IO) {
                repository.loadImages().map { bitmap ->
                    bitmap to repository.detectFaces(bitmap)
                }
            }
            _imagesWithFaces.value = results
            _isLoading.value = false
        }
    }
}