package com.example.fuzzysearch_front

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.fuzzysearch_front.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var focusView: TextView        //フォーカスを外すためのテキスト
    private lateinit var beforeTransText: EditText      //翻訳前の入力テキスト(HomeFragment)
    private lateinit var beforeVoiceButton: ImageButton //翻訳前のテキスト読み上げボタン
    private lateinit var afterVoiceButton: ImageButton  //翻訳後のテキスト読み上げボタン

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //バインディング オブジェクトを作成
        //バインディング クラスの inflate() メソッドを使用することで、ビュー階層をインフレートしてオブジェクトをバインド
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //バインドしたメイン画面のツールバーを使用・連携
        setSupportActionBar(binding.appBarMain.toolbar)

        //メイン画面のフローティング操作ボタンをクリックしたとき
        binding.appBarMain.fab.setOnClickListener { view ->
            /*
            スナックバーは、操作に関する軽量のフィードバックを提供する
            モバイルの場合は画面の下部に、大型のデバイスの場合は左下に短いメッセージが表示されます。
            スナックバーは画面上の他のすべての要素の上に表示され、一度に表示できるのは1つだけです。
            */
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout                       //バインドしたサイドバーの画面全体レイアウト
        val navView: NavigationView = binding.navView                               //バインドしたサイドバーの画面ナビゲーションView
        val navController = findNavController(R.id.nav_host_fragment_content_main)  //ナビゲーションバーの場所としてフラグメントを取得
        focusView = binding.appBarMain.fragmentFocusView                //
        beforeTransText = findViewById(R.id.home_before_text)           //翻訳前テキスト
        beforeVoiceButton = findViewById(R.id.home_before_voice)        //翻訳前読み上げボタン
        afterVoiceButton = findViewById(R.id.home_after_voice)          //翻訳後読み上げボタン

        // ナビゲーションバーで遷移する画面の設定
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_history, R.id.nav_qrcode
            ), drawerLayout
        )
        //ナビゲーションバーとナビゲーションコントローラーを連携
        setupActionBarWithNavController(navController, appBarConfiguration)
        //サイドバーのナビゲーションViewとナビゲーションコントローラーを連携
        navView.setupWithNavController(navController)

        //キーボードが表示・非表示が変更されたとき
        setKeyboardVisibilityListener(object : OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                Log.i(
                    "Keyboard state",
                    if (visible) "Keyboard is active" else "Keyboard is Inactive"
                )
                if (!visible) { //キーボードを閉じたとき
                    // 翻訳前テキストの入力があるとき
                    if (!beforeTransText.text.toString().trim().isNullOrEmpty()) {
                        beforeVoiceButton.setBackgroundResource(com.example.fuzzysearch_front.R.color.transparent)     //翻訳前読み上げボタンを「透明」色に変更
                    }

                    //InputMethodManager をキャストしながら取得
                    //入力メソッドにアクセスするメソッド
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(focusView.windowToken, 0)   //キーボードを閉じるメソッド
                }
            }
        })
    }

    // メニューをActivity上に設置する
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // メインメニューの画面として生成
        //menuInflater.inflate(R.menu.main, menu)
        return true
    }

    //メニューのアイテムを押下した時の処理の関数
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            //設定ボタンを押したとき
            R.id.action_settings -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //そのままではUpボタンが反応しないので、onSupportNavigateUp()をオーバーライドすることでUpボタンが機能するようになる
    override fun onSupportNavigateUp(): Boolean {
        //ナビゲーションバーの場所としてフラグメントを取得
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        //アプリの開始デスティネーションがスタックの最上部に配置されるので、
        //[上へ] や [戻る] をタップすると NavController.navigateUp() メソッドが呼び出され、
        //スタックの最上部にあるデスティネーションが削除（ポップ）される
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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
        val parentView: TextView = focusView //.getChildAt(0);
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

    //入力チェックを行うメソッド
    fun InputCheck(): Boolean {
        if (beforeTransText.text.toString().trim().isNullOrEmpty()) {  //翻訳前テキストの入力がないとき
            Toast.makeText(this, "翻訳前テキストを入力してください", Toast.LENGTH_SHORT).show()
            beforeTransText.setText("")    //半角全角スペース除去
            beforeVoiceButton.setBackgroundResource(com.example.fuzzysearch_front.R.color.no_active)     //翻訳前読み上げボタンを「非アクティブ背景」色に変更
            return false
        }
        beforeVoiceButton.setBackgroundResource(com.example.fuzzysearch_front.R.color.transparent)     //翻訳前読み上げボタンを「透明」色に変更
        Log.d("Status", "入力成功")
        return true
    }
}