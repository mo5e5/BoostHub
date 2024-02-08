package com.example.boosthub.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.boosthub.MainViewModel
import com.example.boosthub.R
import com.example.boosthub.data.datamodel.User
import com.example.boosthub.databinding.FragmentProfileScreenBinding


class ProfileScreenFragment : Fragment() {

    private lateinit var binding: FragmentProfileScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    private var imageShort: Uri? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                //uri enthält Verweis auf unser Bild, damit können wir weiterarbeiten
                imageShort = uri
                binding.profileImageSIV.load(imageShort)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileSaveBTN.setOnClickListener {

            val currentPassword = binding.profileCurrentPasswordTIET.text.toString()
            val passwordFirst = binding.profilePasswordTIET.text.toString()
            val passwordSecond = binding.profilePasswordConfirmTIET.text.toString()

            val userName = binding.profileNameTIET.text.toString()

            if (userName.isNotEmpty()) {
                viewModel.updateUserName(userName)
                Toast.makeText(requireContext(), "name updated", Toast.LENGTH_LONG).show()
            }

            if (currentPassword.isNotEmpty() && passwordFirst.isNotEmpty() && passwordSecond.isNotEmpty()) {
                if (passwordFirst == passwordSecond) {
                    viewModel.changePassword(passwordFirst, currentPassword)
                } else {
                    Toast.makeText(requireContext(), "passwords do not match", Toast.LENGTH_LONG)
                        .show()
                }
            }

            if (imageShort != null) {
                viewModel.uploadProfileImage(imageShort!!)
                Toast.makeText(requireContext(), "image updated", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.userRef.addSnapshotListener { snapshot, _ ->

            val user = snapshot?.toObject(User::class.java)!!

            if (user.image != "") {
                binding.profileImageSIV.load(user.image)
            }
        }

        binding.profileImageSIV.setOnClickListener {
            getContent.launch("image/*")
        }

        /*
           The ViewModel's logout function is called to log out the user and
           then navigates to the LoginScreenFragment.
        */
        binding.profileLogoutBTN.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.loginScreenFragment)
        }
    }
}