package com.exploreutapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.navigation.ui.AppBarConfiguration
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.exploreutapp.model.Place
import com.exploreutapp.remote.ExploreUTService
import com.google.firebase.auth.FirebaseAuth
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import kotlinx.android.synthetic.main.create_new_report.*
import okhttp3.Call
import java.io.File


class CreateReportActivity : AppCompatActivity(){
    private lateinit var appBarConfiguration: AppBarConfiguration
    var picker:ImagePicker = ImagePicker.create(this)
    var images: List<Image>? = null
    var place_id:String? = null
    var place:Place?=null
    var user_id: String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_new_report)

        // enable the back button on top left corner
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        place = intent.getSerializableExtra("place") as Place
        place_id = place!!._id
        if(place_id==null){
            Log.d("null","null")
        }
        var user = FirebaseAuth.getInstance().currentUser
        Log.d("email",user!!.email)
        //Log.d("place_id",place_id)
        user_id = user!!.email
    }

    fun pickImages(view: View){
        picker.imageDirectory("Camera").start()
    }

    @Override
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            images = ImagePicker.getImages(data)
            var images_list = images as  ArrayList<Image>
            var g_adapter = GridViewAdapter(this,images_list)
            gridview.adapter = g_adapter
        }
        super.onActivityResult(requestCode, resultCode, data)

    }


    fun reset(view:View){
        //views that need to be reset:
        //report_title,report_comment, gridview
        report_title.setText("")
        report_comment.setText("")
        gridview.adapter = null
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

        if(gridview.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please add images for the report.",
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
//        builder.url("http://10.0.2.2:8080/create_new_report")
        builder.url(ExploreUTService.baseURL+"create_new_report")
        builder.addParams("title", title)
        builder.addParams("comment", comment)
        builder.addParams("place_id", place_id)
        builder.addParams("user_id",user_id)

        val image_iterator = images!!.iterator()
        while(image_iterator.hasNext()){
            var image = image_iterator.next()
            var pic_file = File(image.path)
            builder.addFile("pic_files",pic_file.name,pic_file)
            Log.d("image_path",image.path)
            Log.d("image_name",pic_file.name)
        }

        //val viewPlaceIntent = Intent(this, ViewPlaceActivity::class.java)
        //viewPlaceIntent.putExtra("place_to_show", place)

        builder.addHeader("Connection","close").build()
            .execute(object: StringCallback(){
                override fun onResponse(p0: String?, p1: Int) {
                    //Toast.makeText(getApplicationContext(), p0,
                        //Toast.LENGTH_SHORT).show()
                    //startActivity(viewPlaceIntent)
                    Toast.makeText(getApplicationContext(), "Successfully added the report ",
                        Toast.LENGTH_LONG).show()
                    finish()
                }

                override fun onError(p0: Call?, p1: java.lang.Exception?, p2: Int) {
                    //Toast.makeText(getApplicationContext(), p1.toString(),
                        //Toast.LENGTH_LONG).show()
                }
            })
    }

    // for back button navigation
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}