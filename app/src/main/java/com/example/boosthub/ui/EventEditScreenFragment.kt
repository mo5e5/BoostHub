package com.example.boosthub.ui

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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

        /**
         * The listener for clicking on the "Upload" button will be added.
         * The input data is saved from the text fields and the image.
         * The event will be uploaded and it will navigate to the previous view.
         */
        binding.eventEditUploadMTB.setOnClickListener {

            if (imageShort == null) {
                imageShort = Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                            "com.example.boosthub.ui" + "/mipmap-hdpi" + "/" +
                            resources.getResourceEntryName(R.mipmap.ic_logo_boosthub))
                binding.eventEditImageSIV.load(imageShort)
                Log.d("imageShort","$imageShort")
            }

            val image = viewModel.eventImageUrl.toString()
            val whatsUp = binding.eventEditWhatsUpTIET.text.toString()
            val location = binding.eventEditLocationTIET.text.toString()
            val date = binding.eventEditDateTIET.text.toString()
            val whosThere = binding.eventEditWhosThereTIET.text.toString()
            val whatElse = binding.eventEditWhatElseTIET.text.toString()
            val restrictions = binding.eventEditRestrictionsTIET.text.toString()

//            "/ic_logo_boosthub"+
//            mipmap-hdpi/ic_logo_boosthub.webp
            viewModel.uploadEvent(
                Event(
                    image = image,
                    whatsUp = whatsUp,
                    location = location,
                    date = date,
                    whosThere = whosThere,
                    whatElse = whatElse,
                    restrictions = restrictions
                ),imageShort!!
            )
            findNavController().navigateUp()
        }

        /**
         * Image click listener is added to start the image selection activity.
         */
        binding.eventEditImageSIV.setOnClickListener {
            getContent.launch("image/*")
        }

        /**
         * Add click listeners for the "Delete" button to navigate to the previous view.
         */
        binding.eventEditDeleteMTB.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}