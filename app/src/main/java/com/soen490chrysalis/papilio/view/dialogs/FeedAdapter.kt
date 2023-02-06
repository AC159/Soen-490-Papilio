package com.soen490chrysalis.papilio.view.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.soen490chrysalis.papilio.R
import com.soen490chrysalis.papilio.services.network.responses.ActivityObject
import com.soen490chrysalis.papilio.view.HomeFragment

class FeedAdapter(private val feedList: List<ActivityObject>, private val homeFragment: HomeFragment): RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private lateinit var activityListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener)
    {
        activityListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_activities_box,
        parent, false)
        return FeedViewHolder(itemView, activityListener)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val currentItem = feedList[position]
        holder.boxTitle.text = currentItem.title


        holder.boxAddress.text = currentItem.address
        holder.boxStartTime.text = currentItem.startTime

        if (currentItem.images != null && currentItem.images?.isNotEmpty() == true){
            Glide.with(homeFragment).load(currentItem.images?.get(0)).into(holder.boxImage)
        }


    }
    override fun getItemCount(): Int {
        return feedList.size
    }

    class FeedViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        val boxTitle: TextView = itemView.findViewById(R.id.activity_box_title)
        val boxImage: ImageView = itemView.findViewById(R.id.activity_box_image)
        val boxAddress: TextView = itemView.findViewById(R.id.activity_box_address)
        val boxStartTime: TextView = itemView.findViewById(R.id.activity_box_start_time)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}
