package com.example.boosthub.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.boosthub.data.datamodel.Chat
import com.example.boosthub.databinding.ItemChatBinding
import com.example.boosthub.ui.ChatScreenFragmentDirections

class ChatAdapter(private val dataset: List<Pair<String, Chat>>) :
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

        val chat = dataset[position]

        val chatId = chat.first


        holder.binding.itemChatContactNameMTV.text = chatId


        holder.binding.itemChatMCV.setOnClickListener {

            val navController = holder.itemView.findNavController()

            navController.navigate(ChatScreenFragmentDirections.actionChatScreenFragmentToChatDetailScreenFragment(chatId))
        }
    }
}