package com.exploreutapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.exploreutapp.model.Place
import com.exploreutapp.remote.ExploreUTService
import com.squareup.picasso.Picasso


class RecyclerViewAdapter(private val places: ArrayList<Place>) :
    RecyclerView.Adapter<RecyclerViewAdapter.PlaceViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    class PlaceViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cv: CardView = itemView.findViewById(R.id.cv)
        var placeName: TextView = itemView.findViewById(R.id.place_name)
        var placeTag: TextView = itemView.findViewById(R.id.place_tag)
        var placePhoto: ImageView = itemView.findViewById(R.id.place_photo)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PlaceViewHolder {
        val v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false)
        return PlaceViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.placeName.setText(places[position].name)
        var tags = ""
        for (tag in places[position].tags) {
            tags += "$tag "
        }
        holder.placeTag.setText(tags)
        val id = places[position]._id
        val imageId = 0
        Picasso.get().load(ExploreUTService.baseURL + "/place_image/" + id + "/" + imageId + ".jpg")
            .resize(360, 640).into(holder.placePhoto)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = places.size
}