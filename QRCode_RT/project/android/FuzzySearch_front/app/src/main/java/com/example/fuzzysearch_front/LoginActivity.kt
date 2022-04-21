package com.example.fuzzysearch_front

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.autofill.UserData
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fuzzysearch_front.room.RoomFunction
import com.example.fuzzysearch_front.room.User
import com.example.fuzzysearch_front.room.UserDao
import com.example.fuzzysearch_front.room.UserDatabase
import com.example.http_post.OkHttp3Callback


class LoginActivity : AppCompatActivity(), OkHttp3Callback.ApiLoginCallback {
    var apiRequest = OkHttp3Callback()  //API処理クラス呼び出し
    var roomRequest = RoomFunction()    //Room処理クラス呼び出し

    lateinit var mUserDao: UserDao      //ユーザーDao(アプリ内のユーザーデータベースへの抽象アクセスを可能にするメソッド)
    lateinit var mUserAdapter: ArrayAdapter<String>     //使用するユーザー情報を格納するデータリスト
    private var mUserList: List<User> = listOf()    //ユーザー情報リスト
    private var mUserCheckMap = mutableMapOf<String, Any>()     //ユーザー情報を連想配列で格納する変更可能なMap

    //UI部品
    private lateinit var userId: EditText           //ユーザーID
    private lateinit var password: EditText         //パスワード
    private lateinit var btnLogin: Button           //ログインボタン
    private lateinit var btnNewCreate: Button       //新規登録ボタン
    private lateinit var user_listview: ListView    //ユーザーデータを確認できるリストビュー
    private lateinit var focusView: TextView        //フォーカスを外すためのテキスト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = resources.getString(R.string.title_login)   //タイトルを「ログイン」に変更

        //レイアウトからID指定したオブジェクト取得
        userId = findViewById<EditText>(R.id.login_user_edit)
        password = findViewById<EditText>(R.id.login_password)
        btnLogin = findViewById<Button>(R.id.login_button)
        user_listview = findViewById<ListView>(R.id.login_user_list)
        focusView = findViewById<TextView>(R.id.login_focusView)

        //アプリ内のユーザーデータベースのインスタンス生成
        mUserDao = UserDatabase.getInstance(this).userDao()
        //ユーザー配列の設定
        mUserAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListOf())

        //user_listview.adapter = mUserAdapter    //ユーザーデータベースの行を全件表示

        roomRequest.setUserRoom(mUserDao, mUserAdapter)     //ユーザーデータベース使用のための情報を RoomFunctionに渡す
        mUserList = roomRequest.getUserList()       //ユーザー情報を配列に格納

        apiRequest.setApiLoginCallback(this)

        mUserCheckMap = roomRequest.mUserCheckMap   //最後のユーザー情報をログイン情報として取得
        if (!mUserCheckMap.isNullOrEmpty()) {       //ログイン情報が空またはNullでないとき
            userId.setText(mUserCheckMap["user"].toString())    //登録したユーザー(メアド)を自動入力
            password.setText(mUserCheckMap["pass"].toString())  //登録したパスワードを自動入力
            btnLogin.setBackgroundColor(resources.getColor(R.color.btn_act))     //ログインボタンを「アクティブButtonカラー」色に変更
            Defines.LOGIN = mUserCheckMap   //アプリ内で呼び出せるログイン情報を取得
        }

        //ユーザー登録されていないとき
//        if (mUserList.isEmpty()) {
//            //btnLogin.visibility = View.INVISIBLE        //登録していない人はログインできないように非表示
//            Toast.makeText(this, "ユーザー登録画面へ遷移", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this, CreateActivity::class.java)
//            startActivity(intent)
//        } else {
//            //btnNewCreate.visibility = View.INVISIBLE    //登録している人は新規登録する必要がないので非表示
//        }

        //ログインボタンを押したとき
        btnLogin.setOnClickListener {
            if (InputCheck()) {     //空白スペースor空の入力チェック(自作)で入力がされているとき
                apiRequest.getAPI_login(userId.text.toString(), password.text.toString())
                //apiRequest.getAPI_login("test2@gamil.com", "password")
            }
        }

        //ユーザーEditのフォーカスが変更したとき
        userId.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {    //フォーカスが外れたとき
                //InputMethodManager をキャストしながら取得
                //入力メソッドにアクセスするメソッド
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)   //キーボードを閉じるメソッド
                ChangeLoginBackground()     //対応したログインボタンの背景に変更
            }
        }

        //パスワードEditのフォーカスが変更したとき
        password.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {    //フォーカスが外れたとき
                //InputMethodManager をキャストしながら取得
                //入力メソッドにアクセスするメソッド
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)   //キーボードを閉じるメソッド
                ChangeLoginBackground()     //対応したログインボタンの背景に変更
            }
        }

        //キーボードが表示・非表示が変更されたとき
        setKeyboardVisibilityListener(object : OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                Log.i(
                    "Keyboard state",
                    if (visible) "Keyboard is active" else "Keyboard is Inactive"
                )
                if (!visible) { //キーボードを閉じたとき
                    //InputMethodManager をキャストしながら取得
                    //入力メソッドにアクセスするメソッド
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(focusView.windowToken, 0)   //キーボードを閉じるメソッド
                    ChangeLoginBackground()     //対応したログインボタンの背景に変更
                }
            }
        })
    }

    //タップイベントが起こったとき
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        focusView.requestFocus()    //強制的にフォーカスビューにフォーカスを当てる
        return super.dispatchTouchEvent(ev)
    }

    //キーボードの表示・非表示を検知するイベント
    interface OnKeyboardVisibilityListener {
        fun onVisibilityChanged(visible: Boolean)
    }

    //キーボードの表示・非表示を検知するイベント
    //時間がないので中身のコメントと意味の理解がないですm(_ _)m
    private fun setKeyboardVisibilityListener(onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {
        val parentView: TextView = findViewById(R.id.login_focusView) //.getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            private var alreadyOpen = false
            private val defaultKeyboardHeightDP = 100
            private val EstimatedKeyboardDP =
                defaultKeyboardHeightDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0
            private val rect: Rect = Rect()
            override fun onGlobalLayout() {
                val estimatedKeyboardHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    EstimatedKeyboardDP.toFloat(),
                    parentView.getResources().getDisplayMetrics()
                ).toInt()
                parentView.getWindowVisibleDisplayFrame(rect)
                val heightDiff: Int =
                    parentView.getRootView().getHeight() - (rect.bottom - rect.top)
                val isShown = heightDiff >= estimatedKeyboardHeight
                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...")
                    return
                }
                alreadyOpen = isShown
                onKeyboardVisibilityListener.onVisibilityChanged(isShown)
            }
        })
    }

    override fun login_success(obj: Any?) {
        Log.e("Login成功", obj.toString())
        Defines.MY_TOKEN = obj.toString()   //ログインしたユーザーのトークンを格納
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            Defines.LOGIN["user"] = userId.text.toString()
            Defines.LOGIN["pass"] = password.text.toString()
            Defines.LOGIN["lang"] = 7       //ログイン時、母語を決めることが出来ないのでEnglish (American)指定
            Defines.LOGIN["token"] = Defines.MY_TOKEN
            Log.d("ログイン情報", "${Defines.LOGIN}")
            if (mUserList.isEmpty()) {  //ログイン情報を新規登録して格納していない人のとき
                roomRequest.insertUser(
                    UserData = User(
                        mUserList.lastIndex+1,
                        Defines.LOGIN["user"].toString(),
                        Defines.LOGIN["pass"].toString(),
                        Defines.LOGIN["lang"] as Int,
                        Defines.LOGIN["token"].toString()
                    )
                )
            }

        }
        //翻訳画面に遷移する
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        //finish()        //このActivityを残さない
    }

    override fun login_failed(obj: String?) {
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            Toast.makeText(applicationContext, obj, Toast.LENGTH_SHORT).show()
        }
    }

    //入力チェックを行うメソッド
    fun InputCheck(): Boolean {
        if (userId.text.toString().trim().isNullOrEmpty()) {    //メールアドレスの入力がないとき
            Toast.makeText(this, "メールアドレスを入力してください", Toast.LENGTH_SHORT).show()
            userId.setText("")      //半角全角スペース除去
            btnLogin.setBackgroundColor(resources.getColor(R.color.btn_no_act))     //ログインボタンを「非アクティブButtonカラー」色に変更
            return false
        }
        if (password.text.toString().trim().isNullOrEmpty()) {  //パスワードの入力がないとき
            Toast.makeText(this, "パスワードを入力してください", Toast.LENGTH_SHORT).show()
            password.setText("")    //半角全角スペース除去
            btnLogin.setBackgroundColor(resources.getColor(R.color.btn_no_act))     //ログインボタンを「非アクティブButtonカラー」色に変更
            return false
        }
        btnLogin.setBackgroundColor(resources.getColor(R.color.btn_act))     //ログインボタンを「アクティブButtonカラー」色に変更
        Log.d("Status", "入力成功")
        return true
    }

    //入力チェック後にログインボタンの対応した背景に変更
    fun ChangeLoginBackground(): Boolean {
        if (userId.text.toString().trim().isNullOrEmpty()) {     //メールアドレスの入力がないとき
            btnLogin.setBackgroundColor(resources.getColor(R.color.btn_no_act))     //ログインボタンを「非アクティブButtonカラー」色に変更
            return false
        }
        if (password.text.toString().trim().isNullOrEmpty()) {  //パスワードの入力がないとき
            btnLogin.setBackgroundColor(resources.getColor(R.color.btn_no_act))     //ログインボタンを「非アクティブButtonカラー」色に変更
            return false
        }
        btnLogin.setBackgroundColor(resources.getColor(R.color.btn_act))     //ログインボタンを「アクティブButtonカラー」色に変更
        return true
    }
}