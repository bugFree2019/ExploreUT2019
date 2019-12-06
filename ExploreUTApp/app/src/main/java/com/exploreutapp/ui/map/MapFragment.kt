package com.exploreutapp.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.exploreutapp.R
import com.exploreutapp.ViewPlaceActivity
import com.exploreutapp.model.Place
import com.exploreutapp.remote.ExploreUTService
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import java.io.Serializable

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMapView: MapView
    private lateinit var mMap: GoogleMap
    private var latitude: Double=0.toDouble()
    private var longitude: Double=0.toDouble()

    private lateinit var mLastLocation: Location
    private var mMarker: Marker?=null
    private var disposable: Disposable? = null

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    companion object {
        private const val MY_PERMISSION_CODE: Int = 1000
        val mService by lazy {
            ExploreUTService.create()
        }
    }

    private var currentPlaces: ArrayList<Place> = ArrayList()
    var currentResult: Place?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        setHasOptionsMenu(true)

        mMapView = root.findViewById(R.id.mapView) as MapView
        mMapView.onCreate(savedInstanceState)

        mMapView.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity()!!.getApplicationContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMapView.getMapAsync(this)

        // check if current build version is higher than Marshmallow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                buildLocationRequest()
                buildLocationCallBack()

                Log.d("location", checkLocationPermission().toString())

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)

                Log.d("location", fusedLocationProviderClient.toString())
                
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            } else {
                Toast.makeText(context,"Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
        else {
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }

        // get current location button shown on map
        val buttonHelper: View = (mMapView.findViewById<View>(Integer.parseInt("1")).getParent()) as View
        val locationButton: View = buttonHelper.findViewById(Integer.parseInt("2"))
        val rlp: RelativeLayout.LayoutParams = locationButton.getLayoutParams() as RelativeLayout.LayoutParams

        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        rlp.setMargins(0, 180, 180, 0)

        root.findViewById<BottomNavigationView>(R.id.bottom_navigation_view).setOnNavigationItemSelectedListener { item->
            when(item.itemId) {
                R.id.action_all -> nearByPlace("All")
                R.id.action_activity -> nearByPlace("Activity")
                R.id.action_library -> nearByPlace("Study")
                R.id.action_building -> nearByPlace("Building")
                R.id.action_view -> nearByPlace("Monument")
            }
            true
        }

        return root
    }

    private fun nearByPlace(place_theme: String) {

        // Clear all marker on Map
        mMap.clear()
        if (place_theme == "All") {
            disposable = mService.getAllPlaces()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        } else {
            disposable = mService.getThemePlaces(place_theme)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        }
    }

    // handle the response with an arraylist of places
    private fun handleResponse(result: ArrayList<Place>) {
        try {
            currentPlaces = result
            for(r in currentPlaces) {
                Log.d("myTag", r._id)
                Log.d("myTag", r.name)
                Log.d("myTag", r.theme)
                Log.d("myTag", r.tags.toString())
//                Log.d("myTag", r.address)
                Log.d("myTag", r.intro)
                Log.d("myTag", r.reviews.toString())
                Log.d("myTag", r.location!!.lat.toString())
                Log.d("myTag", r.location!!.lng.toString())
                Log.d("myTag", r.num_pics!!.toString())
            }

            if (currentPlaces != null) {
                Log.d("myTag", currentPlaces.size.toString())
                for (i in 0 until currentPlaces.size) {
                    val markerOptions = MarkerOptions()
                    val utPlace = currentPlaces[i]

                    // making sure the place we are looking at does have location
                    if (utPlace.location != null) {
                        val placeTheme = utPlace.theme
                        val placeName = utPlace.name
                        val lat = utPlace.location!!.lat
                        val lng = utPlace.location!!.lng

                        val latLng = LatLng(lat,lng)

                        markerOptions.position(latLng)
                        markerOptions.title(placeName)
                        if (Regex("Activity").containsMatchIn(placeTheme) ||
                            Regex("Museum").containsMatchIn(placeTheme)) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_activity)))
                        } else if (Regex("Study").containsMatchIn(placeTheme) ||
                            Regex("Library").containsMatchIn(placeTheme)) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_library)))
                        } else if (Regex("Building").containsMatchIn(placeTheme)) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_building)))
                        } else if (Regex("Monument").containsMatchIn(placeTheme)
                            || Regex("Statue").containsMatchIn(placeTheme)) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_view)))
                        } else {
                            markerOptions.icon(
                                BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_GREEN
                                )
                            )
                        }
                        markerOptions.snippet(i.toString()) // Assign index for Marker
                        // Add marker to map
                        mMap.addMarker(markerOptions)

                        // Move camera
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d("myTag", "No valid json")
        }
        Log.d("myTag", "Done")
    }

    private fun handleError(error: Throwable) {
        Log.d("myTag", error.localizedMessage!!)
    }


    private fun buildLocationCallBack() {
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                mLastLocation = p0!!.locations[p0.locations.size-1]

                if(mMarker != null) {
                    mMarker!!.remove()
                }
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Current position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

                mMarker = mMap.addMarker(markerOptions)

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15f))

            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()

        Log.d("location", locationRequest.toString())

        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun checkLocationPermission(): Boolean {
        if(ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ), MY_PERMISSION_CODE
                )
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ), MY_PERMISSION_CODE
                )
            }
            return false
        } else
            return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {


            Log.d("grant", grantResults.toString())

        when (requestCode) {
            MY_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    buildLocationRequest()
                    buildLocationCallBack()

                    fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(activity!!)
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.myLooper()
                    )
                    mMap.isMyLocationEnabled = true

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(context,"Please change location permission preferences", Toast.LENGTH_LONG).show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
                // Since we do not have any other permission request in this fragment, this left blank
            }
        }
    }


    override fun onStop() {

        //remove current location information when stop app
        if (::fusedLocationProviderClient.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }

        super.onStop()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            }
        }
        else
            mMap.isMyLocationEnabled = true

        // make event clickable on markers

        mMap.setOnMarkerClickListener { marker ->
            // when user select marker, just get result of place assign to static variable
            currentResult = currentPlaces!![Integer.parseInt(marker.snippet)]

            val viewIntent = Intent(activity!!, ViewPlaceActivity::class.java)
            // start new activity
            viewIntent.putExtra("place_to_show", currentResult as Serializable)
            startActivity(viewIntent)
            true
        }

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.setAllGesturesEnabled(true)
        nearByPlace("All")
    }

    override fun onResume() {
        mMapView.onResume()
        super.onResume()
    }


    override fun onPause() {
        mMapView.onPause()
        super.onPause()
        disposable?.dispose()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mMapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        if(item.getItemId() == R.id.sign_out_button) {
            //Signout
            AuthUI.getInstance().signOut(context!!).addOnCompleteListener{
            }.addOnFailureListener{
                Log.d("myTag", "sign out error")
            }
            item.setVisible(false)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}