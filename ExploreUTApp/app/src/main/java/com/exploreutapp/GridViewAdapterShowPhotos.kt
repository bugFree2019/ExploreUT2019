package com.exploreutapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.exploreutapp.model.Place
import com.exploreutapp.remote.ExploreUTService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.photo_item.view.*

class GridViewAdapterShowPhotos : BaseAdapter{

    var place : Place ?= null
    var context: Context? = null

    constructor(context: Context, place: Place) : super() {
        this.context = context
        this.place = place
    }

    override fun getCount(): Int {
        return place!!.num_pics
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        Log.d("position", position.toString())
        val i = position

        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var item_view = inflator.inflate(R.layout.photo_item, null)

        Picasso.get().load(ExploreUTService.baseURL + "/place_image/" + place!!._id + "/" + i + ".jpg")
            .resize(480, 0).into(item_view.image_view)
        return item_view
    }
}