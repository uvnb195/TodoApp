package com.uvnb195.todoapp.data

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestampToLong(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun fromLongToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}