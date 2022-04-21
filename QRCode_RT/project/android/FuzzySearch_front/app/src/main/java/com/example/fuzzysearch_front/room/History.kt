package com.example.fuzzysearch_front.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    //列： 履歴番号(履歴の古い順)
    @PrimaryKey(autoGenerate = false)
    var history_number: Int,

    //列： 翻訳した日付
    @ColumnInfo(name = "his_date")
    var history_date: String,

    //列： 翻訳前_言語
    @ColumnInfo(name = "t_b_lang")
    var trans_before_lang: String,

    //列： 翻訳後_言語
    @ColumnInfo(name = "t_a_lang")
    var trans_after_lang: String,

    //列： 翻訳前_テキスト
    @ColumnInfo(name = "t_b_text")
    var trans_before_text: String,

    //列： 翻訳後_テキスト
    @ColumnInfo(name = "t_a_text")
    var trans_after_text: String
)
