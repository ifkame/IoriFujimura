package com.example.wisperapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import okhttp3.*

//ユーザ登録画面
class S_00_02 : AppCompatActivity() {
    val client = OkHttpClient() //OkHttpオブジェクト生成(グローバル変数)
    var json : JSONObject? = null   //JSONオブジェクト生成(グローバル変数)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.usersignup)

        //レイアウトからID指定したオブジェクト取得
        val yourName = findViewById<EditText>(R.id.P_00_05)     //ユーザ名入力用
        val Email = findViewById<EditText>(R.id.P_00_06)        //Email入力用
        val Password = findViewById<EditText>(R.id.P_00_07)     //password入力用
        val rePassword = findViewById<EditText>(R.id.P_00_08)   //password再入力用
        val createButton = findViewById<Button>(R.id.P_00_09)   //CREATEボタン
 
        //CREATEボタンクリック時
        createButton.setOnClickListener {
            //入力されたメールアドレス、ユーザ名、パスワードをAPIを使用し登録チェックを行う
            try {
                //①passwordとre:passwordの論理チェック
                // 一致しなかった場合エラーメッセージを表示
                if (Password.text.toString() != rePassword.text.toString()) {
                    this@S_00_02.runOnUiThread {
                        Toast.makeText(applicationContext, "パスワードが一致しません", Toast.LENGTH_LONG).show()
                    }
                } else {
                    //②userIdとusernameとpasswordを元にユーザ登録する為、webAPIを叩く
                    //APIで使用するURL
                    val url = "http://click.ecc.ac.jp/ecc/whisper_e/userAdd.php?" +
                            "userName=" + yourName.text.toString() +
                            "&userId=" + Email.text.toString() +
                            "&password=" + Password.text.toString()
                    //GET通信で使用するデータ
                    val request = Request.Builder()
                        .url(url).get().build()

                    //PHP側にデータを送信してWebAPI実行
                    client.newCall(request).enqueue(object : Callback {
                        //接続成功
                        override fun onResponse(call: Call, response: Response) {
                            //ips 帰ってきた結果を格納
                            var ips = response.body!!.string()
                            //ipsをjson形式で　"json"に格納
                            json = JSONObject(ips)
                            //"result"の中身をdata1に格納
                            var data1 = json!!.getString("result")

                            //webAPIを叩いて帰ってきた結果がsuccessだった場合HOMEタイムラインへ遷移する
                            if (data1 == "success") {
                                val intent = Intent(applicationContext, S_00_01::class.java)
                                startActivity(intent)
                            //webAPIを叩いて帰ってきた結果がerrだった場合、エラーメッセージを表示する
                            } else {
                                //"errMsg"の中身をerrMsgに格納
                                var errMsg = json!!.getString("errMsg")
                                this@S_00_02.runOnUiThread {
                                    Toast.makeText(applicationContext, errMsg, Toast.LENGTH_LONG)
                                        .show()
                                }

                            }

                        }


                        //エラー処理
                        override fun onFailure(call: Call, e: IOException) {
                            this@S_00_02.runOnUiThread {
                                Log.d("test", e.message.toString())
                                Toast.makeText(applicationContext, "通信失敗", Toast.LENGTH_LONG).show()
                            }
                        }

                    })
                }

            } catch (e: JSONException) {
                e.printStackTrace();
            }

        }

    }
}
