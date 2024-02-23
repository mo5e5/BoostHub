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
import com.example.boosthub.R
import com.example.boosthub.data.adapter.ChatAdapter
import com.example.boosthub.data.datamodel.Chat
import com.example.boosthub.databinding.FragmentChatScreenBinding

class ChatScreenFragment : Fragment() {

    // The Binding object for the Fragment and the ViewModel are declared.
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

        // Sets a listener for changes in the chat reference.
        viewModel.chatsRef.addSnapshotListener { value, error ->

            if (error == null) {

                // Extracts the list of chats from the Firestore snapshot.
                val chatList: List<Pair<String, Chat>> = value!!.documents.map {
                    Pair(
                        it.id,
                        it.toObject(Chat::class.java)!!
                    )
                }

                // Filters the chats to show only those involving the current user.
                val filteredChatList = chatList.filter {
                    it.second.userList.contains(viewModel.auth.currentUser!!.uid)
                }

                // Adds the user to the chat that is not the current user.
                for ((id, chat) in filteredChatList) {
                    val otherUserId = chat.userList.first {
                        it != viewModel.auth.currentUser!!.uid
                    }
                    viewModel.addUserById(otherUserId)
                }

                // Creates an adapter and sets it for the Chat RecyclerView.
                val adapter = ChatAdapter(filteredChatList, viewModel)
                binding.chatsRV.adapter = adapter
            }

            // Sets the OnClickListener for the button to add a user to a chat.
            binding.chatAddUserBTN.setOnClickListener {

                val dialogBuilder = AlertDialog.Builder(requireContext())

                val inflater = requireActivity().layoutInflater
                val dialogView = inflater.inflate(R.layout.dialog_layout, null)
                val editText = dialogView.findViewById<EditText>(R.id.edit_text)

                dialogBuilder.setView(dialogView)
                dialogBuilder.setPositiveButton("add user") { _, _ ->

                    val email = editText.text.toString()
                    viewModel.createChatByEmail(email)
                }
                dialogBuilder.setNegativeButton("cancel") { _, _ ->
                }
                dialogBuilder.show()
            }
        }
    }
}