package com.uvnb195.todoapp.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

object DateUtils {

    fun asDate(localDate: LocalDate): Date {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
    }
    fun asLocalDate(date: Date): LocalDate {
        return Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDate()
    }
}

