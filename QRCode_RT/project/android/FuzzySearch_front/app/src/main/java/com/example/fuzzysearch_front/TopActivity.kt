package com.example.fuzzysearch_front

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.fuzzysearch_front.room.RoomFunction
import com.example.fuzzysearch_front.room.User
import com.example.fuzzysearch_front.room.UserDao
import com.example.fuzzysearch_front.room.UserDatabase

class TopActivity : AppCompatActivity() {
    var roomRequest = RoomFunction()    //Room処理クラス呼び出し
    lateinit var mUserDao: UserDao      //ユーザーDao(アプリ内のユーザーデータベースへの抽象アクセスを可能にするメソッド)
    lateinit var mUserAdapter: ArrayAdapter<String>     //使用するユーザー情報を格納するデータリスト
    private var mUserList: List<User> = listOf()    //ユーザー情報リスト

    private lateinit var LoginButton: Button
    private lateinit var CreateButton: Button
    private lateinit var ForgetText: TextView
    private lateinit var user_listview: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

        title = resources.getString(R.string.title_top)     //タイトルを「トップ」に変更
        titleColor = resources.getColor(R.color.purple_700) //タイトル色を「濃い青」に変更

        //UI部品
        LoginButton = findViewById<Button>(R.id.top_login_button)
        CreateButton = findViewById<Button>(R.id.top_create_button)
        ForgetText = findViewById<TextView>(R.id.top_forget_text)
        user_listview = findViewById<ListView>(R.id.top_user_list)    //ユーザーデータを確認できるリストビュー

        //アプリ内のユーザーデータベースのインスタンス生成
        mUserDao = UserDatabase.getInstance(this).userDao()
        //ユーザー配列の設定
        mUserAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListOf())

        user_listview.adapter = mUserAdapter    //ユーザーデータベースの行を全件表示

        roomRequest.setUserRoom(mUserDao, mUserAdapter)     //ユーザーデータベース使用のための情報を RoomFunctionに渡す
        mUserList = roomRequest.getUserList()       //ユーザー情報を配列に格納

        ForgetText.visibility = View.GONE   //パスワードを忘れた人の画面遷移テキストを非表示

        //ユーザー登録されていないとき
        if (mUserList.isEmpty()) {
            //btnLogin.visibility = View.INVISIBLE        //登録していない人はログインできないように非表示
            LoginButton.setBackgroundColor(resources.getColor(R.color.btn_no_act))      //ログインボタンを「非アクティブButtonカラー」色に変更
            CreateButton.setBackgroundColor(resources.getColor(R.color.btn_act))        //新規登録ボタンを「アクティブButtonカラー」色に変更
            Toast.makeText(this, "ユーザー登録画面へ遷移", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        } else {
            LoginButton.setBackgroundColor(resources.getColor(R.color.btn_act))         //ログインボタンを「アクティブButtonカラー」色に変更
            CreateButton.setBackgroundColor(resources.getColor(R.color.btn_no_act))     //新規登録ボタンを「非アクティブButtonカラー」色に変更
            //btnNewCreate.visibility = View.INVISIBLE    //登録している人は新規登録する必要がないので非表示
        }

        //ログインボタンを押したとき
        LoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //新規登録ボタンを押したとき
        CreateButton.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }

        //パスワード忘れたボタンを押したとき
        ForgetText.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }
    }

    //Activityに処理が戻って来たとき
    override fun onRestart() {
        Log.d("画面遷移", "私が戻ってきた！")
        roomRequest.getUser()                   // ユーザー情報の再読み込み
        mUserList = roomRequest.getUserList()   //ユーザー情報をroomクラスから取得
        //ユーザー登録されていないとき
        if (mUserList.isEmpty()) {
            //btnLogin.visibility = View.INVISIBLE        //登録していない人はログインできないように非表示
            LoginButton.setBackgroundColor(resources.getColor(R.color.btn_no_act))      //ログインボタンを「非アクティブButtonカラー」色に変更
            CreateButton.setBackgroundColor(resources.getColor(R.color.btn_act))        //新規登録ボタンを「アクティブButtonカラー」色に変更
        } else {
            LoginButton.setBackgroundColor(resources.getColor(R.color.btn_act))         //ログインボタンを「アクティブButtonカラー」色に変更
            CreateButton.setBackgroundColor(resources.getColor(R.color.btn_no_act))     //新規登録ボタンを「非アクティブButtonカラー」色に変更
            //btnNewCreate.visibility = View.INVISIBLE    //登録している人は新規登録する必要がないので非表示
        }
        super.onRestart()
    }
}