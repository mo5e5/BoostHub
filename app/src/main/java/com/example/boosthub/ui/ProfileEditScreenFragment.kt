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

    /**
     * The Binding object for the Fragment and the ViewModel are declared.
     */
    private lateinit var binding: FragmentProfileEditScreenBinding
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

        /**
         * Add click listener for the "Save" button.
         * User input is retrieved and functions
         * are executed.
         */
        binding.profileEditSaveBTN.setOnClickListener {

            val currentPassword = binding.profileEditCurrentPasswordTIET.text.toString()
            val password = binding.profileEditPasswordTIET.text.toString()
            val passwordConfirm = binding.profileEditPasswordConfirmTIET.text.toString()

            val userName = binding.profileEditNameTIET.text.toString()
            val currentCars = binding.profileEditCurrentCarsTIET.text.toString()

            /**
             * Username is updated if the input field is not empty.
             */
            if (userName.isNotEmpty()) {
                viewModel.updateUserData(userName,currentCars)
                Toast.makeText(requireContext(), "data updated", Toast.LENGTH_LONG).show()
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
                binding.profileEditImageSIV.load(user.image)
            }

            binding.profileEditNameTIET.setText(user.userName)
            binding.profileEditCurrentCarsTIET.setText(user.currentCars)
        }

        /**
         * Click listener for adding a profile picture to start the picture selection activity.
         */
        binding.profileEditImageSIV.setOnClickListener {
            getContent.launch("image/*")
        }

        /**
         * The ViewModel's logout function is called to log out the user and
         * then navigates to the LoginScreenFragment.
         */
        binding.profileEditLogoutBTN.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.loginScreenFragment)
        }
    }
}