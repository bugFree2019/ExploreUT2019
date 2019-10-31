package com.example.view

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.view.Common.Common
import com.example.view.Model.MyPlaces
import com.example.view.Remote.IGoogleAPIService
import com.google.android.gms.location.*
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var latitude: Double=0.toDouble()
    private var longitude: Double=0.toDouble()

    private lateinit var mLastLocation:Location
    private var mMarker: Marker?=null

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    companion object {
        private const val MY_PERMISSION_CODE: Int = 1000
    }

    lateinit var mService: IGoogleAPIService

    internal lateinit var currentPlace: MyPlaces


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Init Service
        mService = Common.googleApiService

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                buildLocationRequest()
                buildLocationCallBack()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            }
        }
        else {
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }
        bottom_navigation_view.setOnNavigationItemReselectedListener { item->
            when(item.itemId) {
                R.id.action_fitness -> nearByPlace("fitness")
                R.id.action_library -> nearByPlace("library")
                R.id.action_school -> nearByPlace("school")
                R.id.action_view -> nearByPlace("street view")
            }

        }
    }

    private fun nearByPlace(typePlace: String) {

        // Clear all marker on Map
        mMap.clear()
        val url = getUrl(latitude,longitude,typePlace)

        mService.getNearbyPlaces(url)
            .enqueue(object : Callback<MyPlaces> {
                override fun onResponse(call: Call<MyPlaces>?, response: Response<MyPlaces>?) {
                    currentPlace = response!!.body()!!

                    if(response.isSuccessful) {
                        for (i in 0 until response!!.body()!!.results!!.size) {
                            val markerOptions = MarkerOptions()
                            val googlePlace = response.body()!!.results!![i]
                            val lat = googlePlace.geometry!!.location!!.lat
                            val lng = googlePlace.geometry!!.location!!.lng
                            val placeName = googlePlace.name
                            val latLng = LatLng(lat,lng)

                            markerOptions.position(latLng)
                            markerOptions.title(placeName)
                            if(typePlace.equals("fitness")) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_fitness)))
                            } else if(typePlace.equals("library")) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_library)))
                            } else if (typePlace.equals("school")) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_school)))
                            } else if (typePlace.equals("street view")) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_view)))
                            } else {
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            }
                            markerOptions.snippet(i.toString()) // Assign index for Marker
                            // Add marker to map
                            mMap!!.addMarker(markerOptions)

                            // Move camera
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                            mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f))

                        }


                    }
                }

                override fun onFailure (call: Call<MyPlaces>?, t: Throwable?) {
                    Toast.makeText(baseContext,""+t!!.message, Toast.LENGTH_SHORT).show()
                }
            })


    }

    private fun getUrl(latitude: Double, longitude: Double, typePlace: String): String {

        val googlePlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        googlePlaceUrl.append("?location=$latitude,$longitude")
        googlePlaceUrl.append("&radius=10000")
        googlePlaceUrl.append("&type=$typePlace")
        googlePlaceUrl.append("&key=AIzaSyDfiw9D8Ga_cvPreutbTmjdLZ1lBwyE3Qw")

        Log.d("URL_DEBUG",googlePlaceUrl.toString())
        return googlePlaceUrl.toString()

    }

    private fun buildLocationCallBack() {
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                mLastLocation = p0!!.locations[(p0.locations.size-1)]

                if(mMarker != null) {
                    mMarker!!.remove()
                }
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Your position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

                mMarker = mMap.addMarker(markerOptions)

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.moveCamera(CameraUpdateFactory.zoomTo(11f))

            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun checkLocationPermission(): Boolean {
        if(checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            }
            return false
        } else {
            return true
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            MY_PERMISSION_CODE->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (checkLocationPermission()) {
                            buildLocationRequest()
                            buildLocationCallBack()

                            fusedLocationProviderClient =
                                LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper()
                            )

                            mMap.isMyLocationEnabled = true
                        }
                    }
                } else {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in UT and move the camera
//        val ut = LatLng(30.285, -97.734)
//        mMap.addMarker(MarkerOptions().position(ut).title("Marker in UT"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(ut))

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            }
        }
        else
            mMap.isMyLocationEnabled = true

        mMap.uiSettings.isZoomControlsEnabled = true
    }
}
