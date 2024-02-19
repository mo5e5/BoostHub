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
import com.example.boosthub.databinding.FragmentProfileScreenBinding


class ProfileEditScreenFragment : Fragment() {

    /**
     * The Binding object for the Fragment and the ViewModel are declared.
     */
    private lateinit var binding: FragmentProfileScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    /**
     * URI object to store the selected image.
     */
    private var imageShort: Uri? = null

    /**
     * ActivityResultLauncher to start the image selection activity.
     * URI of the selected image is saved and the image in the ImageView is loaded.
     */
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imageShort = uri
                binding.profileImageSIV.load(imageShort)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileScreenBinding.inflate(layoutInflater)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Add click listener for the "Save" button.
         * User input is retrieved and functions
         * are executed.
         */
        binding.profileSaveBTN.setOnClickListener {

            val currentPassword = binding.profileCurrentPasswordTIET.text.toString()
            val password = binding.profilePasswordTIET.text.toString()
            val passwordConfirm = binding.profilePasswordConfirmTIET.text.toString()

            val userName = binding.profileNameTIET.text.toString()

            /**
             * Username is updated if the input field is not empty.
             */
            if (userName.isNotEmpty()) {
                viewModel.updateUserName(userName)
                Toast.makeText(requireContext(), "name updated", Toast.LENGTH_LONG).show()
            }
            /**
             * Password is changed when all password fields are filled in and when
             * the new password and its control field match.
             */
            if (currentPassword.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty()) {
                if (password == passwordConfirm) {
                    viewModel.changePassword(password, currentPassword)
                } else {
                    Toast.makeText(requireContext(), "passwords do not match", Toast.LENGTH_LONG)
                        .show()
                }
            }

            /**
             * Profile picture updates when a picture is selected.
             */
            if (imageShort != null) {
                viewModel.uploadProfileImage(imageShort!!)
                Toast.makeText(requireContext(), "image updated", Toast.LENGTH_LONG).show()
            }
        }

        /**
         * Listener for changes to the user data in the ViewModel.
         * User data is obtained from the snapshot.
         * Profile image is loaded if an image is present in the user profile.
         * Username is placed in the text field.
         */
        viewModel.currentUserRef.addSnapshotListener { snapshot, _ ->

            val user = snapshot?.toObject(User::class.java)!!

            if (user.image != "") {
                binding.profileImageSIV.load(user.image)
            }

            binding.profileNameTIET.setText(user.userName)
        }

        /**
         * Click listener for adding a profile picture to start the picture selection activity.
         */
        binding.profileImageSIV.setOnClickListener {
            getContent.launch("image/*")
        }

        /**
         * The ViewModel's logout function is called to log out the user and
         * then navigates to the LoginScreenFragment.
         */
        binding.profileLogoutBTN.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.loginScreenFragment)
        }
    }
}