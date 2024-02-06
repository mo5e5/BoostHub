package com.example.boosthub.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.boosthub.MainViewModel
import com.example.boosthub.data.adapter.HomeChatAdapter
import com.example.boosthub.data.adapter.HomeEventAdapter
import com.example.boosthub.data.datamodel.Chat
import com.example.boosthub.data.datamodel.Event
import com.example.boosthub.databinding.FragmentHomeScreenBinding
import com.example.boosthub.testing.listOfChat
import com.example.boosthub.testing.listOfEvent
import com.example.boosthub.testing.listOfMessage

class HomeScreenFragment : Fragment() {

    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The SnapHelper ensures that the RecyclerView always jumps to the current list item.
        val eventHelper: SnapHelper = PagerSnapHelper()
        val chatHelper: SnapHelper = PagerSnapHelper()
        eventHelper.attachToRecyclerView(binding.eventRV)
        chatHelper.attachToRecyclerView(binding.chatRV)

//        viewModel.uploadEvent(listOfEvent.first())
//        viewModel.addChat(listOfChat.first())
//        viewModel.addMessageToChat("9iJROXX06GmIwn5EUvcT", listOfMessage)

        viewModel.eventRef.addSnapshotListener { it, _ ->
            val listEvent = it!!.toObjects(Event::class.java)
            val adapter = HomeEventAdapter(listEvent)
            binding.eventRV.adapter = adapter
        }

        viewModel.chatRef.addSnapshotListener { it, _ ->
            val listChat = it!!.toObjects(Chat::class.java)
            val adapter = HomeChatAdapter(listChat)
            binding.chatRV.adapter = adapter
        }
    }
}