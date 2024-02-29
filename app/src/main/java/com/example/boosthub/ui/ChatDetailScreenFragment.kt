package com.example.boosthub.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.boosthub.MainViewModel
import com.example.boosthub.data.adapter.MessageAdapter
import com.example.boosthub.data.datamodel.Message
import com.example.boosthub.databinding.FragmentChatDetailScreenBinding


class ChatDetailScreenFragment : Fragment() {

    // The binding object for the fragment and the ViewModel are declared, the arguments passed are also picked up.
    private lateinit var binding: FragmentChatDetailScreenBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val args: ChatDetailScreenFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatDetailScreenBinding.inflate(layoutInflater)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the chat ID from the navigation arguments.
        val chatId = args.chatId

        // Sets a listener for changes in the message reference.
        viewModel.getMessageRef(chatId).addSnapshotListener { value, error ->

            if (error == null) {

                val massageList: List<Message> =
                    value!!.toObjects(Message::class.java).sortedBy { it.timestamp }

                val adapter = MessageAdapter(viewModel,massageList, viewModel.auth.currentUser!!.uid)
                binding.chatDetailMassagesRV.adapter = adapter

                // Scroll to the last message when the list is updated.
                binding.chatDetailMassagesRV.layoutManager?.scrollToPosition(adapter.itemCount - 1)
            }
        }

        // Sets the click listener for the send button.
        binding.sendBTN.setOnClickListener {

            val message = binding.chatDetailInputMassageTIET.text.toString()
            binding.chatDetailInputMassageTIET.setText("")
            viewModel.addMessageToChat(message, chatId)
        }
    }
}