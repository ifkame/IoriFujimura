package com.example.fuzzysearch_front.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Lang(
    //列： 言語ID(言語を指定する番号)
    @PrimaryKey(autoGenerate = false)
    var lang_id: Int,

    //列： 選択できる言語
    @ColumnInfo(name = "language")
    var select_lang: String
)
