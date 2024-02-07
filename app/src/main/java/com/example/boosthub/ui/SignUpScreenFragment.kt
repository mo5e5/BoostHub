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

        /*
            The click listener for the SignUp button is set.
            The email and password prompts will be retrieved.
            Check whether all fields are filled out.
            A message is displayed if not all fields are filled out.
            Check that the passwords you entered match.
            The password is retrieved from the first input field.
            The ViewModel's signup function is called.
            A message will appear if the passwords do not match.
        */
        binding.signupBTN.setOnClickListener {

            val email = binding.emailTIET.text.toString()
            val passwordFirst = binding.passwordFirstTIET.text.toString()
            val passwordSecond = binding.passwordSecondTIET.text.toString()

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

                val password = binding.passwordFirstTIET.text.toString()

                viewModel.signup(
                    binding.emailTIET.text.toString(),
                    password
                )
            } else {
                Toast.makeText(requireContext(), "passwords do not match", Toast.LENGTH_LONG).show()
            }
        }

        /*
            The Observer for Users in the ViewModel is set.
            If a user exists, it navigates to the HomeScreenFragment.
        */
        viewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(R.id.homeScreenFragment)
            }
        }

        // Navigates back to the LoginScreenFragment.
        binding.backBTN.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}