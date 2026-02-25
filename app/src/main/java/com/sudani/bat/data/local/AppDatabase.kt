package com.sudani.bat.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sudani.bat.data.model.SudaniNumber

@Database(entities = [SudaniNumber::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun numberDao(): NumberDao
}
