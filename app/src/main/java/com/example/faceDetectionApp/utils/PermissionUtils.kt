package com.example.faceDetectionApp.utils

import android.Manifest
import android.os.Build
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object PermissionUtils {

    /**
     * Checks and requests necessary permissions based on Android version.
     * Calls onPermissionsGranted if all permissions are granted.
     */
    fun checkAndRequestPermissions(
        activity: Activity,
        launcher: ActivityResultLauncher<Array<String>>,
        onPermissionsGranted: () -> Unit
    ) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                launcher.launch(arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES))
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                if (Environment.isExternalStorageManager()) {
                    onPermissionsGranted()
                } else {
                    showStoragePermissionDialog(activity)
                }
            }
            else -> {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    onPermissionsGranted()
                } else {
                    launcher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                }
            }
        }
    }

    /**
     * Shows a dialog to prompt the user to go to settings for storage access.
     */
    private fun showStoragePermissionDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("Storage Permission Required")
            .setMessage("This app needs full storage access to display images. Please grant access in settings.")
            .setPositiveButton("Go to Settings") { _, _ -> openAppSettings(activity) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Opens the app's settings page to grant full storage access.
     */
    fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }
}