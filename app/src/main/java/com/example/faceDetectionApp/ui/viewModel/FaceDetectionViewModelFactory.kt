package com.example.faceDetectionApp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.faceDetectionApp.data.repository.FaceDetectionRepository

class FaceDetectionViewModelFactory(
    private val repository: FaceDetectionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FaceDetectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FaceDetectionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
