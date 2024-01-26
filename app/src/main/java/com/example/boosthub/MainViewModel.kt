package com.example.boosthub

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.boosthub.data.Repository
import com.example.boosthub.data.remote.BoostHubApi
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(BoostHubApi)


    val location = repository.location

    fun getLocation(searchterm: String) {
        viewModelScope.launch{
            try {
                repository.getLocation(searchterm)
            } catch (e:Exception) {
                Log.e("ViewM","$e")
            }
        }
    }
}