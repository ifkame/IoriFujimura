package com.example.fuzzysearch_front.room

import androidx.room.*

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun gAllHistory(): List<History>

    @Query("SELECT * FROM history WHERE history_number NOT IN (:indices)")
    fun gHistory(indices: HashSet<Int>): List<History>

    @Insert
    fun iHistory(history: History)

    @Update
    fun uHistory(history: History)

    @Query("DELETE FROM history")
    fun dAllHistory()

    @Query("DELETE FROM history WHERE history_number = :h_number")
    fun dHistory(h_number: Int)
}