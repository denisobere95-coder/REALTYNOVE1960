package com.denis.realtynova.core.data.local

import androidx.room.TypeConverter
import com.denis.realtynova.core.domain.model.PropertyImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromPropertyImageList(value: List<PropertyImage>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPropertyImageList(value: String): List<PropertyImage>? {
        val listType = object : TypeToken<List<PropertyImage>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}
