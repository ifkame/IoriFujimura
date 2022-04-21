package com.example.fuzzysearch_front.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HisNum(
    //列： 履歴追加番号(履歴の古い順)
    @PrimaryKey(autoGenerate = false)
    var hisnum_id: Int,

    //削除した履歴番号(表示しない番号)
    @ColumnInfo
    var his_number: Int
)
