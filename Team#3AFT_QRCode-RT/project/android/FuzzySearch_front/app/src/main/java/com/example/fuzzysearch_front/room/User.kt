package com.example.fuzzysearch_front.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    //主キー： ID
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    //列： Eメールアドレス
    @ColumnInfo(name = "user_name")
    var email: String,

    //列： パスワード
    @ColumnInfo(name = "password")
    var password: String,

    //列： 言語ID(言語を指定する番号)
    @ColumnInfo(name = "lang_id")
    var lang_id: Int,

    /**
     * ユーザーの自動生成IDと言語IDを暗号化したもの
     * 列: トークン(ユーザー作成時に発行されるもの)
     */
    @ColumnInfo(name = "token")
    var token: String
)
