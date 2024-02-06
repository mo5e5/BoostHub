package com.example.boosthub.data.datamodel

import com.google.firebase.Timestamp

data class Message(
    val uid: String = "",
    val timestamp: Timestamp = Timestamp.now() ,
    val text: String = "",
)
