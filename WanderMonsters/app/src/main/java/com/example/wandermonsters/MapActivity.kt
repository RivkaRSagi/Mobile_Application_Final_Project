package com.example.wandermonsters

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import android.location.*
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.animation.LinearInterpolator
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
import androidx.core.graphics.createBitmap
import java.io.File
import kotlin.random.Random

class MapActivity : AppCompatActivity(),  OnMapReadyCallback{

    private lateinit var mainMap: GoogleMap
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var circle: Circle? = null
    private val monsterEvents = ArrayList<Marker>()
    private var userMarker: Marker? = null

    private val maxDistance = 30.0
    private val zoomLevel = 19f

    private val REQUEST_CODE_PERMISSION = 299
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.map_layout)

        Thread.setDefaultUncaughtExceptionHandler(CustomExceptionHandler())

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_PERMISSION
            )
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    override fun onPause() {
        super.onPause()

        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){
            startLocationUpdates()
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onMapReady(p0: GoogleMap) {
        mainMap = p0
        mainMap.isBuildingsEnabled = false

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            startLocationUpdates()
            fusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mainMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
                    userMarker = mainMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title("user_marker")
                            .icon(
                                getBitmapFromVector(
                                    applicationContext,
                                    R.drawable.user_location
                                )
                            )
                    )
                    circle = mainMap.addCircle(
                        CircleOptions()
                            .center(latLng)
                            .radius(maxDistance)
                            .strokeColor(getColor(R.color.mapStroke))
                            .fillColor(getColor(R.color.mapCircle))
                            .strokeWidth(2f)
                    )
                }
            }

            mainMap.setOnMarkerClickListener { marker ->
                if (marker.title == "monster_event") {
                    monsterEvents.remove(marker)
                    val intent = Intent(this@MapActivity, MiniGameActivity::class.java)

                    marker.remove()
                    startActivity(intent)
                }
                true
            }
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
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }else{
            val message = "Location permission is required for this application to run.\n Please enable this permission"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates(){
        locationRequest = LocationRequest.create().apply {
            interval = 500
            fastestInterval = 100
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mainMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel), 1000, null)


                    if (circle == null) {
                        circle = mainMap.addCircle(
                            CircleOptions()
                                .center(latLng)
                                .radius(maxDistance)
                                .strokeColor(getColor(R.color.mapStroke))
                                .fillColor(getColor(R.color.mapCircle))
                                .strokeWidth(2f)
                        )
                    }

                    if(userMarker == null){
                        userMarker = mainMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title("user_marker")
                                .icon(
                                    getBitmapFromVector(
                                        applicationContext,
                                        R.drawable.user_location
                                    )
                                )
                        )
                    }

                    animateMarker(userMarker!!, latLng)

                    if (monsterEvents.size < 5) {
                        val eventLocation = generateEventIcons(latLng, maxDistance - 6)

                        val random = Random.nextInt(1, 100)
                        if (random >= 1 && random <= 25) {
                            val event = mainMap.addMarker(
                                MarkerOptions()
                                    .position(eventLocation)
                                    .title("monster_event")
                                    .icon(
                                        getBitmapFromVector(
                                            applicationContext,
                                            R.drawable.marker_icon
                                        )
                                    )
                            )
                            monsterEvents.add(event!!)
                        }
                    }


                    val iterator = monsterEvents.iterator()
                    while (iterator.hasNext()) {
                        val marker = iterator.next()
                        val distance = FloatArray(1)

                        Location.distanceBetween(location.latitude, location.longitude, marker.position.latitude, marker.position.longitude, distance)

                        if (distance[0] > maxDistance) {
                            iterator.remove()
                            marker.remove()
                        }
                    }
                }
            }

        }

        fusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

//    fun animateMarker(marker: Circle, toPosition: LatLng) {
//        val handler = Handler(Looper.getMainLooper())
//        val start = SystemClock.uptimeMillis()
//        val duration = 1000L
//        val startLatLng = marker.center
//
//        val interpolator = LinearInterpolator()
//
//        handler.post(object : Runnable {
//            override fun run() {
//                val elapsed = SystemClock.uptimeMillis() - start
//                val t = (elapsed.toFloat() / duration).coerceAtMost(1f)
//                val lat = (toPosition.latitude - startLatLng.latitude) * t + startLatLng.latitude
//                val lng = (toPosition.longitude - startLatLng.longitude) * t + startLatLng.longitude
//                val newLocation = LatLng(lat, lng)
//                marker.center = newLocation
//                mainMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation))
//
//                if (t < 1f) {
//                    handler.postDelayed(this, 16)
//                }
//            }
//        })
//    }

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

    fun animateMarker(marker: Marker, toPosition: LatLng) {
        val handler = Handler(Looper.getMainLooper())
        val start = SystemClock.uptimeMillis()
        val duration = 1000L
        val startLatLng = marker.position

        val interpolator = LinearInterpolator()

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t = (elapsed.toFloat() / duration).coerceAtMost(1f)
                val lat = (toPosition.latitude - startLatLng.latitude) * t + startLatLng.latitude
                val lng = (toPosition.longitude - startLatLng.longitude) * t + startLatLng.longitude
                val latLng = LatLng(lat, lng)
                marker.position = latLng
                circle!!.center = latLng

                if (t < 1f) {
                    handler.postDelayed(this, 16)
                }
            }
        })
    }

    private inner class CustomExceptionHandler : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(thread: Thread, throwable: Throwable) {
            try {
                val context = this@MapActivity
                val dir = context.getExternalFilesDir(null)
                val file = File(dir, "Map_dump.txt")
                val text = throwable.stackTraceToString()
                file.writeText(text)
                Toast.makeText(this@MapActivity, "Stack trace saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MapActivity, "Error saving stack trace to dump file", Toast.LENGTH_SHORT).show()
            }finally {
                val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }

}