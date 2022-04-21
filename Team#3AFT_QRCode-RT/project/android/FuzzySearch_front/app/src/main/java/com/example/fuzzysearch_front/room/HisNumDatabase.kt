package com.example.fuzzysearch_front.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HisNum::class], version = 1, exportSchema = false)
abstract class HisNumDatabase : RoomDatabase() {
    abstract fun hisNumDao(): HisNumDao

    companion object{
        private var INSTANCE: HisNumDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): HisNumDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, HisNumDatabase::class.java, "hisnum.db")
                        // Version違ってMigrationなかったら消しちゃう
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
                return INSTANCE!!
            }
        }
    }
}