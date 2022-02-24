package gapara.co.id.feature.map

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import gapara.co.id.R

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var latLng : LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        latLng = LatLng(intent?.getStringExtra(EXTRA_LATITUDE)?.toDoubleOrNull() ?: 0.0,
            intent?.getStringExtra(EXTRA_LONGITUDE)?.toDoubleOrNull() ?: 0.0)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.isBuildingsEnabled = true
        googleMap.isTrafficEnabled = true
        // googleMap?.isMyLocationEnabled = true
        googleMap.uiSettings?.isZoomControlsEnabled = true
        googleMap.uiSettings?.isZoomGesturesEnabled = true
        googleMap.uiSettings?.isMyLocationButtonEnabled = true

        val latLng = LatLng(latLng?.latitude ?: 0.0, latLng?.longitude ?: 0.0)
        mMap.addMarker(MarkerOptions().position(latLng).title("Location check point"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
    }

    companion object {

        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"

        fun onNewIntent(context: Context, latLong : LatLng) : Intent {
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra(EXTRA_LATITUDE, "${latLong.latitude}")
            intent.putExtra(EXTRA_LONGITUDE, "${latLong.longitude}")
            return intent
        }
    }
}