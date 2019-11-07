
package com.exploreutapp


//import com.esafirm.imagepicker.features.ImagePicker

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import kotlinx.android.synthetic.main.create_new_place.*
import okhttp3.Call
import java.io.File


class CreatePlaceActivity : AppCompatActivity(){


    private var locationManager:LocationManager?=null
    private var listener:LocationListener?=null
    var selectedImageUri:Uri?=null
    var images: List<Image>? = null
    var image:Image? = null
    var picker:ImagePicker = ImagePicker.create(this)
    var loc:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_new_place)


        setSpinners()
        requestReadAndWritePermissions()
        registerLocationUpdates()


    }

    private fun requestReadAndWritePermissions(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ,11)
        }
    }



    private fun registerLocationUpdates(){

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        listener = object:LocationListener {

            override fun onLocationChanged(location: Location) {
                loc = "" + location.getLatitude() + " " + location.getLongitude()
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION)
                ,10)
        }
        locationManager?.requestLocationUpdates("gps", 5000, i, listener)
    }

    private fun setSpinners(){
        val adapter1 = ArrayAdapter.createFromResource(this, R.array.tag_array,android.R.layout.simple_spinner_item)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_tags.adapter = adapter1
        spinner_tags.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        })

        val adapter2 = ArrayAdapter.createFromResource(this, R.array.theme_array,android.R.layout.simple_spinner_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_theme.adapter = adapter2
        spinner_theme.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        })
    }

    fun getLocation(view: View){
        text_loc.setText(loc)
    }


    fun pickImages(view: View){
        picker.imageDirectory("Camera").start()
    }

    @Override
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            images = ImagePicker.getImages(data)
            // or get a single image onlyp
            image = ImagePicker.getFirstImageOrNull(data)
            Glide.with(imageView)
                .load(image!!.path)
                .into(imageView)
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun reset(view:View){
        //views that need to be reset:
        //text_name, spinner_theme, spinner_tags, text_loc, text_intro, imageView
        text_name.setText("")
        spinner_theme.setSelection(0)
        spinner_tags.setSelection(0)
        text_loc.setText("Latitude: Longitude")
        text_intro.setText("")
        imageView.setImageDrawable(null)
    }

    //Check the validity of inputs (name, location, intro, )
    private fun checkValidity():Boolean{
        if(text_name.text.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please input the place name.",
                Toast.LENGTH_SHORT).show()
            return false
        }

        if(text_loc.text.equals("Latitude: Longitude")){
            Toast.makeText(getApplicationContext(), "Please get the current location.",
                Toast.LENGTH_SHORT).show()
            return false
        }

        if(text_intro.text.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please input the intro.",
                Toast.LENGTH_SHORT).show()
            return false
        }

        if(imageView.getDrawable()==null){
            Toast.makeText(getApplicationContext(), "Please add images for the place.",
                Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun postData(view: View){
        //Check the validity of inputs, if there are still some invalid inputs, show the notification.
        if(!checkValidity()){
            return
        }

        var name = text_name.text.toString()
        var theme = spinner_theme.selectedItem.toString()
        var tag = spinner_tags.selectedItem.toString()
        var intro = text_intro.text.toString()
        var coordinates = text_loc.text.toString()


        var builder=OkHttpUtils.post()
        builder.url("http://10.0.2.2:8080/create_new_place")
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
                    Toast.makeText(getApplicationContext(), p0,
                        Toast.LENGTH_SHORT).show()
                }

                override fun onError(p0: Call?, p1: java.lang.Exception?, p2: Int) {
                    Toast.makeText(getApplicationContext(), p1.toString(),
                        Toast.LENGTH_LONG).show()
                }
            })
    }
}

