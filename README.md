[Screen_recording_20240619_092531_compressed.webm](https://github.com/AnbuNirsen/Geofence/assets/47245869/2ea0d6e1-0ddd-4021-9400-a93ea8878252)
# Geofense App 

This Application allows to track the user based on the given geofence boundry

To mark the Geofence long press on the map which will create the boundry of 100 meters radius

when the user enters into this boundry he will get notified about entry/dwell and exit scenarios

# Points to Note

* here Google maps, Geofence, DaggerHilt, Notification features are used

* To use Geofencing in Android 10 (API level 29) or higher we need ACCESS_BACKGROUND_LOCATION permission
  ref: https://developer.android.com/develop/sensors-and-location/location/geofencing#RequestGeofences

* With lastest android version Android 11,
  some security is added for location. Now to access background location,
  users have to manually “Allow all the time” in settings.

* ref: https://developer.android.com/develop/sensors-and-location/location/permissions#request-background-location

