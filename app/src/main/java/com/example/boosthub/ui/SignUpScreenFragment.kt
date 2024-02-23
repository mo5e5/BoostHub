package com.example.boosthub.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.boosthub.MainViewModel
import com.example.boosthub.R
import com.example.boosthub.databinding.FragmentSignUpScreenBinding

class SignUpScreenFragment : Fragment() {

    // The Binding object for the Fragment and the ViewModel are declared.
    private lateinit var binding: FragmentSignUpScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up OnClickListener for the signup button.
        binding.signupBTN.setOnClickListener {

            // Retrieve email and passwords from text fields.
            val email = binding.emailTIET.text.toString()
            val passwordFirst = binding.passwordFirstTIET.text.toString()
            val passwordSecond = binding.passwordSecondTIET.text.toString()

            // Check if any of the fields are empty.
            if (email.isEmpty() ||
                passwordFirst.isEmpty() ||
                passwordSecond.isEmpty()
            ) {
                Toast.makeText(
                    requireContext(),
                    "it is necessary that all fields are filled out",
                    Toast.LENGTH_LONG
                ).show()
            } else if (passwordFirst == passwordSecond) {

                // If passwords match, sign up the user.
                val password = binding.passwordFirstTIET.text.toString()

                viewModel.signup(
                    binding.emailTIET.text.toString(),
                    password
                )
            } else {
                // Display toast message if passwords do not match.
                Toast.makeText(requireContext(), "passwords do not match", Toast.LENGTH_LONG).show()
            }
        }

        // Observe the user LiveData in the ViewModel and navigate back to the home screen if user is null.
        viewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(R.id.homeScreenFragment)
            }
        }

        // Set up OnClickListener for the back button this navigates back to the previous screen.
        binding.backBTN.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}