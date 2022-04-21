package com.example.fuzzysearch_front.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Lang::class], version = 1, exportSchema = false)
abstract class LangDatabase : RoomDatabase() {
    abstract fun langDao(): LangDao

    companion object{
        private var INSTANCE: LangDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): LangDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, LangDatabase::class.java, "Lang.db")
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