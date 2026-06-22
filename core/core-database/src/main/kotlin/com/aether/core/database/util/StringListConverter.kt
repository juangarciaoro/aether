package com.aether.core.database.util

import androidx.room.TypeConverter

class StringListConverter {
    @TypeConverter
    fun fromList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split(",")
}
