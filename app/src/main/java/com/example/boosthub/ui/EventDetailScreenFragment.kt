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
import com.example.boosthub.data.datamodel.Chat
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

        /**
         * Sets an onClickListener for a button to toggle selection state.
         * If the button is clicked, it toggles the selection state in the ViewModel.
         * Depending on the selection state, the user is either added or removed from chats associated with the event.
         * Only non-creator users are affected by this action.
         */
        binding.eventDetailThumbUpDownIBTN.setOnClickListener {
            viewModel.toggleSelection()

            val userId = viewModel.auth.currentUser!!.uid
            val eventId = args.eventId

            if (viewModel.isSelected.value == true) {
                if (userId != args.creatorId) {
                    viewModel.chatsRef
                        .whereEqualTo("eventId", eventId)
                        .get()
                        .addOnSuccessListener {
                            for (document in it) {
                                val chatId = document.id
                                viewModel.addUserToChat(chatId = chatId, userIdToAdd = userId)
                            }
                        }
                }
            }
            if (viewModel.isSelected.value == false) {
                if (userId != args.creatorId) {
                    viewModel.chatsRef
                        .whereEqualTo("eventId", eventId)
                        .get()
                        .addOnSuccessListener {
                            for (document in it) {
                                val chatId = document.id
                                viewModel.deleteUserFromChat(
                                    chatId = chatId,
                                    userIdToDelete = userId
                                )
                            }
                        }
                }
            }
        }

        /**
         * Sets up a snapshot listener to monitor changes in the chat collection.
         * This listener updates the color of a button based on the user's presence in any chat associated with the event.
         * If the user is in any chat for the specified event, the button color is set to green; otherwise, it is set to red.
         */
        viewModel.chatsRef.addSnapshotListener { value, error ->
            if (error == null) {

                val currentUserId = viewModel.auth.currentUser!!.uid
                val eventId = args.eventId

                // Extract chat information from the snapshot.
                val chatList: List<Pair<String, Chat>> = value!!.documents.map {
                    Pair(
                        it.id,
                        it.toObject(Chat::class.java)!!
                    )
                }

                var userInAnyChat = false

                /**
                 * Iterate through each chat in the list.
                 * Checks then if the chat is associated with the specified event if the current user is in the chat's user list.
                 *  And update then the flag if the user is found in any chat.
                 */
                for ((chatId, chat) in chatList) {
                    if (chat.eventId == eventId) {
                        val userList: List<String> = chat.userList

                        val isUserInList = userList.contains(currentUserId)

                        if (isUserInList) {
                            userInAnyChat = true
                        }
                    }
                }

                // Update button color based on user's presence in any chat.
                val colorId = if (userInAnyChat) R.color.green else R.color.red
                binding.eventDetailThumbUpDownIBTN.setColorFilter(
                    ContextCompat.getColor(
                        binding.eventDetailThumbUpDownIBTN.context,
                        colorId
                    ), PorterDuff.Mode.SRC_IN
                )
            }
        }
    }
}

