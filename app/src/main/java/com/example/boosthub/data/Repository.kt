package com.example.boosthub.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.boosthub.data.datamodel.Location
import com.example.boosthub.data.remote.BoostHubApi

class Repository(private val api: BoostHubApi) {

    private val _location = MutableLiveData<List<Location>>()
    val location: LiveData<List<Location>>
        get() = _location

    suspend fun getLocation(searchterm: String) {
        try {
            _location.value = api.retrofitService.getLocation(searchterm)
        } catch (e: Exception) {
            Log.e("Repo", "$e")
        }
    }
}