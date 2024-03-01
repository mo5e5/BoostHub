package com.example.boosthub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
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

            // Hide bottom navigation for specific destinations.
            when (destination.id) {
                R.id.loginScreenFragment,
                R.id.signUpScreenFragment,
                R.id.chatDetailScreenFragment,
                R.id.eventEditScreenFragment,
                R.id.eventDetailScreenFragment,
                R.id.profileEditScreenFragment -> {
                    if (binding.bottomNavigationView.visibility == View.VISIBLE) {
                        binding.bottomNavigationView.visibility = View.GONE
                        binding.bottomNavigationView.startAnimation(
                            AnimationUtils.loadAnimation(
                                this,
                                android.R.anim.fade_out
                            )
                        )
                    }
                }

                else -> {
                    if (binding.bottomNavigationView.visibility != View.VISIBLE) {
                        binding.bottomNavigationView.visibility = View.VISIBLE
                        binding.bottomNavigationView.startAnimation(
                            AnimationUtils.loadAnimation(
                                this,
                                android.R.anim.fade_in
                            )
                        )
                    }
                }
            }

            // Sets an animation if you switch between fragments.
            when (destination.id) {
                R.id.loginScreenFragment,
                R.id.signUpScreenFragment,
                R.id.homeScreenFragment,
                R.id.profileScreenFragment,
                R.id.profileEditScreenFragment,
                R.id.chatScreenFragment,
                R.id.chatDetailScreenFragment,
                R.id.eventDetailScreenFragment,
                R.id.eventEditScreenFragment -> {
                    binding.fragmentContainerView.startAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            android.R.anim.fade_in
                        )
                    )
                }
            }
        }

        // Handle back press navigation using OnBackPressedCallback.
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = binding.fragmentContainerView.findNavController()
                navController.navigateUp()
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