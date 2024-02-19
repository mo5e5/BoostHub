package com.example.boosthub.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.boosthub.MainViewModel
import com.example.boosthub.data.datamodel.Chat
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

        val pair = dataset[position]

        val chatId = pair.first

        val chat = pair.second

        val userList = chat.userList

        val otherUserId = userList.firstOrNull {
            it != viewModel.auth.currentUser!!.uid
        }

        if (!otherUserId.isNullOrEmpty()) {
            viewModel.userRef.document(otherUserId).get().addOnSuccessListener {
                val userObjekt = it.toObject<User>()!!
                holder.binding.itemChatContactImageSIV.load(userObjekt.image)
                holder.binding.itemChatContactNameMTV.text = userObjekt.userName
            }
        }

        holder.binding.itemChatMCV.setOnClickListener {
            val navController = holder.itemView.findNavController()
            navController.navigate(
                ChatScreenFragmentDirections.actionChatScreenFragmentToChatDetailScreenFragment(
                    chatId
                )
            )
        }
    }
}