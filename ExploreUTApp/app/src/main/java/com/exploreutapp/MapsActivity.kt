//package com.exploreutapp

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.OnMapReadyCallback

//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.Location
//import android.os.Build
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.os.Looper
//import android.util.Log
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat.checkSelfPermission
//import com.exploreutapp.model.Places
//import com.exploreutapp.remote.IExploreUTService
//import com.google.android.gms.location.*
//import com.google.android.gms.maps.*
//
//import com.google.android.gms.maps.model.*
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import io.reactivex.schedulers.Schedulers
//import kotlinx.android.synthetic.main.activity_maps.*
//import org.json.JSONException
//import java.io.Serializable
//
//
//class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
//
//    private lateinit var mMap: GoogleMap
//    private var latitude: Double=0.toDouble()
//    private var longitude: Double=0.toDouble()
//
//    private lateinit var mLastLocation:Location
//    private var mMarker: Marker?=null
//
//    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//    lateinit var locationRequest: LocationRequest
//    lateinit var locationCallback: LocationCallback
//
//    companion object {
//        private const val MY_PERMISSION_CODE: Int = 1000
//        val mService by lazy {
//            IExploreUTService.create()
//        }
//    }
//
//
//    private var currentPlaces: ArrayList<Places> = ArrayList()
//    var currentResult:Places?=null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_maps)
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkLocationPermission()) {
//                buildLocationRequest()
//                buildLocationCallBack()
//
//                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//                fusedLocationProviderClient.requestLocationUpdates(
//                    locationRequest,
//                    locationCallback,
//                    Looper.myLooper()
//                )
//            }
//        }
//        else {
//            buildLocationRequest()
//            buildLocationCallBack()
//
//            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//            fusedLocationProviderClient.requestLocationUpdates(
//                locationRequest,
//                locationCallback,
//                Looper.myLooper()
//            )
//        }
//
//
//        bottom_navigation_view.setOnNavigationItemSelectedListener { item->
//            when(item.itemId) {
//                R.id.action_activity -> nearByPlace("Activity")
//                R.id.action_library -> nearByPlace("Study")
//                R.id.action_building -> nearByPlace("Building")
//                R.id.action_view -> nearByPlace("Monument")
//            }
//            true
//        }
//    }
//
//    private fun nearByPlace(place_theme: String) {
//
//        // Clear all marker on Map
//        mMap.clear()
//
//
//
//        var disposable: Disposable? = mService.getThemePlaces()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe (this::handleResponse, this::handleError)
//
//
//        if (currentPlaces != null) {
//
//            for (i in 0 until currentPlaces.size) {
//                val markerOptions = MarkerOptions()
//                val utPlace = currentPlaces[i]
//
//                // making sure the place we are looking at does have location
//                if (utPlace.location != null && utPlace.location != null) {
//
//                    val placeTheme = utPlace.theme
//                    val placeName = utPlace.name
//                    val lat = utPlace.location!!.lat
//                    val lng = utPlace.location!!.lng
//
////                    println(lat.toString())
////                    println(lng.toString())
//
//                    val latLng = LatLng(lat,lng)
//
//                    markerOptions.position(latLng)
//                    markerOptions.title(placeName)
//                    if (place_theme == placeTheme && place_theme == "Activity") {
//                        markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_activity)))
//                    } else if (place_theme == placeTheme && place_theme == "Study") {
//                        markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_library)))
//                    } else if (place_theme == placeTheme && place_theme == "Building") {
//                        markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_building)))
//                    } else if (place_theme == placeTheme && place_theme == "Monument") {
//                        markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_view)))
//                    } else {
//                        markerOptions.icon(
//                            BitmapDescriptorFactory.defaultMarker(
//                                BitmapDescriptorFactory.HUE_GREEN
//                            )
//                        )
//                    }
//                    markerOptions.snippet(i.toString()) // Assign index for Marker
//                    // Add marker to map
//                    mMap.addMarker(markerOptions)
//
//                    // Move camera
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
//                }
//            }
//        }
//    }
//
//    // handle the response with an arraylist of places
//    private fun handleResponse(response: ArrayList<Places>) {
//        try {
//            currentPlaces = response
//
//            for(r in response) {
//                Log.d("myTag", r._id)
//                Log.d("myTag", r.name)
//                Log.d("myTag", r.theme)
//                Log.d("myTag", r.tags.toString())
//                Log.d("myTag", r.address)
//                Log.d("myTag", r.intro)
//                Log.d("myTag", r.reviews.toString())
//                Log.d("myTag", r.location!!.lat.toString())
//                Log.d("myTag", r.location!!.lng.toString())
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//            Log.d("myTag", "No valid json")
//        }
//        Log.d("myTag", "Done")
//    }
//
//    private fun handleError(error: Throwable) {
//        Log.d("myTag", error.localizedMessage!!)
//    }
//
//
//    private fun buildLocationCallBack() {
//        locationCallback = object: LocationCallback() {
//            override fun onLocationResult(p0: LocationResult?) {
//                mLastLocation = p0!!.locations[p0.locations.size-1]
//
//                if(mMarker != null) {
//                    mMarker!!.remove()
//                }
//                latitude = mLastLocation.latitude
//                longitude = mLastLocation.longitude
//
//                val latLng = LatLng(latitude, longitude)
//                val markerOptions = MarkerOptions()
//                    .position(latLng)
//                    .title("Your position")
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//
//                mMarker = mMap.addMarker(markerOptions)
//
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//                mMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
//
//            }
//        }
//    }
//
//    private fun buildLocationRequest() {
//        locationRequest = LocationRequest()
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest.interval = 5000
//        locationRequest.fastestInterval = 3000
//        locationRequest.smallestDisplacement = 10f
//    }
//
//    private fun checkLocationPermission(): Boolean {
//        if(checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                ActivityCompat.requestPermissions(
//                    this, arrayOf(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                    ), MY_PERMISSION_CODE
//                )
//            } else ActivityCompat.requestPermissions(
//                this, arrayOf(
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                ), MY_PERMISSION_CODE
//            )
//            return false
//        } else
//            return true
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        when(requestCode) {
//            MY_PERMISSION_CODE ->{
//                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if(checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        if (checkLocationPermission()) {
//                            buildLocationRequest()
//                            buildLocationCallBack()
//
//                            fusedLocationProviderClient =
//                                LocationServices.getFusedLocationProviderClient(this)
//                            fusedLocationProviderClient.requestLocationUpdates(
//                                locationRequest,
//                                locationCallback,
//                                Looper.myLooper()
//                            )
//
//                            mMap.isMyLocationEnabled = true
//                        }
//                    }
//                } else {
//                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//
//    }
//
//
//    override fun onStop() {
//
//        //remove current location information when stop app
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
//        super.onStop()
//    }
//
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                mMap.isMyLocationEnabled = true
//            }
//        }
//        else
//            mMap.isMyLocationEnabled = true
//
//        // make event clickable on markers
//
//        mMap.setOnMarkerClickListener { marker ->
//            // when user select marker, just get result of place assign to static variable
//            currentResult = currentPlaces!![Integer.parseInt(marker.snippet)]
//
//            val viewIntent = Intent(this@MapsActivity, ViewPlace::class.java)
//            // start new activity
//            viewIntent.putExtra("place_to_show", currentResult as Serializable)
//            startActivity(viewIntent)
//            true
//        }
//
//        mMap.uiSettings.isZoomControlsEnabled = true
//        mMap.uiSettings.isCompassEnabled = true
//        mMap.uiSettings.setAllGesturesEnabled(true)
//    }
//}
