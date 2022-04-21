package com.example.fezzytest

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity1 : AppCompatActivity()/*, LayOutClickListner*/ {

    lateinit var mAdapter: CustomAdapter
    lateinit var mAnimalList: ArrayList<Animal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        //val textView = findViewById<TextView>(android.R.id.home_before_text)
        //textView.movementMethod = ScrollingMovementMethod()


        // データの作成
//        val dog = Animal("イヌ", 3, R.mipmap.ic_launcher)
//        val cat = Animal("ネコ", 2, R.mipmap.ic_launcher)
//        val elephant = Animal("ゾウ", 10, R.mipmap.ic_launcher)
//        val horse = Animal("ウマ", 4, R.mipmap.ic_launcher)
//        val lion = Animal("ライオン", 6, R.mipmap.ic_launcher)
        val lang = Animal("英語","Translation")
        val lang1 = Animal("日本語","翻訳")
        mAnimalList = arrayListOf(lang, lang1)

        // RecyclerViewの取得
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        // LayoutManagerの設定
        recyclerView.layoutManager = LinearLayoutManager(this)

        // CustomAdapterの生成と設定
        mAdapter = CustomAdapter(mAnimalList)
        recyclerView.adapter = mAdapter
    }

    /*override fun LayoutTapped(custom: Animal, position: Int) {

    }*/
}