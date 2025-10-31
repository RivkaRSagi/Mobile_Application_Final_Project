package com.example.wandermonsters

import android.Manifest
import android.content.pm.PackageManager
import java.util.concurrent.TimeUnit
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import android.location.*
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions

class MapActivity : AppCompatActivity(),  OnMapReadyCallback{

    private lateinit var mainMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private var circle: Circle? = null

    private val REQUEST_CODE_PERMISSION = 299
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.map_layout)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_PERMISSION
            )
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
    }

    override fun onMapReady(p0: GoogleMap) {
        mainMap = p0
        mainMap.isMyLocationEnabled = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_CODE_PERMISSION
            && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            //Permission granted do nothing.
        }else{
            val message = "Location permission is required for this application to run.\n Please enable this permission"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates(){
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.MILLISECONDS.toMillis(600)
            fastestInterval = TimeUnit.MILLISECONDS.toMillis(300)
            maxWaitTime = TimeUnit.MILLISECONDS.toMillis(20)
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                currentLocation = locationResult.lastLocation ?: return
                val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                mainMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19f))

                if(circle == null){
                    circle = mainMap.addCircle(
                        CircleOptions()
                            .center(currentLocation.let { LatLng(it!!.latitude, it.longitude) })
                            .radius(30.0)
                            .strokeColor(getColor(R.color.mapStroke))
                            .fillColor(getColor(R.color.mapCircle))
                            .strokeWidth(2f)
                    )
                }else{
                    circle?.center = latLng
                    circle?.radius = 30.0
                }
            }

        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }
}