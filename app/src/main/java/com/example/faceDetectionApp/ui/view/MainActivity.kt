package com.example.faceDetectionApp.ui.view

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.faceDetectionApp.data.repository.FaceDetectionRepository
import com.example.faceDetectionApp.ui.viewModel.FaceDetectionViewModel
import com.example.faceDetectionApp.ui.viewModel.FaceDetectionViewModelFactory
import com.example.faceDetectionApp.utils.PermissionUtils
import com.example.facedetectionapp.R
import com.google.mlkit.vision.face.Face

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: FaceDetectionViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var permissionMessage: TextView
    private lateinit var permissionButton: TextView

    private val faceTags = mutableMapOf<Int, String>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        handlePermissionResults(permissions)
    }

    /**
     * Called when the activity is created. Sets up the UI, ViewModel, and permissions.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
        setupViewModel()

        PermissionUtils.checkAndRequestPermissions(this, requestPermissionLauncher) {
            loadImagesAndObserveViewModel()
        }
    }

    /**
     * Sets up the toolbar, RecyclerView, and permission-related UI components.
     */
    private fun setupUI() {
        // Setting up toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Face Detection App"

        // Initializing UI components
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        permissionMessage = findViewById(R.id.permissionMessage)
        permissionButton = findViewById(R.id.permissionButton)

        // Setting up button to navigate to app settings if permissions are denied
        permissionButton.setOnClickListener {
            PermissionUtils.openAppSettings(this)
        }
    }

    /**
     * Initializes the ViewModel with a factory, allowing dependency injection of the repository.
     */
    private fun setupViewModel() {
        val factory =
            FaceDetectionViewModelFactory(FaceDetectionRepository(application.contentResolver))
        viewModel = ViewModelProvider(this, factory).get(FaceDetectionViewModel::class.java)
    }

    /**
     * Loads images and sets up LiveData observers for UI updates.
     * Observers are set up to handle loading states and display detected faces.
     */
    private fun loadImagesAndObserveViewModel() {
        observeViewModel()
        viewModel.loadAndProcessImages() // Initiates image loading and face detection
    }

    /**
     * Observes LiveData from ViewModel to update the UI based on loading state and face data.
     */
    private fun observeViewModel() {
        // Observes the loading state to toggle the progress bar visibility
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.imagesWithFaces.observe(this) { results ->
            recyclerView.adapter = FaceImageAdapter(results, faceTags) { face ->
                promptForTag(face)
            }
        }
    }

    /**
     * Opens a dialog to prompt the user for a tag for the given face.
     * Saves the tag in the faceTags map, using the face's unique ID as the key.
     */
    private fun promptForTag(face: Face) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Tag Face")

        val input = EditText(this).apply { hint = "Enter name" }
        dialog.setView(input)

        dialog.setPositiveButton("OK") { _, _ ->
            val tagName = input.text.toString().trim()
            if (tagName.isNotEmpty()) {
                faceTags[face.trackingId ?: face.hashCode()] = tagName
                recyclerView.adapter?.notifyDataSetChanged() // Refresh adapter to display new tag
                Toast.makeText(this, "Tagged face as $tagName", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setNegativeButton("Cancel", null)
        dialog.show()
    }

    /**
     * Handles the results of permission requests.
     * If all permissions are granted, it proceeds with loading images.
     * If any permission is denied, it shows a message and button to open settings.
     */
    private fun handlePermissionResults(permissions: Map<String, Boolean>) {
        if (permissions.all { it.value }) {
            loadImagesAndObserveViewModel()
        } else {
            showPermissionDeniedMessage()
        }
    }

    /**
     * Displays a message and button when permissions are denied, prompting the user to enable them.
     */
    private fun showPermissionDeniedMessage() {
        permissionMessage.visibility = View.VISIBLE
        permissionButton.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
    }
}