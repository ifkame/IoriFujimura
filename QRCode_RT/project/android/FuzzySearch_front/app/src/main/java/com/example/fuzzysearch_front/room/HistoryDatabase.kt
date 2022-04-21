package com.example.fuzzysearch_front.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [History::class], version = 1, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object{
        private var INSTANCE: HistoryDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): HistoryDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, HistoryDatabase::class.java, "history.db")
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