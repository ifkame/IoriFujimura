package com.example.wisperapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

open class M_00_01 : AppCompatActivity() {
    //初期設定
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
    }

    //メニューの作成の関数
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        //メニューのリソース選択
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    //メニューのアイテムを押下した時の処理の関数
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            //タイムライン(action_timeline)ボタンを押したとき
            R.id.action_timeline -> {
                // タイムライン(S_01_01)クラスを呼び出すIntentを生成
                val intent = Intent(this, S_01_01::class.java)
                // Intent呼び出しを実行する
                startActivity(intent)
                //return true
            }
            //検索(action_search)ボタンを押したとき
            R.id.action_search -> {
                // 検索(S_02_01)クラスを呼び出すIntentを生成
                val intent = Intent(this, S_02_01::class.java)
                // Intent呼び出しを実行する
                startActivity(intent)
                //return true
            }
            //ウィスパー(action_whisper)ボタンを押したとき
            R.id.action_whisper -> {
                // ウィスパー(S_03_01)クラスを呼び出すIntentを生成
                val intent = Intent(this, S_03_01::class.java)
                // Intent呼び出しを実行する
                startActivity(intent)
                //return true
            }
            //Myプロフィール(action_my_pro)ボタンを押したとき
            R.id.action_my_pro -> {
                //選択されたユーザーIDに自分のユーザーIDを格納
                Defines.SELECT_USER_ID = Defines.USER_ID
                // Myプロフィール(S_05_01)クラスを呼び出すIntentを生成
                val intent = Intent(this, S_05_01::class.java)
                // Intent呼び出しを実行する
                startActivity(intent)
                //return true
            }
            //プロフィール編集(action_pro_edit)ボタンを押したとき
            R.id.action_pro_edit -> {
                // プロフィール編集(S_06_01)クラスを呼び出すIntentを生成
                val intent = Intent(this, S_06_01::class.java)
                // Intent呼び出しを実行する
                startActivity(intent)
                //return true
            }
        }
        //whenで指定している以外のIDが取得したときに何も返されなくなるのを防ぐ
        return super.onOptionsItemSelected(item)
    }
}