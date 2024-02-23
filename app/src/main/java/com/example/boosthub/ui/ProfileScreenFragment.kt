package com.example.boosthub.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import coil.load
import com.example.boosthub.MainViewModel
import com.example.boosthub.R
import com.example.boosthub.data.adapter.EventAdapter
import com.example.boosthub.data.datamodel.Event
import com.example.boosthub.data.datamodel.User
import com.example.boosthub.databinding.FragmentProfileScreenBinding

class ProfileScreenFragment : Fragment() {

    //The Binding object for the Fragment and the ViewModel are declared.
    private lateinit var binding: FragmentProfileScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileScreenBinding.inflate(layoutInflater)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up a listener to fetch the current user data from the ViewModel
        viewModel.currentUserRef.addSnapshotListener { snapshot, _ ->

            // Convert the snapshot to a User object
            val user = snapshot?.toObject(User::class.java)!!

            // Load the user image into the ImageView if it is not empty
            if (user.image != "") {
                binding.profileImageSIV.load(user.image)
            }
            // Set the user name and current cars in the corresponding TextViews
            binding.profileUserCurrentUserMTV.text = user.userName
            binding.profileUserCurrentUserCurrentCarsMTV.text = user.currentCars
        }

        // Sets the OnClickListener for the button to navigate to the profile edit screen.
        binding.profileEditBTN.setOnClickListener {
            findNavController().navigate(R.id.profileEditScreenFragment)
        }

        // Attach a PagerSnapHelper to the RecyclerView for smooth scrolling between events.
        val eventHelper: SnapHelper = PagerSnapHelper()
        eventHelper.attachToRecyclerView(binding.profileRV)

        // Set up a listener to fetch events from the ViewModel when data changes.
        viewModel.eventsRef.addSnapshotListener { it, _ ->
            val listEvent = it!!.toObjects(Event::class.java)
            val filteredList = listEvent.filter { it.creatorId == viewModel.currentUserRef.id }
            val adapter = EventAdapter(filteredList)
            binding.profileRV.adapter = adapter
        }
    }
}
