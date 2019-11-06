package com.exploreutapp

//import com.esafirm.imagepicker.features.ImagePicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import kotlinx.android.synthetic.main.create_new_place.*
import okhttp3.Call
import java.io.ByteArrayOutputStream
import java.io.File


class CreatePlaceActivity : AppCompatActivity(){

    private var image: ImageView? = null

    private var locationManager:LocationManager?=null
    private var listener:LocationListener?=null
    var iview:ImageView?=null
    var selectedImageUri:Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_new_place)

        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        iview = findViewById<ImageView>(R.id.imageView)
        listener = object:LocationListener {

            override fun onLocationChanged(location:Location) {
                text_loc.text = "" + location.getLatitude() + " " + location.getLongitude()
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
        requestReadAndWritePermissions()


    }

    fun getImageFromGallery(view:View){
        //picker.start() // start image picker activity with request code
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    fun getImageFromCamera(view:View){
        //picker.start() // start image picker activity with request code
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 2)
    }


    fun getLocation(view:View){
        var i:Float=0f
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION)
                ,10)
        }
        locationManager?.requestLocationUpdates("gps", 5000, i, listener)
    }

    fun requestReadAndWritePermissions(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ,11)
        }
    }




    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 2) {

                val bundle = data!!.extras
                val bmp = bundle!!.get("data") as Bitmap?
                iview?.setImageBitmap(bmp)

            } else if (requestCode == 1) {

                selectedImageUri = data!!.data
                iview?.setImageURI(selectedImageUri)
            }

        }
    }

    fun encodeImage(bm:Bitmap):String {
        var baos =  ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos)
        var b = baos.toByteArray()
        var encImage = Base64.encodeToString(b, Base64.DEFAULT)

        return encImage
    }

    fun getRealPathFromUri(context: Context, contentUri:Uri):String {
        var cursor: Cursor?= null
        try {
            var proj = arrayOf( MediaStore.Images.Media.DATA )
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null)
            var column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            return cursor!!.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
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
    fun checkValidity():Boolean{
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

        if(iview?.getDrawable()==null){
            Toast.makeText(getApplicationContext(), "Please add one image for the place.",
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
        var coordinates = text_loc.text.split(" ")


        var map = HashMap<String,Double>()
        map.put("lat",coordinates[0].toDouble())
        map.put("lng",coordinates[1].toDouble())

        var gson = Gson()
        var coordinates_json = gson.toJson(map).toString()

        var bitmapdrawable = iview?.getDrawable() as BitmapDrawable
        var bitmap = bitmapdrawable.bitmap
        val image_uri = selectedImageUri
        var path:String?=null
        if(image_uri!=null){
            path = getRealPathFromUri(this,image_uri)
        }
        var image = File(path)
        Log.d("image_path",path)
        //Log.d("image_name",)
        //var encodedimage = encodeImage(bitmap)


        OkHttpUtils
            .post()
            .url("http://10.0.2.2:8082/create_new_place")
            .addParams("theme", theme)
            .addParams("tags", tag)
            .addParams("name", name)
            .addParams("intro", intro)
            .addParams("location", coordinates_json)
            //.addFile("pic_files",image.name,image)
            .build()
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

    /*
    Multi-image upload test
    */
    /*
    var picker:ImagePicker =ImagePicker.create(this)

    fun onClick_test(view: View){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ||  ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
                    , 11)
            }
        }
        picker.imageDirectory("Camera").start()
    }
    */



    companion object {
        private val TAG = "MainActivity"
    }
}