package com.example.boosthub.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.boosthub.MainViewModel
import com.example.boosthub.data.adapter.MessageAdapter
import com.example.boosthub.data.datamodel.Message
import com.example.boosthub.databinding.FragmentChatDetailScreenBinding

class ChatDetailScreenFragment : Fragment() {

    private lateinit var binding: FragmentChatDetailScreenBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val args: ChatDetailScreenFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatDetailScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatId = args.chatId

        viewModel.getMessageRef(chatId).addSnapshotListener { value, error ->
            Log.d("messageI", value.toString())
            if (error == null) {
                Log.d("messageII", value.toString())

                val massageList: List<Message> =
                    value!!.toObjects(Message::class.java).sortedBy { it.timestamp }
                Log.d("messageIII", massageList.toString())
                val adapter = MessageAdapter(massageList, viewModel.auth.currentUser!!.uid)
                binding.chatDetailMassagesRV.adapter = adapter
            }
        }

        binding.sendBTN.setOnClickListener {
            val message = binding.chatDetailInputMassageTIET.text.toString()
            binding.chatDetailInputMassageTIET.setText("")
            viewModel.addMessageToChat(message, chatId)
        }
    }
}