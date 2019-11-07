package com.exploreutapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import kotlinx.android.synthetic.main.create_new_report.*
import okhttp3.Call
import java.io.File

class CreateReportActivity : AppCompatActivity(){
    var picker:ImagePicker = ImagePicker.create(this)
    var images: List<Image>? = null
    var image: Image? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_new_report)
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
        report_title.setText("")
        report_comment.setText("")
        imageView.setImageDrawable(null)
    }

    private fun checkValidity():Boolean{
        if(report_title.text.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please input the report title.",
                Toast.LENGTH_SHORT).show()
            return false
        }

        if(report_comment.text.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please input the report comment.",
                Toast.LENGTH_SHORT).show()
            return false
        }

        if(imageView.getDrawable()==null){
            Toast.makeText(getApplicationContext(), "Please add images for the review.",
                Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun postData(view: View){
        if(!checkValidity()){
            return
        }
        var title = report_title.text.toString()
        var comment = report_comment.text.toString()



        var builder=OkHttpUtils.post()
        builder.url("http://10.0.2.2:8080/create_new_report")
        builder.addParams("title", title)
        builder.addParams("comment", comment)

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