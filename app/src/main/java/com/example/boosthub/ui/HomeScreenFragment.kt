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

        /**
         * The SnapHelper ensures that the RecyclerView always jumps to the current list item.
         */
        val eventHelper: SnapHelper = PagerSnapHelper()
        eventHelper.attachToRecyclerView(binding.eventRV)

        /**
         * The snapshot listener is added to the events collection.
         * When a change is made to the "events" collection.
         * The snapshot listener extracts the list of events and converts it into a list of event objects.
         * An adapter is created and the list of events is passed.
         * The created adapter is passed to the RecyclerView in the layout.
         */
        viewModel.eventsRef.addSnapshotListener { it, _ ->
            val listEvent = it!!.toObjects(Event::class.java)
            val adapter = EventAdapter(listEvent)
            binding.eventRV.adapter = adapter
        }

        /**
         * Clicking on "editEventBTN" will navigate to the "eventEditScreenFragment".
         */
        binding.editEventBTN.setOnClickListener {
            findNavController().navigate(R.id.eventEditScreenFragment)
        }
    }
}