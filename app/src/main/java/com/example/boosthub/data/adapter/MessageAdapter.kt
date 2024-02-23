package com.example.boosthub.data.adapter


import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.boosthub.data.datamodel.Message
import com.example.boosthub.databinding.ItemMessageBinding


class MessageAdapter(private val dataset: List<Message>, private val userId: String): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(val binding: ItemMessageBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

        // Gets the message object from the dataset at the given position.
        val message = dataset[position]

        // Sets the text of the message in the corresponding TextView.
        holder.binding.messageTV.text = message.content.trim()

        // Checks whether the message was sent by the current user or not.
        val myMessage: Boolean = (message.senderId == userId)

        //Sets the gravity of the layout based on the sender ID.
        if(myMessage){
            holder.binding.messageLL.gravity = Gravity.END
        } else {
            holder.binding.messageLL.gravity = Gravity.START
        }

    }
}