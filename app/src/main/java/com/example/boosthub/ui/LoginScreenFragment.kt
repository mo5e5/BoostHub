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
import com.example.boosthub.databinding.FragmentLoginScreenBinding

class LoginScreenFragment : Fragment() {

    // The Binding object for the Fragment and the ViewModel are declared.
    private lateinit var binding: FragmentLoginScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up a click listener to log in to a profile.
        binding.loginBTN.setOnClickListener {

            val email = binding.emailTIET.text.toString()
            val password = binding.passwordTIET.text.toString()

            // Checks whether all necessary fields are filled out. Displays a toast if not so.
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "it is necessary that all fields are filled out",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                viewModel.login(
                    email,
                    password
                )
            }
        }

        // Observes the user state LiveData object. If a user exists, it navigates to the HomeScreenFragment.
        viewModel.user.observe(viewLifecycleOwner)
        {
            if (it != null) {
                findNavController().navigate(R.id.homeScreenFragment)
            }
        }

        // Click listeners for the "signup" button to navigate to the signup screen.
        binding.signupBTN.setOnClickListener {
            findNavController().navigate(R.id.signUpScreenFragment)
        }
    }
}