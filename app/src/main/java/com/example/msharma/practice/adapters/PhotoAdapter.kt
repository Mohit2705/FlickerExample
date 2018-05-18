package com.example.msharma.practice.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.msharma.practice.R
import com.squareup.picasso.Picasso

const val PHOTO_VIEW = 0
const val LOADING_VIEW = 1
private const val TAG = "PhotoAdapter"

class PhotoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list: ArrayList<String> = ArrayList()
    private var showLoading = false

    override fun getItemViewType(position: Int): Int {
        if (showLoading && position == list.size) {
            return LOADING_VIEW
        }
        return PHOTO_VIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // TODO replace with when
        return if (viewType == PHOTO_VIEW) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loading_row, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            PHOTO_VIEW -> {
                holder as ItemViewHolder
                Picasso.with(holder.photoThumbnailView.context).load(list[position]).into(holder.photoThumbnailView)
            }
            LOADING_VIEW -> {
                Log.v(TAG, "Loading view holder")
            }
            else -> throw IllegalStateException("Unknown view type")
        }
    }

    override fun getItemCount(): Int {
        return list.size + if (showLoading) 1 else 0
    }

    fun setItems(items: Pair<List<String>, Boolean>) {
        list.addAll(items.first)
        showLoading = items.second
      // TODO use item range inserted
        notifyDataSetChanged()
    }

    fun clearItems() {
        list.clear()
        showLoading = false
        notifyDataSetChanged()
    }

}

class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
}

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val photoThumbnailView: ImageView = view.findViewById(R.id.image)
}
