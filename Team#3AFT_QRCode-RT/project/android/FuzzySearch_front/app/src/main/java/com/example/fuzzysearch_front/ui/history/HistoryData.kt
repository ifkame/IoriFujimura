package com.example.fuzzysearch_front.ui.history

import java.util.*

data class HistoryData(
    val historyCnt: Int,         //翻訳結果(履歴件数)カウント
    val historyDate: String,        //翻訳した日時
    val transBeforeLang: String,    //翻訳前_言語
    val transAfterLang: String,     //翻訳後_言語
    val transBeforeText: String,    //翻訳前_テキスト
    val transAfterText: String      //翻訳後_テキスト
)
