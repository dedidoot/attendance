package gapara.co.id.core.base.location

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import gapara.co.id.core.api.log
import gapara.co.id.core.base.BaseApplication

class RequestLocation(var activity: Activity) {

    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null
    var currentLocation: Location? = null

    init {
        locationRequest = LocationHelper.generateLocationRequest()
        googleApiClient = LocationHelper.generateGoogleClient(activity = activity as FragmentActivity,
                connectionCallback = object : GoogleApiClient.ConnectionCallbacks {
                    override fun onConnected(p0: Bundle?) {
                         log("currentLocation ${currentLocation}")
                        requesting()
                    }

                    override fun onConnectionSuspended(p0: Int) {
                        googleApiClient?.connect()
                         log("test2 $p0")
                    }

                }, connectionFailedListener = GoogleApiClient.OnConnectionFailedListener { p0 -> log("test3 $p0") })

        googleApiClient?.connect()
        locationSettingsRequest()
    }

    private fun locationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(requestHighAccuracyLocation())
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build())
        result.addOnCompleteListener {
            try {
                it.getResult(ApiException::class.java)
                updatingLastLocation()
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(activity, LOCATION_CODE)
                    } catch (e: IntentSender.SendIntentException) {
                         log("error1 ${e.message}")
                    } catch (e: ClassCastException) {
                          log("error2 ${e.message}")
                    }
                }
            }
        }
    }

    private fun requestHighAccuracyLocation(): LocationRequest {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (30 * 1000).toLong()
        locationRequest.fastestInterval = (5 * 1000).toLong()
        return locationRequest
    }

    private fun updatingLastLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            log("permission location failed!")
            return
        }
        if (googleApiClient == null) { return }
        val lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        if (lastLocation != null) {
            currentLocation = lastLocation
            log("test8 $currentLocation")
            saveLocalStorageLocation()
        } else {
             log("test9")
            when {
                googleApiClient?.isConnected == true -> {
                     log("test11")
                    requesting()
                }

                googleApiClient?.isConnected == false -> {
                     log("test5")
                    googleApiClient?.connect()
                }
            }
        }
    }

    private fun requesting() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            log("permission location failed")
            return
        }
        if (googleApiClient == null) { return }
        LocationServices.FusedLocationApi
                    .requestLocationUpdates(googleApiClient, requestHighAccuracyLocation(), object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            currentLocation = location
                            saveLocalStorageLocation()
                            if (googleApiClient == null) { return }
                            if (googleApiClient?.isConnected == false) { return }
                            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
                        }
                    })
    }

    private fun saveLocalStorageLocation() {
        BaseApplication.sessionManager?.latitude = "${currentLocation?.latitude}"
        BaseApplication.sessionManager?.longitude = "${currentLocation?.longitude}"
        log("currentLocation $currentLocation")
    }

    companion object {
        const val LOCATION_CODE = 34
    }
}