package com.example.fuzzysearch_front.room

import androidx.room.*

@Dao
interface HisNumDao {
    @Query("SELECT his_number FROM hisnum")
    fun gAllHisNum(): List<Int>

    @Insert
    fun iHisNum(hisnum: HisNum)

    @Update
    fun uHisNum(hisnum: HisNum)

    @Query("DELETE FROM hisnum")
    fun dAllHisNum()

    @Query("DELETE FROM hisnum WHERE his_number = :number")
    fun dHisNum(number: Int)
}