package com.example.boosthub.data.datamodel

import android.os.Message

data class Chat(
    val id: Long,
    val profile: Profile,
    val lastMessage: Message,
)