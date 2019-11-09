package com.exploreutapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.list_rev.view.*

class ListViewAdapter : BaseAdapter {

    var reviews : ArrayList<String> ?= null
    var context: Context? = null

    constructor(context: Context, reviews: ArrayList<String>) : super() {
        this.context = context
        this.reviews = reviews
    }

    override fun getCount(): Int {
        return reviews!!.size
    }

    override fun getItem(position: Int): Any {
        return reviews!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val review1 = this.reviews!![position]

        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var itemView = inflator.inflate(R.layout.list_rev, null)
        itemView.text_rev.text = review1

        return itemView
    }
}

