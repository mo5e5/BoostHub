package com.example.boosthub.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.boosthub.R
import com.example.boosthub.data.datamodel.Event
import com.example.boosthub.databinding.ItemSmallEventBinding
import com.example.boosthub.ui.HomeScreenFragmentDirections
import com.example.boosthub.ui.ProfileScreenFragmentDirections

class EventAdapter(private val dataset: List<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(val binding: ItemSmallEventBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding =
            ItemSmallEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {

        val event = dataset[position]

        holder.binding.itemSmallEventImageSIV.load(event.image)

        holder.binding.itemSmallLocationInputMTV.text = event.location

        holder.binding.itemSmallWhatsUpInputMTV.text = event.whatsUp

        holder.binding.itemSmallEventMCV.setOnClickListener {
            val navController = holder.itemView.findNavController()
            if (navController.currentDestination?.id == R.id.homeScreenFragment) {
                navController.navigate(
                    HomeScreenFragmentDirections.actionHomeScreenFragmentToEventDetailScreenFragment(
                        event.image,
                        event.whatsUp,
                        event.location,
                        event.date,
                        event.whosThere,
                        event.whatElse,
                        event.restrictions,
                    )
                )
            } else if (navController.currentDestination?.id == R.id.profileScreenFragment) {
                navController.navigate(
                    ProfileScreenFragmentDirections.actionProfileScreenFragmentToEventEditScreenFragment(
                        event.eventId
                    )
                )
            }
        }
    }
}