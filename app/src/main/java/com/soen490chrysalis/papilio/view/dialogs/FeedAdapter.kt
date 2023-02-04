package com.soen490chrysalis.papilio.view.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soen490chrysalis.papilio.R
import com.soen490chrysalis.papilio.services.network.responses.ActivityObject

class FeedAdapter(private val feedList: List<ActivityObject>): RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private lateinit var activityListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener)
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
//        holder.boxId.id = currentItem.id.toIntOrNull()
        holder.boxTitle.text = currentItem.title
//        holder.boxImage.setImageURI(currentItem.images?.get(0))
        holder.boxAddress.text = currentItem.address
        holder.boxStartTime.text = currentItem.startTime


    }
    override fun getItemCount(): Int {
        return feedList.size
    }

    class FeedViewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView){
//        val boxId: RelativeLayout = itemView.findViewById(R.id.activity_box)
        val boxTitle: TextView = itemView.findViewById(R.id.activity_box_title)
//        val boxImage: ShapeableImageView = itemView.findViewById(R.id.activity_box_image)
        val boxAddress: TextView = itemView.findViewById(R.id.activity_box_address)
        val boxStartTime: TextView = itemView.findViewById(R.id.activity_box_start_time)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}

//private fun ShapeableImageView.setImageResource(pair: Pair<String, InputStream>) {
//
//}
