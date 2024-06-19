package com.example.geofenseappimplementation.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.geofenseappimplementation.ui.geofense.MapsActivity
import com.example.geofenseappimplementation.utils.NotificationUtils
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeoFenceBroadcastReceiver : BroadcastReceiver() {
    private val notificationUtils = NotificationUtils()
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null || (geofencingEvent.hasError())) {
            Log.e(TAG, "onReceive: Error receiving Geofence event")
            return
        }
        val geofenceList = geofencingEvent.triggeringGeofences
        geofenceList?.forEach {
            Log.d(TAG, "GeofenceId: ${it.requestId}")
        }
        val transititonType = geofencingEvent.geofenceTransition

        when (transititonType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                notificationUtils.sendGeofenceNotification(context,"GEOFENCE_TRANSITION_ENTER",MapsActivity::class.java)
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show()
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                notificationUtils.sendGeofenceNotification(context,"GEOFENCE_TRANSITION_DWELL",MapsActivity::class.java)
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show()
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                notificationUtils.sendGeofenceNotification(context,"GEOFENCE_TRANSITION_EXIT",MapsActivity::class.java)
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show()
            }
        }


    }

    companion object {
        private const val TAG = "GeoFenceBroadcastReceiv"
    }
}