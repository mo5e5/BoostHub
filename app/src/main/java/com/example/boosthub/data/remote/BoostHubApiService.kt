package com.example.boosthub.data.remote

import com.example.boosthub.data.datamodel.remote.Location
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://nominatim.openstreetmap.org/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val logger: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val httpClient = OkHttpClient.Builder()
    .addInterceptor(logger)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(httpClient)
    .build()

interface BoostHubApiService {

    @GET("search")
    suspend fun getLocation(
        @Query("q") searchterm: String, @Query("format") format: String = "json",
        @Query("polygon") polygon: Int = 1, @Query("addressdetails") addressDetails: Int = 1
    ): List<Location>

}

object BoostHubApi {
    val retrofitService: BoostHubApiService by lazy { retrofit.create(BoostHubApiService::class.java) }
}