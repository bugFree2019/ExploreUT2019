package com.exploreutapp.ui.add

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.exploreutapp.GridViewAdapter
import com.exploreutapp.R
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import java.io.File

class AddPlaceFragment : Fragment(), View.OnClickListener {
    private var locationManager:LocationManager?=null
    private var listener:LocationListener?=null
    var images: List<Image>? = null
    var loc:String?=null
    var root:View?=null
    val LOCATION_PERMISSION_REQUEST = 10
    val PICK_IMAGES_PERMISSION_REQUEST = 11
    var could_get_location = false
    var could_pick_images = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.create_new_place, container, false)

        setHasOptionsMenu(true)

        setSpinners()
        setButtons()
        //request the permissions for "get location" functions when the fragment is created, if the permissions needed are granted succesfuuly, the "registerLocationUpdates" function  will be called
        //Note that the registerLocationUpdates function only needs to be called once (we'll check if the LocationManager has been initialized before we call this method.)
        requestLocationPermissions()

        val users = FirebaseAuth.getInstance().currentUser
        if (users != null) {
            println(users!!.email)
            Log.d("myTag", users!!.email)
        }

        return root
    }

    private fun setSpinners(){
        val adapter1 = ArrayAdapter.createFromResource(context!!, R.array.tag_array,android.R.layout.simple_spinner_item)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        var spinner_tags = root!!.findViewById<Spinner>(R.id.spinner_tags)
        spinner_tags.adapter = adapter1
        spinner_tags.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        })

        val adapter2 = ArrayAdapter.createFromResource(context!!, R.array.theme_array,android.R.layout.simple_spinner_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        var spinner_theme = root!!.findViewById<Spinner>(R.id.spinner_theme)
        spinner_theme.adapter = adapter2
        spinner_theme.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        })
    }

    fun setButtons(){
        var button_loc =  root!!.findViewById<Button>(R.id.button_loc)
        var button_images =  root!!.findViewById<Button>(R.id.button_images)
        var button_reset =  root!!.findViewById<Button>(R.id.button_reset)
        var button_submit =  root!!.findViewById<Button>(R.id.button_submit)
        button_loc.setOnClickListener(this)
        button_images.setOnClickListener(this)
        button_reset.setOnClickListener(this)
        button_submit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v?.id==R.id.button_loc){
            getLocation(v)
        }
        else if(v?.id==R.id.button_images){
            pickImages(v)
        }
        else if(v?.id==R.id.button_reset){
            reset(v)
        }
        else if(v?.id==R.id.button_submit){
            postData(v)
        }
    }

    private fun requestPickImagesPermissions() {
        if (ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )  {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                ),PICK_IMAGES_PERMISSION_REQUEST
            )
        }
        else{
            could_pick_images=true
        }
    }

    private fun requestLocationPermissions(){
        if (ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity!!, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION)
                ,LOCATION_PERMISSION_REQUEST)
            Log.d("result","have visited here1")
        }
        else{
            Log.d("result","have visited here2")
            could_get_location=true
            if(locationManager==null){
                registerLocationUpdates()
            }
        }
    }


    private fun registerLocationUpdates(){

        locationManager = activity!!.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        listener = object:LocationListener {

            override fun onLocationChanged(location: Location) {
                loc = "" + location.getLatitude() + " " + location.getLongitude()
                Log.d("location",loc)
            }


            override fun onStatusChanged(s:String, i:Int, bundle:Bundle) {

            }


            override fun onProviderEnabled(s:String) {

            }

            override fun onProviderDisabled(s:String) {
                var i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(i)
            }
        }

        var i:Float=5f
        if (ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager?.requestLocationUpdates("gps", 1, i, listener)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty())) {
                    var all_permissions_granted = true
                    for(grant_result in grantResults){
                        if(grant_result!=PackageManager.PERMISSION_GRANTED){
                            all_permissions_granted=false
                        }
                    }
                    if(all_permissions_granted){
                        could_get_location=true
                        if(locationManager==null){
                            registerLocationUpdates()
                        }
                    }
                }
                else {
                    Log.d("result","have visited here3")
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            PICK_IMAGES_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty())){
                    var all_permissions_granted = true
                    for(grant_result in grantResults){
                        if(grant_result!=PackageManager.PERMISSION_GRANTED){
                            all_permissions_granted=false
                        }
                    }
                    if(all_permissions_granted){
                        could_pick_images=true
                    }
                }
            }
        }
    }

    fun getLocation(view: View){
        requestLocationPermissions()
        if(!could_get_location){
            Toast.makeText(activity!!.getApplicationContext(), "Not all the relevant permissions are granted. The function can't work.",Toast.LENGTH_LONG).show()
            return
        }
        root!!.findViewById<TextView>(R.id.text_loc).setText(loc)
    }

    fun pickImages(view: View){
        requestPickImagesPermissions()
        Log.d("debug","1")
        if(!could_pick_images){
            Log.d("debug","2")
            Toast.makeText(activity!!.getApplicationContext(), "Not all the relevant permissions are granted. The function can't work.",Toast.LENGTH_LONG).show()
            return
        }
        Log.d("debug","3")
        var picker: ImagePicker = ImagePicker.create(this)
        picker.imageDirectory("Camera").start()
    }

    @Override
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            images = ImagePicker.getImages(data)
            var images_list = images as  ArrayList<Image>

            // or get a single image only
            //image = ImagePicker.getFirstImageOrNull(data)
            var gridview = root!!.findViewById<GridView>(R.id.gridview)
            var g_adapter = GridViewAdapter(context!!,images_list)
            gridview.adapter = g_adapter

        }
        if(requestCode==11){
            Log.d("test",data.toString())
        }
        super.onActivityResult(requestCode, resultCode, data)

    }


    fun reset(view:View){
        //views that need to be reset:
        //text_name, spinner_theme, spinner_tags, text_loc, text_intro, imageView
        root!!.findViewById<EditText>(R.id.text_name).setText("")
        root!!.findViewById<Spinner>(R.id.spinner_theme).setSelection(0)
        root!!.findViewById<Spinner>(R.id.spinner_tags).setSelection(0)
        root!!.findViewById<TextView>(R.id.text_loc).setText("Latitude: Longitude")
        root!!.findViewById<EditText>(R.id.text_intro).setText("")
        root!!.findViewById<GridView>(R.id.gridview).adapter = null
    }

    //Check the validity of inputs (name, location, intro, )
    private fun checkValidity():Boolean{
        if(root!!.findViewById<EditText>(R.id.text_name).text.isEmpty()){
            Toast.makeText(activity!!.getApplicationContext(), "Please input the place name.",
                Toast.LENGTH_SHORT).show()
            return false
        }

        if(root!!.findViewById<TextView>(R.id.text_loc).text.equals("Latitude: Longitude")){
            Toast.makeText(activity!!.getApplicationContext(), "Please get the current location.",
                Toast.LENGTH_SHORT).show()
            return false
        }

        if(root!!.findViewById<EditText>(R.id.text_intro).text.isEmpty()){
            Toast.makeText(activity!!.getApplicationContext(), "Please input the intro.",
                Toast.LENGTH_SHORT).show()
            return false
        }

        if(root!!.findViewById<GridView>(R.id.gridview).isEmpty()){
            Toast.makeText(activity!!.getApplicationContext(), "Please add images for the place.",
                Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun postData(view:View){
        //Check the validity of inputs, if there are still some invalid inputs, show the notification.
        if(!checkValidity()){
            return
        }

        var name = root!!.findViewById<EditText>(R.id.text_name).text.toString()
        var theme = root!!.findViewById<Spinner>(R.id.spinner_theme).selectedItem.toString()
        var tag = root!!.findViewById<Spinner>(R.id.spinner_tags).selectedItem.toString()
        var intro = root!!.findViewById<EditText>(R.id.text_intro).text.toString()
        var coordinates = root!!.findViewById<TextView>(R.id.text_loc).text.toString()


        var builder=OkHttpUtils.post()
//        builder.url("http://10.0.2.2:8080/create_new_place")
        builder.url("https://explore-ut.appspot.com/")
        builder.addParams("theme", theme)
        builder.addParams("tag", tag)
        builder.addParams("name", name)
        builder.addParams("intro", intro)
        builder.addParams("location", coordinates)

        val image_iterator = images!!.iterator()
        while(image_iterator.hasNext()){
            var image = image_iterator.next()
            var pic_file = File(image.path)
            builder.addFile("pic_files",pic_file.name,pic_file)
            Log.d("image_path",image.path)
            Log.d("image_name",pic_file.name)

        }

        builder.addHeader("Connection","close").build()
            .execute(object: StringCallback(){
                override fun onResponse(p0: String?, p1: Int) {
                    Toast.makeText(activity!!.getApplicationContext(), p0,
                        Toast.LENGTH_LONG).show()
                }

                override fun onError(p0: Call?, p1: java.lang.Exception?, p2: Int) {
                    Toast.makeText(activity!!.getApplicationContext(), p1.toString(),
                        Toast.LENGTH_LONG).show()
                }
            })
        Toast.makeText(activity!!.getApplicationContext(), "Successfully add the place "+root!!.findViewById<EditText>(R.id.text_name).text.toString(),
            Toast.LENGTH_LONG).show()
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
