package com.example.boosthub.ui

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.boosthub.MainViewModel
import com.example.boosthub.R
import com.example.boosthub.databinding.FragmentEventDetailScreenBinding

class EventDetailScreenFragment : Fragment() {

    // The binding object for the fragment and the ViewModel are declared, the arguments passed are also picked up.
    private lateinit var binding: FragmentEventDetailScreenBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val args: EventDetailScreenFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventDetailScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gets the data and inserts it into the corresponding views.
        binding.eventDetailImageSIV.load(args.image)
        binding.eventDetailWhatsUpInputMTV.text = args.whatsUp
        binding.eventDetailLocationInputMTV.text = args.location
        binding.eventDetailDateInputMTV.text = args.date
        binding.eventDetailWhosThereInputMTV.text = args.whosThere
        binding.eventDetailWhatElseInputMTV.text = args.whatElse
        binding.eventDetailRestrictionsInputMTV.text = args.restrictions

        // Observes location data changes and updates map navigation intent.
        viewModel.location.observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {

                val dataset = it[0]

                val lat = dataset.lat
                val lon = dataset.lon
                val searchterm = args.location
                val intentString = "geo:$lat,$lon?q=$searchterm"

                // Opens Google Maps with the destination location when the Location TextView is clicked.
                binding.eventDetailLocationInputMTV.setOnClickListener {

                    val gmmIntentUri = Uri.parse(intentString)
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }
            }
        }

        // Gets the location data for the specified event.
        viewModel.getLocation(args.location)

        // These variables store the current state of the buttons
        var isThumbUpSelected = false
        var isThumbDownSelected = false

        // SetOnClickListener for the thumbs up button.
        binding.eventDetailThumbUpIBTN.setOnClickListener {
            isThumbUpSelected = !isThumbUpSelected

            if (isThumbUpSelected) {
                binding.eventDetailThumbUpIBTN.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    ), PorterDuff.Mode.SRC_IN
                )

                // Reset the color of the other button.
                binding.eventDetailThumbDownIBTN.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey
                    ), PorterDuff.Mode.SRC_IN
                )
                isThumbDownSelected = false
            } else {

                // Reset the color of the button to the default color.
                binding.eventDetailThumbUpIBTN.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey
                    ), PorterDuff.Mode.SRC_IN
                )
            }
        }

        // SetOnClickListener for the thumbs down button.
        binding.eventDetailThumbDownIBTN.setOnClickListener {
            isThumbDownSelected = !isThumbDownSelected

            if (isThumbDownSelected) {
                binding.eventDetailThumbDownIBTN.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    ), PorterDuff.Mode.SRC_IN
                )

                // Reset the color of the other button.
                binding.eventDetailThumbUpIBTN.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey
                    ), PorterDuff.Mode.SRC_IN
                )
                isThumbUpSelected = false
            } else {

                // Reset the color of the button to the default color.
                binding.eventDetailThumbDownIBTN.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey
                    ), PorterDuff.Mode.SRC_IN
                )
            }
        }
    }
}
