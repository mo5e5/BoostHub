package com.example.boosthub.ui

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.boosthub.MainViewModel
import com.example.boosthub.R
import com.example.boosthub.data.datamodel.Event
import com.example.boosthub.databinding.FragmentEventEditScreenBinding

class EventEditScreenFragment : Fragment() {

    /**
     * The Binding object for the Fragment and the ViewModel are declared.
     */
    private lateinit var binding: FragmentEventEditScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    private val args: EventEditScreenFragmentArgs by navArgs()

    /**
     * URI object to store the selected image.
     */
    private var imageShort: Uri? = null

    /**
     * The ActivityResultLauncher is used to start the image selection activity.
     * The selected URI is saved and the image is loaded in the ImageView.
     */
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = args.eventId

        /**
         * The listener for clicking on the "Upload" button will be added.
         * If no image has been selected, a uri for default image is created and loaded.
         * The input data is saved from the text fields and the image.
         * If certain fields are left blank, a message will be issued via a toast that tells the user to fill them in.
         * Only then can an event be uploaded
         * The event will be uploaded and it will navigate to the previous view.
         */
        binding.eventEditUploadMBTN.setOnClickListener {

            if (imageShort == null) {
                imageShort = Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                            requireContext().packageName + "/" +
                            R.drawable.boosthub
                )
                binding.eventEditImageSIV.load(imageShort)
            }

            val image = viewModel.eventImageUrl.toString()
            val whatsUp = binding.eventEditWhatsUpTIET.text.toString()
            val location = binding.eventEditLocationTIET.text.toString()
            val date = binding.eventEditDateTIET.text.toString()
            val whosThere = binding.eventEditWhosThereTIET.text.toString()
            val whatElse = binding.eventEditWhatElseTIET.text.toString()
            val restrictions = binding.eventEditRestrictionsTIET.text.toString()
            val creatorId = viewModel.currentUserRef.id

            if (whatsUp.isEmpty() || location.isEmpty() || date.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "it is necessary that all required fields are filled out",
                    Toast.LENGTH_LONG
                ).show()
            } else {
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

        binding.eventEditEditMBTN.setOnClickListener {

            if (imageShort == null) {
                imageShort = Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                            requireContext().packageName + "/" +
                            R.drawable.boosthub
                )
                binding.eventEditImageSIV.load(imageShort)
            }

            val image = viewModel.eventImageUrl.toString()
            val whatsUp = binding.eventEditWhatsUpTIET.text.toString()
            val location = binding.eventEditLocationTIET.text.toString()
            val date = binding.eventEditDateTIET.text.toString()
            val whosThere = binding.eventEditWhosThereTIET.text.toString()
            val whatElse = binding.eventEditWhatElseTIET.text.toString()
            val restrictions = binding.eventEditRestrictionsTIET.text.toString()
            val creatorId = viewModel.currentUserRef.id

            if (whatsUp.isEmpty() || location.isEmpty() || date.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "it is necessary that all required fields are filled out",
                    Toast.LENGTH_LONG
                ).show()
            } else {
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


        /**
         * Image click listener is added to start the image selection activity.
         */
        binding.eventEditImageSIV.setOnClickListener{
            getContent.launch("image/*")
        }

        if (eventId != "0") {
            viewModel.getEventById(args.eventId)
        }

        /**
         * Add click listeners for the "Delete" button to navigate to the previous view.
         */
        binding.eventEditCancelMBTN.setOnClickListener{
            findNavController().navigateUp()
        }

        if (eventId != "0") {
            binding.eventEditDeleteMBTN.visibility = View.VISIBLE
            binding.eventEditEditMBTN.visibility = View.VISIBLE
            binding.eventEditUploadMBTN.visibility = View.GONE
        } else {
            binding.eventEditDeleteMBTN.visibility = View.GONE
            binding.eventEditEditMBTN.visibility = View.GONE
            binding.eventEditUploadMBTN.visibility = View.VISIBLE
        }

        binding.eventEditDeleteMBTN.setOnClickListener{
            viewModel.deleteEvent(eventId)
            findNavController().navigateUp()
        }

        viewModel.currentEvent.observe(viewLifecycleOwner)
        {
            binding.eventEditImageSIV.load(it.image)
            binding.eventEditWhatsUpTIET.setText(it.whatsUp)
            binding.eventEditLocationTIET.setText(it.location)
            binding.eventEditDateTIET.setText(it.date)
            binding.eventEditWhosThereTIET.setText(it.whosThere)
            binding.eventEditWhatElseTIET.setText(it.whatElse)
            binding.eventEditRestrictionsTIET.setText(it.restrictions)
        }
    }
}