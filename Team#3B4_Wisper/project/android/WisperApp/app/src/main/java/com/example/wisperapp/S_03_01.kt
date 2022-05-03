package com.example.wisperapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

//import com.squareup.okhttp.Request

class S_03_01 : M_00_01() , Defines {
    // private var content: EditText? = null
    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.whisper)

        //変数宣言
        val content = findViewById<EditText>(R.id.wh_edit)             //入力用 Edittext
        val cancelButton: Button = findViewById(R.id.wh_cancel_button)  //戻る用 Button
        val postButton: Button = findViewById(R.id.wh_post_button)      //投稿用 Button
        var tx = findViewById<TextView>(R.id.textView6)

        // キーボードを表示するようにする
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        //キャンセルボタンクリック時
        cancelButton.setOnClickListener {
            finish()     //今開いている画面を閉じて、前の画面に戻る
        }

        postButton.setOnClickListener {

            try {
                val url =
                    "http://click.ecc.ac.jp/ecc/whisper_e/whisperAdd.php?" + "userId=" + Defines.USER_ID + "&content=" + content.text.toString()+ "&imagePath="

                val request = Request.Builder()
                    .url(url).get().build()

                client.newCall(request).enqueue(object : Callback {

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
                            finish()
                        } else {
                            //"errMsg"の中身をerrMsgに格納//
                            var errMsg = json.getString("errMsg")
                            this@S_03_01.runOnUiThread {

                                Toast.makeText(applicationContext, errMsg, Toast.LENGTH_LONG).show()
                            }
                        }
                        tx.post { tx.text = ips}
                    }

                    //エラー処理
                    override fun onFailure(call: Call, e: IOException) {
                        this@S_03_01.runOnUiThread {
                            Log.d("test", e.message.toString())
                            Toast.makeText(applicationContext, "通信失敗", Toast.LENGTH_LONG).show()
                        }
                    }

                })
                } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}