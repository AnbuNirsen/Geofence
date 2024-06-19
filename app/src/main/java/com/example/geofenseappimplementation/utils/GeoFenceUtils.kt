package com.example.geofenseappimplementation.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.geofenseappimplementation.R
import com.example.geofenseappimplementation.ui.receiver.GeoFenceBroadcastReceiver
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest

class GeoFenceUtils {
    fun getGeoFence(
        id: String,
        latLng: LatLng,
        radius: Float,
        transitionTypes: Int
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(5000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    fun geoFencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
    }

    fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, GeoFenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    fun getExceptionMessage(context: Context, e: Exception): String {
        if (e is ApiException) {
            val apiException: ApiException = e
            return when (apiException.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> context.getString(R.string.geofence_service_not_available)
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> context.getString(R.string.too_many_geofences)
                GeofenceStatusCodes.GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION -> context.getString(R.string.location_permission_not_given)
                else -> e.message.toString()
            }
        }
        return e.message.toString()
    }


}