package com.example.wisperapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class S_06_01 : M_00_01() {
    val client = OkHttpClient()                             //OkHttpオブジェクト生成(グローバル変数)
    var json : JSONObject? = null                           //JSONオブジェクト生成(グローバル変数)
    private var iconSelectSpinner: Spinner? = null          //自分のアイコン画像変更用 Spinner
    private var iconSelectPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.useredit)

        //レイアウトからID指定したオブジェクト取得
        val ImageChangeButton: Button = findViewById(R.id.C_Imagebutton)    //画像変更用 Button
        val CancelButton: Button = findViewById(R.id.UE_cancelButton)       //キャンセル用 Button
        val SaveButton: Button = findViewById(R.id.UE_saveButton)           //保存用 Button
        val IconImage = findViewById<ImageView>(R.id.UE_iconId)             //アイコン画像用 ImageView
        val NameEdit = findViewById<EditText>(R.id.UE_nameEditText)         //名前入力用 Edit
        val PasswordONOFF = findViewById<EditText>(R.id.UE_password)        //パスワードONOFF用 Edit
        val ProfileEdit = findViewById<EditText>(R.id.UE_proFileEditText)   //プロフィール入力用 Edit

        ImageChangeButton.visibility = View.GONE    //画像変更用 Buttonの非表示

        //パスワードの Layoutを非表示
        val PasswordText = findViewById<TextView>(R.id.UE_passTextView)     //パスワード表示用 Text
        val PasswordEdit = findViewById<EditText>(R.id.UE_passEditText)     //パスワード入力用 Edit
        PasswordText.visibility = View.GONE        //パスワード表示 Text の非表示
        PasswordEdit.visibility = View.GONE        //パスワード入力 Edit の非表示

        try {
            //自分のユーザ情報の表示データを取得する
            //APIで使用するURL
            val url1 =
                "http://click.ecc.ac.jp/ecc/whisper_e/userInfo.php?" +
                        "userId=" + Defines.USER_ID                       //ログイン画面で取得した userId で実行
            //GET通信で使用するデータ
            val request1 = Request.Builder()
                .url(url1).get().build()

            //自分のユーザ情報のデータを取得
            client.newCall(request1).enqueue(object : Callback {

                //接続成功
                override fun onResponse(call: Call, response: Response) {
                    //ips 帰ってきた結果を格納
                    var ips = response.body!!.string()
                    //ipsをjson形式で　"json"に格納
                    json = JSONObject(ips)

                    val JSON_result = json!!.getString("result")        //"result"の中身をJSON_resultに格納
                    //val JSON_userId = json!!.getString("userId")              //"userId"の中身をJSON_userIdに格納
                    val JSON_userName = json!!.getString("userName")    //"userName"の中身をJSON_userNameに格納
                    val JSON_password = json!!.getString("password")    //"password"の中身をJson_passwordに格納
                    val JSON_profile = json!!.getString("profile")      //"profile"の中身をJSON_profileに格納
                    var JSON_icon = json!!.getString("icon")            //"icon"の中身をJSON_iconに格納

                    if (JSON_icon == ""){
                        JSON_icon = 0.toString()
                    }

                    //データ取得成功
                    if (JSON_result == "success") {
                        this@S_06_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                            //ユーザー情報表示
                            IconImage.setImageResource(Defines.ICON_IMAGE_LIST[JSON_icon.toInt()])   //選択したアイコンに画像を変更
                            Defines.MY_ICON_IMAGE = JSON_icon.toInt()
                            //[IconImage.setImageResource(JSON_icon.toInt()) 画像変更するなら]
                            NameEdit.setText(JSON_userName.toString())      //名前入力の値をデータベースから取得
                            ProfileEdit.setText(JSON_profile.toString())    //プロフィール入力の値をデータベースから取得
                            // ↓ [追加: パスワードが変更可能]
                            PasswordONOFF.setText(JSON_password)            //パスワード入力の値をデータベースから取得
                        }
                    } else {    //データ取得失敗
                        //"errMsg"の中身をerrMsgに格納
                        var JSON_errMsg = json!!.getString("errMsg")
                        this@S_06_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                            Toast.makeText(applicationContext, JSON_errMsg, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    //tx.post { tx.text = ips}
                }

                //エラー処理
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("test", e.message.toString())
                    this@S_06_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                        Toast.makeText(applicationContext, "通信失敗", Toast.LENGTH_LONG).show()
                    }
                }

            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        //画像変更用アダプターを作成
        //android.R.Layout.simple_spinner_itemをR.layout.spinner_itemに変更
        val myIconSpinnerAdapter: ArrayAdapter<*> =
            ArrayAdapter.createFromResource(this, R.array.myIconSpinnerItems, R.layout.spinner_item)

        //スピナーをクリックしたときのドロップダウンリストを設定
        //android.R.Layout.simple_spinner_dropdown_itemをR.layout.spinner_dropdown_itemに変更
        myIconSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        iconSelectSpinner = findViewById<View>(R.id.UE_icon_select_spinner) as Spinner
        iconSelectSpinner!!.adapter = myIconSpinnerAdapter
        iconSelectSpinner!!.setSelection(Defines.MY_ICON_IMAGE!!.toInt())

        //画像変更用スピナーのアイテムが選択されたとき
        iconSelectSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                // ここにスピナー内のアイテムを選択した際の処理を書く
                val iconSelectSpinnerText = parent?.selectedItem as String
                IconImage.setImageResource(Defines.ICON_IMAGE_LIST[position])   //選択したアイコンに画像を変更
                iconSelectPosition = position                                   //自分のアイコン画像の位置番号を格納
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // スピナーでは使用しないようですが、ないといけないのでこのまま放置
            }
        }

        /*ImageChangeButton.setOnClickListener {
            //画像変換
        }*/

        CancelButton.setOnClickListener {
            finish()//前画面へ遷移する
        }

        SaveButton.setOnClickListener {
            try {
                //ユーザ変更の処理命令を行う
                //APIで使用するURL
                val url2 =
                    "http://click.ecc.ac.jp/ecc/whisper_e/userUpd.php?" + "userId=" + Defines.USER_ID +
                            "&userName=" + NameEdit.text.toString() +
                            "&password=" + PasswordONOFF.text.toString() +
                            "&profile=" + ProfileEdit.text.toString() +
                            "&icon=" + iconSelectPosition   //← 画像を変更する処理を増やすときに変更してください

                //https://click.ecc.ac.jp/ecc/whisper_e/userUpd.php?userId=1&userName=1&password=1&profile=1&icon=1

                //GET通信で使用するデータ
                val request2 = Request.Builder()
                    .url(url2).get().build()

                //ユーザ変更の処理命令を送る
                client.newCall(request2).enqueue(object : Callback {

                    //接続成功
                    override fun onResponse(call: Call, response: Response) {
                        //ips 帰ってきた結果を格納
                        var ips = response.body!!.string()
                        //ipsをjson形式で　"json"に格納
                        val json = JSONObject(ips)
                        //"result"の中身をdata1に格納
                        var data1 = json.getString("result")
                        //"errMsg"の中身をerrMsgに格納
                        //var errMsg = json.getString("errMsg")
                        if (data1 == "success") {
                            //タイムライン画面に遷移する
                            val intent = Intent(applicationContext, S_01_01::class.java)
                            startActivity(intent)
                        } else {
                            //"errMsg"の中身をerrMsgに格納//
                            var errMsg = json.getString("errMsg")
                            this@S_06_01.runOnUiThread {

                                Toast.makeText(applicationContext, errMsg, Toast.LENGTH_LONG).show()
                            }

                        }

                    }


                    //エラー処理
                    override fun onFailure(call: Call, e: IOException) {
                        this@S_06_01.runOnUiThread {
                            Log.d("test", e.message.toString())
                            Toast.makeText(applicationContext, "通信失敗", Toast.LENGTH_LONG).show()
                        }
                    }

                })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            //ログインユーザの情報を変更
        }
    }
}