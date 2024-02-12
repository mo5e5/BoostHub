package com.example.boosthub.data.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.boosthub.R
import com.example.boosthub.data.datamodel.Event
import com.example.boosthub.databinding.ItemHomeEventBinding
import com.example.boosthub.ui.HomeScreenFragmentDirections
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class HomeEventAdapter(private val dataset: List<Event>) :
    RecyclerView.Adapter<HomeEventAdapter.HomeEventViewHolder>() {

    inner class HomeEventViewHolder(val binding: ItemHomeEventBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeEventViewHolder {
        val binding =
            ItemHomeEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeEventViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: HomeEventViewHolder, position: Int) {

        val event = dataset[position]

        holder.binding.itemHomeEventImageSIV.load(event.image)

        holder.binding.itemHomeLocationInputMTV.text = event.location

        holder.binding.itemHomeWhatsUpInputMTV.text = event.whatsUp

        holder.binding.itemHomeEventMCV.setOnClickListener {
            val navController = holder.itemView.findNavController()
            navController.navigate(HomeScreenFragmentDirections.actionHomeScreenFragmentToEventDetailScreenFragment(position))
        }
    }
}