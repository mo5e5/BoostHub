package com.example.boosthub.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import com.example.boosthub.MainViewModel
import com.example.boosthub.data.adapter.ChatAdapter
import com.example.boosthub.data.datamodel.Chat
import com.example.boosthub.databinding.FragmentChatScreenBinding

class ChatScreenFragment : Fragment() {

    private lateinit var binding: FragmentChatScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.chatsRef.addSnapshotListener { value, error ->

            if (error == null) {

                val chatList: List<Pair<String, Chat>> = value!!.documents.map {
                    Pair(
                        it.id,
                        it.toObject(Chat::class.java)!!
                    )
                }

                val filteredChatList = chatList.filter {
                    it.second.userList.contains(viewModel.auth.currentUser!!.uid)
                }

                for ((id, chat) in filteredChatList) {
                    val otherUserId = chat.userList.first {
                        it != viewModel.auth.currentUser!!.uid
                    }
                    viewModel.addUserById(otherUserId)
                }

                val adapter = ChatAdapter(filteredChatList,viewModel)
                binding.chatsRV.adapter = adapter
            }

            binding.testBtn.setOnClickListener {

                val dialogBuilder = AlertDialog.Builder(requireContext())

                val editText = EditText(requireContext())
                dialogBuilder.setView(editText)
                dialogBuilder.setPositiveButton("chat +") { _, _ ->
                    val email = editText.text.toString()
                    viewModel.createChatByEmail(email)
                }
                dialogBuilder.setNegativeButton("chat -") { _, _ ->

                }
                dialogBuilder.show()
            }
        }
    }
}