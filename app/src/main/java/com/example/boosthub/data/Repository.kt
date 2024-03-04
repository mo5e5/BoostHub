package com.example.boosthub.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.boosthub.data.remote.Location
import com.example.boosthub.data.remote.BoostHubApi

// Repository for data management of the api call and the database.
class Repository(private val api: BoostHubApi) {


    //region API

    // LiveData for the location data.
    private val _location = MutableLiveData<List<Location>>()
    val location: LiveData<List<Location>>
        get() = _location

    /**
     * Function to retrieve location data based on a search term.
     * @param searchterm will be filled later as text input
     */
    suspend fun getLocation(searchterm: String) {
        try {
            _location.value = api.retrofitService.getLocation(searchterm)
        } catch (e: Exception) {
            Log.e("Repo", "$e")
        }
    }

    //endregion
}