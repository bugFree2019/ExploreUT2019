package com.example.create

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import kotlinx.android.synthetic.main.activity_main.imageView
import kotlinx.android.synthetic.main.create_new_report.*
import okhttp3.Call
import java.io.ByteArrayOutputStream

class SecondActivity : AppCompatActivity(){
    var iview: ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_new_report)
        iview = findViewById(R.id.imageView)
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 2) {

                val bundle = data!!.extras
                val bmp = bundle!!.get("data") as Bitmap?
                iview?.setImageBitmap(bmp)

            } else if (requestCode == 1) {

                val selectedImageUri = data!!.data
                iview?.setImageURI(selectedImageUri)
            }

        }
    }

    fun encodeImage(bm: Bitmap):String {
        var baos =  ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos)
        var b = baos.toByteArray()
        var encImage = Base64.encodeToString(b, Base64.DEFAULT)

        return encImage
    }

    fun reset(view:View){
        //views that need to be reset:
        //text_name, spinner_theme, spinner_tags, text_loc, text_intro, imageView
        report_title.setText("")
        report_comment.setText("")
        imageView.setImageDrawable(null)
    }

    fun checkValidity(name:String, intro:String, coordinates:String):Boolean{
        if(name.length==0){
            Toast.makeText(getApplicationContext(), "Please input the place name.",
                Toast.LENGTH_SHORT).show()
            return false
        }

        if(coordinates.equals("Latitude: Longitude")){
            Toast.makeText(getApplicationContext(), "Please get the current location.",
                Toast.LENGTH_SHORT).show()
            return false
        }

        if(intro.length==0){
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
        var title = report_title.text.toString()
        var comment = report_comment.text.toString()

        var bitmapdrawable = iview?.getDrawable() as BitmapDrawable
        var bitmap = bitmapdrawable.bitmap

        var encodedimage = encodeImage(bitmap)


        OkHttpUtils
            .post()
            .url("http://10.0.2.2:8082/create_new_report")
            .addParams("title", title)
            .addParams("comment", comment)
            .addParams("encoded_pic",encodedimage)
            .build()
            .execute(object: StringCallback(){
                override fun onResponse(p0: String?, p1: Int) {

                }

                override fun onError(p0: Call?, p1: java.lang.Exception?, p2: Int) {
                }

            })

    }
}