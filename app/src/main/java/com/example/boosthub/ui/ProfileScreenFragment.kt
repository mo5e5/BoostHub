package com.example.boosthub.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

    /**
     * The Binding object for the Fragment and the ViewModel are declared.
     */
    private lateinit var binding: FragmentProfileScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    /**
     * URI object to store the selected image.
     */
    private var imageShort: Uri? = null

    /**
     * ActivityResultLauncher to start the image selection activity.
     * URI of the selected image is saved and the image in the ImageView is loaded.
     */
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imageShort = uri
                binding.profileImageSIV.load(imageShort)
            }
        }

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

        /**
         * Profile picture updates when a picture is selected.
         */
        if (imageShort != null) {
            viewModel.uploadProfileImage(imageShort!!)
            Toast.makeText(requireContext(), "image updated", Toast.LENGTH_LONG).show()
        }


        /**
         * Listener for changes to the user data in the ViewModel.
         * User data is obtained from the snapshot.
         * Profile image is loaded if an image is present in the user profile.
         * Username is placed in the text field.
         */
        viewModel.currentUserRef.addSnapshotListener{ snapshot, _ ->

            val user = snapshot?.toObject(User::class.java)!!

            if (user.image != "") {
                binding.profileImageSIV.load(user.image)
            }
            binding.profileUserCurrentUserMTV.text = user.userName
            binding.profileUserCurrentUserCurrentCarsMTV.text = user.currentCars
        }

        /**
         * The edit profile button navigates to the fragment profile edit screen.
         */
        binding.profileEditBTN.setOnClickListener{
            findNavController().navigate(R.id.profileEditScreenFragment)
        }

        /**
         * The SnapHelper ensures that the RecyclerView always jumps to the current list item.
         */
        val eventHelper: SnapHelper = PagerSnapHelper()
        eventHelper.attachToRecyclerView(binding.profileRV)

        /**
         * The snapshot listener is added to the events collection.
         * When a change is made to the "events" collection.
         * The snapshot listener extracts the list of events and converts it into a list of event objects.
         * An adapter is created and the list of events is passed.
         * The created adapter is passed to the RecyclerView in the layout.
         */
        viewModel.eventsRef.addSnapshotListener { it, _ ->
            val listEvent = it!!.toObjects(Event::class.java)
            val filteredList = listEvent.filter { it.creatorId == viewModel.currentUserRef.id }
            val adapter = EventAdapter(filteredList)
            binding.profileRV.adapter = adapter
        }
    }
}
