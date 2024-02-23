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

    // Binding object for the activity layout.
    private lateinit var binding: ActivityMainBinding

    // ViewModel instance using by viewModels delegate.
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the NavHostFragment using its ID.
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        // Set up the bottom navigation with the NavController.
        binding.bottomNavigationView.setupWithNavController(navHost.navController)

        // Listen for changes in the destination and hide/show bottom navigation accordingly.
        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {

                // Hide bottom navigation for specific destinations.
                R.id.loginScreenFragment -> binding.bottomNavigationView.visibility = View.GONE
                R.id.signUpScreenFragment -> binding.bottomNavigationView.visibility = View.GONE
                R.id.chatDetailScreenFragment -> binding.bottomNavigationView.visibility = View.GONE
                R.id.eventEditScreenFragment -> binding.bottomNavigationView.visibility = View.GONE
                R.id.eventDetailScreenFragment -> binding.bottomNavigationView.visibility = View.GONE
                R.id.profileEditScreenFragment -> binding.bottomNavigationView.visibility = View.GONE
                else -> binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }

        // Handle back press navigation using OnBackPressedCallback.
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.fragmentContainerView.findNavController().navigateUp()
            }
        })

        // Observe toast LiveData in ViewModel and show toast message when it's not empty.
        viewModel.toast.observe(this) {
            if (!it.isNullOrEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.emptyLifeData()
            }
        }
    }
}