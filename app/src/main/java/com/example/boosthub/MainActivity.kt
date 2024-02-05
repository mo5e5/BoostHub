package com.example.boosthub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.boosthub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The Binding object is created and the corresponding layout is infiltrated.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
            The Navigation Host Fragment is searched for and found in the layout based on its ID.
            The NavController for bottom navigation is configured.
         */
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHost.navController)

        /*
            A listener for navigation destination changes is added.
            The visibility of the bottom navigation is adjusted based on the current destination.
            If the target is the LoginScreenFragment or SignUpScreenFragment, the bottom navigation is hidden.
            For other destinations the bottom navigation is displayed.
         */
        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.loginScreenFragment -> binding.bottomNavigationView.visibility = View.GONE
                R.id.signUpScreenFragment -> binding.bottomNavigationView.visibility = View.GONE
                else -> binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }

        /*
            The onBackPressedDispatcher is configured to trigger the Navigation Up event for the NavController.
            When the back button is pressed, the navigation up event is triggered for the NavController
         */
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.fragmentContainerView.findNavController().navigateUp()
            }
        })

        /*
           The toast message observer in the ViewModel is set.
           Toast messages are displayed when the LiveData for toast messages changes.
        */
        viewModel.toast.observe(this) {
            if (!it.isNullOrEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.emptyLifeData()
            }
        }
    }
}