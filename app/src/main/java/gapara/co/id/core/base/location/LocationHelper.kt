package gapara.co.id.core.base.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.ReportRequest
import gapara.co.id.core.base.BaseDialogView
import kotlinx.coroutines.launch
import kotlin.random.Random

object LocationHelper : CoroutineDeclare {

    const val KEY_REQUEST_FINE_LOCATION = 102

    fun showRequestPermission(activity: Activity) {
        if (isShouldShowRequestPermission(activity)) {
            val baseDialog = BaseDialogView(activity).setMessage("Izinkan aplikasi mengakses fitur ini?")
                .setPositiveString("Iya")
                .setCancelable(false)
                .setOnClickPositive {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), KEY_REQUEST_FINE_LOCATION)
                }

            baseDialog.show()
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), KEY_REQUEST_FINE_LOCATION)
        }
    }

    fun isShouldShowRequestPermission(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun isGrantedLocationPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun isGrantedOnResult(grantResults: IntArray): Boolean {
        return grantResults.count() > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    fun generateLocationRequest(): LocationRequest {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1 * 1000)
                .setFastestInterval(1 * 1000)

    }

    fun generateGoogleClient(activity: FragmentActivity, connectionCallback: GoogleApiClient.ConnectionCallbacks, connectionFailedListener: GoogleApiClient.OnConnectionFailedListener): GoogleApiClient? {
        var clientId = (System.currentTimeMillis() / 1000).toInt()

        try {
            clientId = Random.nextInt(clientId, Int.MAX_VALUE)
            return GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(connectionCallback)
                .enableAutoManage(activity, clientId, connectionFailedListener)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(LocationServices.API)
                .build()
        } catch (e : Exception) {
            launch { ReportRequest.postWithDeviceDetail("$e ${e.message}") }
            e.printStackTrace()
            return null
        }
    }
}