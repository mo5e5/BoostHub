package com.example.boosthub.misc

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object DateUtils {
    fun toSimpleString(date: Date): String {
        val format = SimpleDateFormat("dd/MM/yyy HH:mm:ss")
        return format.format(date)
    }
}