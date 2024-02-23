package com.example.boosthub.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.boosthub.MainViewModel
import com.example.boosthub.R
import com.example.boosthub.data.adapter.EventAdapter
import com.example.boosthub.data.datamodel.Event
import com.example.boosthub.databinding.FragmentHomeScreenBinding

class HomeScreenFragment : Fragment() {

    // The Binding object for the Fragment and the ViewModel are declared.
    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Attach a PagerSnapHelper to the RecyclerView for smooth scrolling between events.
        val eventHelper: SnapHelper = PagerSnapHelper()
        eventHelper.attachToRecyclerView(binding.eventRV)

        // Set up a listener to fetch events from the ViewModel when data changes.
        viewModel.eventsRef.addSnapshotListener { it, _ ->
            val listEvent = it!!.toObjects(Event::class.java)
            val adapter = EventAdapter(listEvent)
            binding.eventRV.adapter = adapter
        }

        // Set up a click listener to navigate to the event edit screen.
        binding.editEventIBTN.setOnClickListener {
            findNavController().navigate(R.id.eventEditScreenFragment)
        }
    }
}