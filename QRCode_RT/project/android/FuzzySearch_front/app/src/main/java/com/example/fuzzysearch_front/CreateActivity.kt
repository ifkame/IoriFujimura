package com.example.fuzzysearch_front

import android.R
import android.content.Context
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.fuzzysearch_front.databinding.ActivityCreateBinding
import com.example.fuzzysearch_front.room.*
import com.example.http_post.OkHttp3Callback
import org.json.JSONArray
import org.json.JSONObject

class CreateActivity : AppCompatActivity(), OkHttp3Callback.ApiCreateUserCallback,
    OkHttp3Callback.ApiLanguageCallback {
    var apiRequest = OkHttp3Callback()  //API処理クラス呼び出し
    var roomRequest = RoomFunction()    //Room処理クラス呼び出し

    lateinit var mUserDao: UserDao      //ユーザーDao(アプリ内のユーザーデータベースへの抽象アクセスを可能にするメソッド)
    lateinit var mUserAdapter: ArrayAdapter<String>     //使用するユーザー情報を格納するデータリスト
    private var mUserList: List<User> = listOf()    //ユーザー情報リスト
    lateinit var mLangDao: LangDao      //言語Dao(アプリ内のユーザーデータベースへの抽象アクセスを可能にするメソッド)
    lateinit var mLangAdapter: ArrayAdapter<String>     //使用する言語情報を格納するデータリスト
    private var mLangList: List<Lang> = listOf()    //言語情報リスト

    private lateinit var binding: ActivityCreateBinding

    // 選択できる言語リスト
    private lateinit var spinnerItems: ArrayList<String>
    private var select_lang: Int = 1
    private lateinit var spinnerAdapter: ArrayAdapter<String>   //言語スピナーで使用するデータリスト

    //UI部品
    private lateinit var userId: EditText       //ユーザーID
    private lateinit var password: EditText     //パスワード
    private lateinit var btnCreate: Button      //新規登録ボタン
    private lateinit var lang_spinner: Spinner  //言語スピナー
    private lateinit var list_view: ListView    //確認用リストビュー
    private lateinit var focusView: TextView    //フォーカスを外すためのテキスト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = resources.getString(com.example.fuzzysearch_front.R.string.title_create)    //タイトルを「新規登録」に変更

        //レイアウトからID指定したオブジェクト取得
        userId = binding.createUserEdit
        password = binding.createPassword
        btnCreate = binding.createAccountButton
        lang_spinner = binding.createLangSpinner
        list_view = binding.createListview
        focusView = binding.createFocusView

        //使わないUI部品
        list_view.visibility = View.INVISIBLE
        binding.createSelectText.visibility = View.INVISIBLE

        apiRequest.setApiLanguageCallback(this)     //言語取得コールバック呼び出し
        apiRequest.setApiCreateUserCallback(this)   //新規登録コールバック呼び出し

        //アプリ内のユーザーデータベースのインスタンス生成
        mUserDao = UserDatabase.getInstance(this).userDao()
        //ユーザー配列の設定
        mUserAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListOf())
        //アプリ内の言語データベースのインスタンス生成
        mLangDao = LangDatabase.getInstance(this).langDao()
        //言語配列の設定
        mLangAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListOf())

        list_view.adapter = mUserAdapter    //ユーザーデータベースの行を全件表示

        roomRequest.setUserRoom(mUserDao, mUserAdapter)     //ユーザーデータベース使用のための情報を RoomFunctionに渡す
        roomRequest.setLangRoom(mLangDao, mLangAdapter)     //言語データベース使用のための情報を RoomFunctionに渡す

        mUserList = roomRequest.getUserList()       //ユーザー情報を配列に格納
        mLangList = roomRequest.getLangList()       //言語情報を配列に格納

        spinnerItems = ArrayList<String>()      //言語スピナーアイテムをリセット

        /**
         * 言語を取得するAPI
         */
        if (mLangList.isNotEmpty()) {  //言語データベース配列リストが空でないとき
            for (i in mLangList.indices) {
                spinnerItems.add(mLangList[i].select_lang)  //言語データベース内のデータを格納
            }
        }

        // Adapterの作成（Adapterのパラメータ設定）
        spinnerAdapter = ArrayAdapter<String>(
            applicationContext,
            //R.layout.simple_spinner_item, spinnerItems
            com.example.fuzzysearch_front.R.layout.custom_spinner,
            spinnerItems
        )

        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        // spinner に adapter をセット
        // View Binding
        lang_spinner.adapter = spinnerAdapter

        apiRequest.getAPI_language()    //選択できる言語取得APIを実行

        // 言語スピナーが押したとき
        binding.createLangSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                //　アイテムが選択された時
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?, position: Int, id: Long
                ) {
                    val spinnerParent =
                        parent as Spinner               //この言語スピナーオブジェクトをスピナーとして作成
                    val item = spinnerParent.selectedItem as String     //作成したスピナーから選択したアイテムを取得
                    // View Binding
                    //binding.createSelectText.text = item        //選択したアイテムを表示
                    select_lang = position + 1                    //選択した位置を選択した言語として格納
                }

                //　アイテムが選択されなかった
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //特に処理なし
                }
            }

        //新規登録ボタンを押したとき
        binding.createAccountButton.setOnClickListener {
            if (InputCheck()) {     //空白スペースor空の入力チェック(自作)で入力がされているとき
                /**
                 * 新規登録を行うAPI
                 * email: 作成したいユーザ名
                 * lang_id: 作成するユーザーの登録言語
                 * password: 作成したいユーザのパスワード
                 */
                apiRequest.postCreateUser(userId.text.toString(), select_lang, password.text.toString())}
        }

        //ユーザーEditのフォーカスが変更したとき
        userId.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {    //フォーカスが外れたとき
                //InputMethodManager をキャストしながら取得
                //入力メソッドにアクセスするメソッド
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)   //キーボードを閉じるメソッド
                ChangeCreateBackground()    //対応した新規登録ボタンの背景に変更
            }
        }

        //パスワードEditのフォーカスが変更したとき
        password.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {    //フォーカスが外れたとき
                //InputMethodManager をキャストしながら取得
                //入力メソッドにアクセスするメソッド
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)   //キーボードを閉じるメソッド
                ChangeCreateBackground()    //対応した新規登録ボタンの背景に変更
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
                    ChangeCreateBackground()    //対応した新規登録ボタンの背景に変更
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
        val parentView: TextView = findViewById(com.example.fuzzysearch_front.R.id.create_focusView) //.getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
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

    //言語の取得成功時
    override fun language_success(obj: JSONArray?) {
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            roomRequest.deleteLang()
            //言語データベース配列リストが空でないとき
            if (mLangList.isNotEmpty()) spinnerItems = ArrayList<String>()  //言語スピナーアイテムをリセット
            for (i in 0 until obj!!.length()) {
                var lang_id = obj.getJSONObject(i).getInt("lang_id")
                var lang_name = obj.getJSONObject(i).getString("lang_name")
                Log.d("", "言語番号[%d]: %s".format(lang_id, lang_name))
                spinnerItems.add(lang_name)             //言語スピナーに追加する取得したデータリストを格納
                roomRequest.insertLang(Lang(lang_id, lang_name))    //選択言語のRoomデータ追加
                binding.createLangSpinner.post {    //言語スピナーに post()して UIThreadを操作する
                    spinnerAdapter.notifyDataSetChanged()   // リストの再読み込み
                }
            }
            Log.e("Room格納した値", "${roomRequest.getLang()}")
        }
    }

    //言語の取得失敗時
    override fun language_failed() {
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            for (i in mLangList.indices) {
                spinnerItems.add(mLangList[i].select_lang)
                Log.d("a", i.toString())
            }
        }
    }

    //新規登録の結果取得時
    override fun createuser_success(obj: JSONObject) {
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            Toast.makeText(applicationContext, obj.toString(), Toast.LENGTH_SHORT).show()

            //成功時に登録したユーザー情報リストを生成
            var UserSuccessData =
                User(
                    mUserList.lastIndex,
                    userId.text.toString(),
                    password.text.toString(),
                    select_lang,
                    obj.getString("token")
                )
            Log.d("UserSuccessData", UserSuccessData.toString())

            roomRequest.insertUser(UserSuccessData)     //ユーザーデータベースの行追加
            Toast.makeText(applicationContext, "${UserSuccessData}", Toast.LENGTH_SHORT).show()
            finish()    //このActivityを終了する
        }
    }

    //新規登録の取得失敗時
    override fun createuser_failed(obj: JSONObject) {
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            Toast.makeText(applicationContext, obj.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    //入力チェックを行うメソッド
    fun InputCheck(): Boolean {
        if (userId.text.toString().trim().isNullOrEmpty()) {    //メールアドレスの入力がないとき
            Toast.makeText(this, "メールアドレスを入力してください", Toast.LENGTH_SHORT).show()
            userId.setText("")      //半角全角スペース除去
            btnCreate.setBackgroundColor(resources.getColor(com.example.fuzzysearch_front.R.color.btn_no_act))     //新規登録ボタンを「非アクティブButtonカラー」色に変更
            return false
        }
        if (password.text.toString().trim().isNullOrEmpty()) {  //パスワードの入力がないとき
            Toast.makeText(this, "パスワードを入力してください", Toast.LENGTH_SHORT).show()
            password.setText("")    //半角全角スペース除去
            btnCreate.setBackgroundColor(resources.getColor(com.example.fuzzysearch_front.R.color.btn_no_act))     //新規登録ボタンを「非アクティブButtonカラー」色に変更
            return false
        }
        btnCreate.setBackgroundColor(resources.getColor(com.example.fuzzysearch_front.R.color.btn_act))     //新規登録ボタンを「アクティブButtonカラー」色に変更
        Log.d("Status", "入力成功")
        return true
    }

    //入力チェック後に新規登録ボタンの対応した背景に変更
    fun ChangeCreateBackground(): Boolean {
        if (userId.text.toString().trim().isNullOrEmpty()) {     //メールアドレスの入力がないとき
            btnCreate.setBackgroundColor(resources.getColor(com.example.fuzzysearch_front.R.color.btn_no_act))     //ログインボタンを「非アクティブButtonカラー」色に変更
            return false
        }
        if (password.text.toString().trim().isNullOrEmpty()) {  //パスワードの入力がないとき
            btnCreate.setBackgroundColor(resources.getColor(com.example.fuzzysearch_front.R.color.btn_no_act))     //ログインボタンを「非アクティブButtonカラー」色に変更
            return false
        }
        btnCreate.setBackgroundColor(resources.getColor(com.example.fuzzysearch_front.R.color.btn_act))     //ログインボタンを「アクティブButtonカラー」色に変更
        return true
    }
}