package com.example.boosthub.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.boosthub.MainViewModel
import com.example.boosthub.R
import com.example.boosthub.data.datamodel.User
import com.example.boosthub.databinding.FragmentProfileEditScreenBinding


class ProfileEditScreenFragment : Fragment() {

    //The Binding object for the Fragment and the ViewModel are declared.
    private lateinit var binding: FragmentProfileEditScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    // URI object to store the selected image.
    private var imageShort: Uri? = null

    // ActivityResultLauncher to start the image selection activity and handle the result.
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imageShort = uri
                binding.profileEditImageSIV.load(imageShort)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEditScreenBinding.inflate(layoutInflater)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up OnClickListener for the save button.
        binding.profileEditSaveBTN.setOnClickListener {

            // Get user input data.
            val currentPassword = binding.profileEditCurrentPasswordTIET.text.toString()
            val password = binding.profileEditPasswordTIET.text.toString()
            val passwordConfirm = binding.profileEditPasswordConfirmTIET.text.toString()
            val userName = binding.profileEditNameTIET.text.toString()
            val currentCars = binding.profileEditCurrentCarsTIET.text.toString()

            // Update user data if name is not empty.
            if (userName.isNotEmpty()) {
                viewModel.updateUserData(userName, currentCars)
                Toast.makeText(requireContext(), "data updated", Toast.LENGTH_LONG).show()
            }

            // Change password if all fields are not empty and passwords match.
            if (currentPassword.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty()) {
                if (password == passwordConfirm) {
                    viewModel.changePassword(password, currentPassword)
                } else {
                    Toast.makeText(requireContext(), "passwords do not match", Toast.LENGTH_LONG)
                        .show()
                }
            }

            // Upload profile image if it is not null.
            if (imageShort != null) {
                viewModel.uploadProfileImage(imageShort!!)
                Toast.makeText(requireContext(), "image updated", Toast.LENGTH_LONG).show()
            }
        }

        // Set up a listener to fetch current user data and update UI.
        viewModel.currentUserRef.addSnapshotListener { snapshot, _ ->

            val user = snapshot?.toObject(User::class.java)!!

            // Load user image into ImageView if not empty.
            if (user.image != "") {
                binding.profileEditImageSIV.load(user.image)
            }

            // Set user name and current cars in corresponding TextViews.
            binding.profileEditNameTIET.setText(user.userName)
            binding.profileEditCurrentCarsTIET.setText(user.currentCars)
        }

        // Set up OnClickListener for selecting profile image.
        binding.profileEditImageSIV.setOnClickListener {
            getContent.launch("image/*")
        }

        // Set up OnClickListener for logout button.
        binding.profileEditLogoutBTN.setOnClickListener {

            // Logout user and navigate to login screen.
            viewModel.logout()
            findNavController().navigate(R.id.loginScreenFragment)
        }
    }
}