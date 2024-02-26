package com.example.boosthub.ui

import android.content.ContentResolver
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
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.boosthub.MainViewModel
import com.example.boosthub.R
import com.example.boosthub.data.datamodel.Event
import com.example.boosthub.databinding.FragmentEventEditScreenBinding

class EventEditScreenFragment : Fragment() {

    // The binding object for the fragment and the ViewModel are declared, the arguments passed are also picked up.
    private lateinit var binding: FragmentEventEditScreenBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val args: EventEditScreenFragmentArgs by navArgs()

    // URI object to store the selected image.
    private var imageShort: Uri? = null

    // ActivityResultLauncher to start the image selection activity and handle the result.
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imageShort = uri
                binding.eventEditImageSIV.load(imageShort)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventEditScreenBinding.inflate(layoutInflater)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the event ID from the navigation arguments.
        val eventId = args.eventId

        // Variable to store the current event's image URI.
        var imageEvent = ""

        // Clear the current event data in ViewModel.
        viewModel.clearEvent()

        // Check if the event is being edited or uploaded as a new event.
        if (eventId != "0") {

            // If editing an existing event, fetch the event details from ViewModel.
            viewModel.getEventById(args.eventId)

            // Adjust visibility of buttons based on editing/uploading mode.
            binding.eventEditDeleteMBTN.visibility = View.VISIBLE
            binding.eventEditEditMBTN.visibility = View.VISIBLE
            binding.eventEditUploadMBTN.visibility = View.GONE
        } else {

            // If uploading a new event, adjust button visibility accordingly.
            binding.eventEditDeleteMBTN.visibility = View.GONE
            binding.eventEditEditMBTN.visibility = View.GONE
            binding.eventEditUploadMBTN.visibility = View.VISIBLE
        }

        // Sets the OnClickListener for the button to upload a new event.
        binding.eventEditUploadMBTN.setOnClickListener {

            // Check if an image is selected; if not, load a default image.
            if (imageShort == null) {
                imageShort = Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                            requireContext().packageName + "/" +
                            R.drawable.boosthub
                )
                binding.eventEditImageSIV.load(imageShort)
            }

            // Retrieve input data from text fields and other sources.
            val image = viewModel.eventImageUrl.toString()
            val whatsUp = binding.eventEditWhatsUpTIET.text.toString()
            val location = binding.eventEditLocationTIET.text.toString()
            val date = binding.eventEditDateTIET.text.toString()
            val whosThere = binding.eventEditWhosThereTIET.text.toString()
            val whatElse = binding.eventEditWhatElseTIET.text.toString()
            val restrictions = binding.eventEditRestrictionsTIET.text.toString()
            val creatorId = viewModel.currentUserRef.id

            // Check if required fields are filled out; if not, display a toast.
            if (whatsUp.isEmpty() || location.isEmpty() || date.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "it is necessary that all required fields are filled out",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                // If uploading a new event, upload the event data and navigate to previous view.
                if (eventId == "0") {
                    viewModel.uploadEvent(
                        Event(
                            image = image,
                            whatsUp = whatsUp,
                            location = location,
                            date = date,
                            whosThere = whosThere,
                            whatElse = whatElse,
                            restrictions = restrictions,
                            creatorId = creatorId,
                        ), imageShort!!
                    )
                }
                findNavController().navigateUp()
            }
        }

        // Sets the OnClickListener for the button to edit a event.
        binding.eventEditEditMBTN.setOnClickListener {

            // If no image is selected, load either the existing event's image or a default image.
            if (imageShort == null) {
                imageShort = if (imageEvent == "") {
                    Uri.parse(
                        ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                                requireContext().packageName + "/" +
                                R.drawable.boosthub
                    )
                } else {
                    Uri.parse(imageEvent)
                }
                binding.eventEditImageSIV.load(imageShort)
            }

            // Retrieve input data from text fields and other sources.
            val image = viewModel.currentEvent.value!!.image
            val whatsUp = binding.eventEditWhatsUpTIET.text.toString()
            val location = binding.eventEditLocationTIET.text.toString()
            val date = binding.eventEditDateTIET.text.toString()
            val whosThere = binding.eventEditWhosThereTIET.text.toString()
            val whatElse = binding.eventEditWhatElseTIET.text.toString()
            val restrictions = binding.eventEditRestrictionsTIET.text.toString()
            val creatorId = viewModel.currentUserRef.id

            // Check if required fields are filled out; if not, display a toast.
            if (whatsUp.isEmpty() || location.isEmpty() || date.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "it is necessary that all required fields are filled out",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // If editing an existing event, update the event data and navigate to previous view.
                viewModel.updateEvent(
                    Event(
                        image = image,
                        whatsUp = whatsUp,
                        location = location,
                        date = date,
                        whosThere = whosThere,
                        whatElse = whatElse,
                        restrictions = restrictions,
                        creatorId = creatorId,
                        eventId = eventId
                    ), imageShort!!,
                    eventId
                )
            }
            findNavController().navigateUp()
        }

        // Image click listener to start the image selection activity.
        binding.eventEditImageSIV.setOnClickListener {
            getContent.launch("image/*")
        }

        // Click listeners for the "Cancel" and "Delete" buttons to navigate to the previous view.
        binding.eventEditCancelMBTN.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.eventEditDeleteMBTN.setOnClickListener {

            // Delete the event and navigate to the previous view.
            viewModel.deleteEvent(eventId)
            findNavController().navigateUp()
        }

        // Observe changes in the current event and update UI accordingly.
        viewModel.currentEvent.observe(viewLifecycleOwner) {
            if (it.image == "") {
                binding.eventEditImageSIV.load(R.drawable.boosthub)
            } else {
                binding.eventEditImageSIV.load(it.image)
            }
            binding.eventEditWhatsUpTIET.setText(it.whatsUp)
            binding.eventEditLocationTIET.setText(it.location)
            binding.eventEditDateTIET.setText(it.date)
            binding.eventEditWhosThereTIET.setText(it.whosThere)
            binding.eventEditWhatElseTIET.setText(it.whatElse)
            binding.eventEditRestrictionsTIET.setText(it.restrictions)
            imageEvent = it.image
        }
    }
}