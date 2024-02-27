package com.example.boosthub.data.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.boosthub.MainViewModel
import com.example.boosthub.data.datamodel.Chat
import com.example.boosthub.data.datamodel.Event
import com.example.boosthub.data.datamodel.User
import com.example.boosthub.databinding.ItemChatBinding
import com.example.boosthub.ui.ChatScreenFragmentDirections
import com.google.firebase.firestore.toObject

class ChatAdapter(private val dataset: List<Pair<String, Chat>>, val viewModel: MainViewModel) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding =
            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        // Gets the chat object from the dataset at the given location.
        val pair = dataset[position]
        val chatId = pair.first
        val chat = pair.second

        // Gets the list of users for this chat.
        val userList = chat.userList

        // Check if the chat is a group chat.
        if (chat.group) {

            // Fetch the chat document from Firestore.
            viewModel.chatsRef.document(chatId).get().addOnSuccessListener {
                val chatObject = it.toObject<Chat>()!!

                // Retrieve the associated event ID from the chat.
                viewModel.eventsRef.document(chatObject.eventId!!).get()
                    .addOnSuccessListener { event ->
                        val eventObjekt = event.toObject<Event>()!!
                        holder.binding.itemChatContactImageSIV.load(eventObjekt.image)
                        holder.binding.itemChatContactNameMTV.text = eventObjekt.whatsUp
                    }
            }
        } else {

            // Determines the ID of the other user in the chat.
            val otherUserId = userList.firstOrNull {
                it != viewModel.auth.currentUser!!.uid
            }

            // Loads the other user's image and name.
            if (!otherUserId.isNullOrEmpty()) {
                viewModel.userRef.document(otherUserId).get().addOnSuccessListener {
                    val userObjekt = it.toObject<User>()!!
                    holder.binding.itemChatContactImageSIV.load(userObjekt.image)
                    holder.binding.itemChatContactNameMTV.text = userObjekt.userName
                }
            }
        }

        // Navigates to the chat detail view when the chat item is clicked.
        holder.binding.itemChatMCV.setOnClickListener {
            val navController = holder.itemView.findNavController()
            navController.navigate(
                ChatScreenFragmentDirections.actionChatScreenFragmentToChatDetailScreenFragment(
                    chatId
                )
            )
        }

        // Displays a delete dialog when the chat item is long pressed.
        holder.binding.itemChatMCV.setOnLongClickListener {
            val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
            dialogBuilder.apply {
                setTitle("delete chat")
                setMessage("are you sure you want to delete this chat?")
                setPositiveButton("delete chat") { _, _ ->
                    viewModel.deleteChat(chatId)
                }
                setNegativeButton("cancel") { _, _ ->
                }
                show()
            }
            true
        }
    }
}