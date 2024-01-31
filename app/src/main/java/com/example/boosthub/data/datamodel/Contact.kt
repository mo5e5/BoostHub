package com.example.boosthub.data.datamodel

import android.net.http.UrlRequest

data class Contact(
    val id: Long,
    val email: String,
    val password: String,
    val name: String,
    val image: String,
)
