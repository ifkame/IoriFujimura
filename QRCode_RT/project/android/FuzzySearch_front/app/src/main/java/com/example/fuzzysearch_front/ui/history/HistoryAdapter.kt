package com.example.fuzzysearch_front.ui.history

import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fuzzysearch_front.R


/**
 * 履歴表示アダプター（未定）
 * textView: 値[Number (0 ~ 100)]
 */
class HistoryAdapter internal constructor(private var HistoryDataList: List<HistoryData>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    // Viewの初期化
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val historyCnt: TextView            //翻訳結果(履歴件数)カウント
        val historyDate: TextView           //翻訳した日時
        val transBeforeLang: TextView       //翻訳前_言語
        val transAfterLang: TextView        //翻訳後_言語
        val transBeforeText: TextView       //翻訳前_テキスト
        val transAfterText: TextView        //翻訳後_テキスト

        //レイアウトとアダプター内で使用する型を紐づける
        init {
            historyCnt = view.findViewById(R.id.history_history_count)              //翻訳結果(履歴件数)カウントオブジェクト生成
            historyDate = view.findViewById(R.id.history_date)                      //翻訳した日時オブジェクト生成
            transBeforeLang = view.findViewById(R.id.history_trans_before_lang)     //翻訳前_言語オブジェクト生成
            transAfterLang = view.findViewById(R.id.history_trans_after_lang)       //翻訳後_言語オブジェクト生成
            transBeforeText = view.findViewById(R.id.history_trans_before_text)     //翻訳前_テキストオブジェクト生成
            transAfterText = view.findViewById(R.id.history_trans_after_text)       //翻訳後_テキストオブジェクト生成

            transBeforeText.movementMethod = ScrollingMovementMethod()
            transBeforeText.movementMethod = ScrollingMovementMethod()
        }
    }
    
    // レイアウトの設定
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        //履歴表示用のRecyclerViewのパラメータ格納
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.history_item, viewGroup, false)
        return ViewHolder(view)     //履歴表示用のアイテムリスト生成
    }

    // RecyclerViewの設定(作成)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        //履歴表示の ViewHolderのレイアウトに与えられた値を設定
        val HistoryData = HistoryDataList[position]   //指定された配列リストのデータをそれぞれの変数に格納

        //ユーザー詳細の ViewHolderのレイアウトに与えられた値を設定
        viewHolder.historyCnt.text = HistoryData.historyCnt.toString()      //翻訳結果(履歴件数)カウント → historyCnt
        viewHolder.historyDate.text = HistoryData.historyDate               //翻訳した日時 → historyDate
        viewHolder.transBeforeLang.text = HistoryData.transBeforeLang       //翻訳前_言語 → transBeforeLang
        viewHolder.transAfterLang.text = HistoryData.transAfterLang         //翻訳後_言語 → transAfterLang
        viewHolder.transBeforeText.text = HistoryData.transBeforeText       //翻訳前_テキスト → transBeforeText
        viewHolder.transAfterText.text = HistoryData.transAfterText         //翻訳後_テキスト → transAfterText
    }

    // 表示数を返す
    override fun getItemCount() = HistoryDataList.size

    /**
     * RecycleViewにあるデータを全件取得させるメソッド。
     */
    fun getItem(position: Int): List<HistoryData>{
        return listOf(HistoryDataList[position])
    }

    /**
     * リストデータを追加して画面に反映させるメソッド。
     */
    fun add(listData: List<HistoryData>) {
        this.HistoryDataList += listData
        notifyDataSetChanged()
    }

    /**
     * リストデータを一件削除して画面に反映させるメソッド。
     */
    fun del(position: Int){
        var tempHistoryDataList = HistoryDataList
        var indices: List<Int> = listOf()
        for (i in tempHistoryDataList.indices) {
            if (i != position) {
                indices += i
            }
        }
        Log.d("indicesの値", "$indices")
        Log.d("消される履歴", "${tempHistoryDataList[position]}")
        Log.d("残す履歴データ", "${tempHistoryDataList.slice(indices)}")
        this.HistoryDataList = listOf()
        this.HistoryDataList = tempHistoryDataList.slice(indices)
        Log.d("残っているデータ(Adapter)", "$HistoryDataList")
    }

    /**
     * リストデータを全件削除して画面に反映させるメソッド。
     */
    fun delAll(){
        this.HistoryDataList = listOf()
        notifyDataSetChanged()
    }
}