package com.example.merk

import androidx.room.TypeConverter

class ListConverter {
    @TypeConverter
    fun fromListToString(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun fromStringToList(value: String?): List<String>? {
        return value?.split(",")?.map { it }
    }
}
