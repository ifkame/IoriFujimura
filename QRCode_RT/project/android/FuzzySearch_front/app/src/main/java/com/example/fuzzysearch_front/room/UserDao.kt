package com.example.fuzzysearch_front.room

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun gAllUser(): List<User>

    @Insert
    fun iUser(user: User)

    @Update
    fun uUser(user: User)

    @Query("DELETE FROM user")
    fun dAllUser()

    @Delete
    fun dUser(user: User)
}