package com.example.boosthub.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.boosthub.data.datamodel.Chat
import com.example.boosthub.databinding.ItemHomeChatBinding

class HomeChatAdapter(private val dataset: List<Pair<String,Chat>>) :
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
    }
}