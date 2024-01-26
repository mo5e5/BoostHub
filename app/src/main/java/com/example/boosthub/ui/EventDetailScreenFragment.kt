package com.example.boosthub.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.boosthub.MainViewModel
import com.example.boosthub.databinding.FragmentEventDetailScreenBinding

class EventDetailScreenFragment : Fragment() {

    private lateinit var binding: FragmentEventDetailScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventDetailScreenBinding.inflate(layoutInflater)
        return binding.root
    }
}