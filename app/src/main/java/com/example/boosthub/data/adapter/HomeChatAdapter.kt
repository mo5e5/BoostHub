package com.example.boosthub.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.boosthub.data.datamodel.Chat
import com.example.boosthub.databinding.ItemHomeChatBinding

class HomeChatAdapter(private val dataset: List<Chat>) :
    RecyclerView.Adapter<HomeChatAdapter.HomeChatViewHolder>() {

    inner class HomeChatViewHolder(val binding: ItemHomeChatBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeChatViewHolder {
        val binding =
            ItemHomeChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeChatViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: HomeChatViewHolder, position: Int) {


        val chat = dataset[position]

//        holder.binding.chatContactImageSIV.load(chat.uidI)

//        if () {
//            holder.binding.newCatSIV.visibility = View.VISIBLE
//        } else {
//            holder.binding.newCatSIV.visibility = View.GONE
//        }
    }
}