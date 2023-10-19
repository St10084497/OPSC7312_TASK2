package com.example.openedmaps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BirdAdapter(private val birdList: List<TaskModel>) : RecyclerView.Adapter<BirdAdapter.BirdViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return BirdViewHolder(view)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        val bird = birdList[position]
        holder.birdName.text = bird.birdName
        holder.location.text = bird.locationName
        holder.description.text = bird.description
    }

    override fun getItemCount() = birdList.size

    inner class BirdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val birdName: TextView = itemView.findViewById(R.id.birdNameTextView)
        val location: TextView = itemView.findViewById(R.id.locationNameTextView)
        val description: TextView = itemView.findViewById(R.id.descriptionTextView)
    }
}