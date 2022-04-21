package com.example.room_example

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)    //
    val id: Int,

    var name: String,

    //var mailaddress: String,
    //var password: String,
    //var language: String,
    //var token: String,

    @ColumnInfo(name = "nenrei")
    var age: Int
)