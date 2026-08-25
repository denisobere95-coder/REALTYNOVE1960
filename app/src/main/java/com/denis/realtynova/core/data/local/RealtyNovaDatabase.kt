package com.denis.realtynova.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PropertyEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RealtyNovaDatabase : RoomDatabase() {
    abstract val propertyDao: PropertyDao
}
