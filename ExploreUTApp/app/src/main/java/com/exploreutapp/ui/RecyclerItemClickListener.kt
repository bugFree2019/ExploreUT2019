package com.exploreutapp.ui

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView


class RecyclerItemClickListener(context: Context, recyclerView: RecyclerView,
                                listener: OnItemClickListener) : RecyclerView.OnItemTouchListener {
    private val  mListener: OnItemClickListener = listener

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onLongItemClick(view: View, position: Int)
    }

    val mGestureDetector: GestureDetector

    init {
        class SimpleOnGestureListenerImpl : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child: View = recyclerView.findChildViewUnder(e.getX(), e.getY())!!
                mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child))
            }
        }
        mGestureDetector = GestureDetector(context, SimpleOnGestureListenerImpl())
    }

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView: View = view.findChildViewUnder(e.getX(), e.getY())!!
        if (mGestureDetector!!.onTouchEvent(e)) {
          mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
          return true
        }
        return false
    }

    override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {

    }

    override fun onRequestDisallowInterceptTouchEvent (disallowIntercept: Boolean){

    }
}