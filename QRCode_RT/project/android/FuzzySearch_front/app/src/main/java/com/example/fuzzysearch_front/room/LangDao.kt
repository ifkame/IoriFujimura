package com.example.fuzzysearch_front.room

import androidx.room.*

@Dao
interface LangDao {
    @Query("SELECT * FROM lang")
    fun gAllLang(): List<Lang>

    @Insert
    fun iLang(lang: Lang)

    @Update
    fun uLang(lang: Lang)

    @Query("DELETE FROM lang")
    fun dAllLang()
}