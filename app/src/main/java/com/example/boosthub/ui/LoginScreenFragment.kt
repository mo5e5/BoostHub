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

    /*
        The click listener for the login button is set.
        The email and password prompts will be retrieved.
        It is checked whether all fields are filled out.
        A message is displayed if not all fields are filled out.
        The login function of the ViewModel is called.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBTN.setOnClickListener {

            val email = binding.emailTIET.text.toString()
            val password = binding.passwordTIET.text.toString()

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

        /*
            The Observer for Users in the ViewModel is set.
            If a user exists, it navigates to the HomeScreenFragment.
        */
        viewModel.user.observe(viewLifecycleOwner)
        {
            if (it != null) {
                findNavController().navigate(R.id.homeScreenFragment)
            }
        }

        /*
           The click listener for the SignUp button is set.
           Navigation to the SignUpScreenFragment is carried out.
        */
        binding.signupBTN.setOnClickListener {
            findNavController().navigate(R.id.signUpScreenFragment)
        }
    }
}