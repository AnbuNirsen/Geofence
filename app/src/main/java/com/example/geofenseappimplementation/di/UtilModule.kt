package com.example.geofenseappimplementation.di

import com.example.geofenseappimplementation.utils.GeoFenceUtils
import com.example.geofenseappimplementation.utils.NotificationUtils
import com.example.geofenseappimplementation.utils.PermissionUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object UtilModule {

    @Provides
    @ActivityScoped
    fun provideGeofenceUtils(): GeoFenceUtils {
        return GeoFenceUtils()
    }

    @Provides
    @ActivityScoped
    fun provideNotificationUtils(): NotificationUtils {
        return NotificationUtils()
    }

    @Provides
    @ActivityScoped
    fun providePermissionUtils(): PermissionUtils {
        return PermissionUtils()
    }

}