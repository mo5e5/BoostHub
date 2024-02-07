package com.example.boosthub.data.datamodel

import com.google.firebase.Timestamp

data class Message(
    val content: String = "",
    val senderId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
)
