package com.exploreutapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.model.Image
import kotlinx.android.synthetic.main.grid_item.view.*

class GridViewAdapter : BaseAdapter {
    var images : ArrayList<Image> ?= null
    var context: Context? = null

    constructor(context: Context, images: ArrayList<Image>) : super() {
        this.context = context
        this.images = images
    }

    override fun getCount(): Int {
        return images!!.size
    }

    override fun getItem(position: Int): Any {
        return images!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val image = this.images!![position]

        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var item_view = inflator.inflate(R.layout.grid_item, null)
        Glide.with(item_view.imageview)
            .load(image!!.path)
            .into(item_view.imageview)

        return item_view
    }
}

