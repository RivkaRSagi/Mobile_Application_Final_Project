package com.example.wandermonsters

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
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
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import kotlin.math.nextDown
import androidx.core.graphics.createBitmap

class MapActivity : AppCompatActivity(),  OnMapReadyCallback{

    private lateinit var mainMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private var circle: Circle? = null
    private val monsterEvents = ArrayList<Marker>()

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

        mainMap.setOnMarkerClickListener { marker ->
            if (marker.title == "monster_event") {
                Toast.makeText(this, "Monster Event Clicked!", Toast.LENGTH_SHORT).show()
                monsterEvents.remove(marker)
                marker.remove()
            }
            true
        }


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
                val maxDistance = 30.0

                if (circle == null) {
                    circle = mainMap.addCircle(
                        CircleOptions()
                            .center(currentLocation.let { LatLng(it!!.latitude, it.longitude) })
                            .radius(maxDistance)
                            .strokeColor(getColor(R.color.mapStroke))
                            .fillColor(getColor(R.color.mapCircle))
                            .strokeWidth(2f)
                    )
                } else {
                    circle?.center = latLng
                    circle?.radius = maxDistance
                }

                if (monsterEvents.size < 5) {
                    val eventLocation = generateEventIcons(latLng, 30.0)
                    val event = mainMap.addMarker(
                        MarkerOptions()
                            .position(eventLocation)
                            .title("monster_event")
                            .icon(getBitmapFromVector(applicationContext, R.drawable.marker_icon))
                    )

                    monsterEvents.add(event!!)
                }

                monsterEvents.forEach { marker ->
                    val distance = FloatArray(1)
                    Location.distanceBetween(currentLocation!!.latitude, currentLocation!!.longitude, marker.position.latitude, marker.position.longitude, distance)
                    if (distance[0] > maxDistance) {
                        marker.remove()
                        monsterEvents.remove(marker)
                    }
                }
            }

        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun generateEventIcons(location: LatLng, radius: Double): LatLng{
        val random = java.util.Random()
        val radiusDeg = radius / 111000f // this is a rough conversion

        val u = random.nextDouble()
        val v = random.nextDouble()
        val w = radiusDeg * Math.sqrt(u)
        val t = 2 * Math.PI * v
        val latOff = w * Math.cos(t)
        val lngOff = w * Math.sin(t) / Math.cos(Math.toRadians(location.latitude))

        return LatLng(location.latitude + latOff, location.longitude + lngOff)
    }

    //AI generated function to convert a vector drawable to a Bitmap. This is required for the google maps marker icon.
    fun getBitmapFromVector(context: Context, drawableId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, drawableId)!!
        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}