package com.example.boosthub.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Base URL for API requests.
const val BASE_URL = "https://nominatim.openstreetmap.org/"

// Creates a Moshi JSON converter using a KotlinJsonAdapterFactory.
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Creates an HTTP logging interceptor to log network activity.
private val logger: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

// Creates an OkHttpClient with the HTTP logging interceptor.
private val httpClient = OkHttpClient.Builder()
    .addInterceptor(logger)
    .build()

// Creates a Retrofit instance using MoshiConverterFactory for JSON conversion and the OkHttpClient.
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(httpClient)
    .build()

// Defines the API service for BoostHub.
interface BoostHubApiService {

    // GET request to retrieve location information based on a search term.
    @GET("search")
    suspend fun getLocation(
        @Query("q") searchterm: String, @Query("format") format: String = "json",
        @Query("polygon") polygon: Int = 1, @Query("addressdetails") addressDetails: Int = 1
    ): List<Location>
}

// Object for accessing the Retrofit service.
object BoostHubApi {
    val retrofitService: BoostHubApiService by lazy { retrofit.create(BoostHubApiService::class.java) }
}