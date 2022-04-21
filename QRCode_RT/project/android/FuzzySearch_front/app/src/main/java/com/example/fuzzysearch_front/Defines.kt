package com.example.fuzzysearch_front

import android.widget.ImageView
import com.example.fuzzysearch_front.R

interface Defines {
    companion object{
        /*
           LocalHostのアドレス
           ホスト ループバック インターフェースへの特殊エイリアス
           （開発マシンの 127.0.0.1 など）
         */
        /*
            constをつけるとgetXXX()の処理がなくなるのでメソッド数が減る。
            constをつけるとgetXXX()メソッドを一つ挟まずに直接変数にアクセスする。
         */
        const val ACTUAL_LOCAL = "localhost"        //実機端末のLocalhostアドレス
        const val VIRTUALITY_LOCAL = "10.0.2.2"     //仮想端末のLocalhostアドレス
        const val NEW_REG_URL = "http://${ACTUAL_LOCAL}:5000/new_user_reg"    //新規登録時に使用するURL
        const val LOGIN_URL = "http://${ACTUAL_LOCAL}:5000/login"             //ログイン時に使用するURL
        const val GET_LANG_URL = "http://${ACTUAL_LOCAL}:5000/get_lang_code"  //言語取得時に使用するURL
        const val TRANSLATE_URL = "http://${ACTUAL_LOCAL}:5000/translate"  //言語取得時に使用するURL
        const val HISTORY_URL = "http://${ACTUAL_LOCAL}:5000/history"  //言語取得時に使用するURL

        //自分のトークン("id"と"lang_code"を暗号化したもの)
        var MY_TOKEN : String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwibGFuZ19jb2RlIjoiREEifQ.DxNv9Ch6uNV85HL-XYfvXdOsvzjDk8-jVTEApVrKDpQ"
        //ログイン情報
        var LOGIN = mutableMapOf<String, Any>(
            "id" to "",
            "user" to "",
            "pass" to "",
            "lang" to "",
            "token" to ""
        ).withDefault { "default" }
        //翻訳言語リスト(初期設定：空配列)
        var TRANSLATE_LANGUAGE: ArrayList<String> = ArrayList<String>()
    }
}