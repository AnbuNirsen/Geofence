package com.example.geofenseappimplementation.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionUtils {

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasNotificationPermission(
        context: Context,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onSuccess()
        } else {
            onFailed()
        }
    }

    fun shouldShowRationalPermission(
        activity: Activity,
        permission: String,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            onSuccess()
        } else {
            onFailed()
        }
    }

    fun askForPermission(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }
}