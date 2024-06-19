package com.example.geofenseappimplementation.ui.geofense

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.geofenseappimplementation.R
import com.example.geofenseappimplementation.databinding.ActivityMapsBinding
import com.example.geofenseappimplementation.utils.GeoFenceUtils
import com.example.geofenseappimplementation.utils.PermissionUtils
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
* To use Geofencing in Android 10 (API level 29) or higher we need ACCESS_BACKGROUND_LOCATION permission
* ref: https://developer.android.com/develop/sensors-and-location/location/geofencing#RequestGeofences
*
* With lastest android version Android 11,
* some security is added for location. Now to access background location,
* users have to manually “Allow all the time” in settings.
* ref: https://developer.android.com/develop/sensors-and-location/location/permissions#request-background-location
* */

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var geofencingClient: GeofencingClient
    @Inject lateinit var geofenceUtils: GeoFenceUtils
    @Inject lateinit var permissionUtils: PermissionUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geofencingClient = LocationServices.getGeofencingClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val blr = LatLng(12.870815, 77.655122)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(blr, 18f))
        enableUserLocation()
        mMap.setOnMapLongClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    @SuppressLint("MissingPermission")
    private fun enableUserLocation() {
        if (permissionUtils.hasPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            permissionUtils.shouldShowRationalPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                {
                    showPermissionDescription(
                        getString(R.string.permission_needed),
                        getString(R.string.location_permission_needed)
                    ) {
                        permissionUtils.askForPermission(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            FINE_LOCATION_ACCESS_REQUEST_CODE
                        )
                    }
                },
                {
                    permissionUtils.askForPermission(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        FINE_LOCATION_ACCESS_REQUEST_CODE
                    )
                }
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (permissionUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    checkBackgroundLocationPermission()
                    mMap.isMyLocationEnabled = true
                }
            }
        }
        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (permissionUtils.hasPermission(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                ) {
                    Log.d(TAG, getString(R.string.add_geofence))
                    checkNotificationsPermission()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_permission_required),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, getString(R.string.notification_permission_granted))
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionUtils.shouldShowRationalPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS,
                        {
                            showPermissionDescription(
                                getString(R.string.permission_needed),
                                getString(R.string.notification_permission_description)
                            ) {
                                val settingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                                startActivity(settingsIntent)
                            }
                        },
                        {
                            Toast.makeText(
                                this,
                                getString(R.string.notification_permission_description),
                                Toast.LENGTH_LONG
                            ).show()
                        })
                }
            }
        }
    }

    private fun checkNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionUtils.hasNotificationPermission(
                this,
                {
                    Log.d(TAG, getString(R.string.notification_permission_already_given))
                },
                {
                    permissionUtils.askForPermission(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    )
                }
            )
        }
    }

    private fun checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!permissionUtils.hasPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            ) {
                askPermissionForBackgroundUsage();
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun askPermissionForBackgroundUsage() {
        permissionUtils.shouldShowRationalPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            {
                showPermissionDescription(
                    getString(R.string.permission_needed),
                    getString(R.string.background_location_permission_needed)
                ) {
                    permissionUtils.askForPermission(
                        this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
                    )
                }
            },
            {
                permissionUtils.askForPermission(
                    this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
                )
            }
        )

    }

    override fun onMapLongClick(latLng: LatLng) {
        mMap.clear()
        addMarker(latLng)
        addCircle(latLng)
        addGeoFence(latLng)
    }

    private fun addGeoFence(latLng: LatLng) {
        val geofence = geofenceUtils.getGeoFence(
            GEO_FENCE_ID,
            latLng,
            GEO_FENCE_RADIUS,
            GeofencingRequest.INITIAL_TRIGGER_ENTER or
                    GeofencingRequest.INITIAL_TRIGGER_DWELL or
                    GeofencingRequest.INITIAL_TRIGGER_EXIT
        )
        val geofencingRequest = geofenceUtils.geoFencingRequest(geofence)
        val pendingIntent = geofenceUtils.getPendingIntent(this)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    ContextCompat.getString(this, R.string.geofence_added),
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener {
                Log.e(TAG, "error: ${geofenceUtils.getExceptionMessage(this, it)}")
            }
    }

    private fun addCircle(latLng: LatLng) {
        val circleOptions = CircleOptions()
            .center(latLng)
            .radius(GEO_FENCE_RADIUS.toDouble())
            .strokeColor(Color.argb(255, 106, 90, 205))
            .fillColor(Color.argb(64, 106, 90, 205))
            .strokeWidth(4f)
        mMap.addCircle(circleOptions)
    }

    private fun addMarker(latLng: LatLng) {
        val marker = MarkerOptions().position(latLng)
        mMap.addMarker(marker)
    }

    private fun showPermissionDescription(
        title: String,
        message: String,
        askPermission: () -> Unit
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                askPermission()
            }
            .setNegativeButton("CANCEL") { dialog: DialogInterface, _ ->
                dialog.cancel()
            }
            .create().show()
    }

    companion object {
        const val FINE_LOCATION_ACCESS_REQUEST_CODE = 1001
        const val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1002
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1003
        const val GEO_FENCE_RADIUS = 100f
        const val GEO_FENCE_ID = "GEO_FENCE_ID_01"
        private const val TAG = "MapsActivity"

    }
}