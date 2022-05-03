package com.example.wisperapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

//ログイン画面
class S_00_01 :  AppCompatActivity() {
    val client = OkHttpClient()     //OkHttpオブジェクト生成(グローバル変数)
    var json : JSONObject? = null   //JSONオブジェクト生成(グローバル変数)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        //レイアウトからID指定したオブジェクト取得
        val userIdText = findViewById<EditText>(R.id.P_00_01)   //E-mail入力用
        val PasswordText = findViewById<EditText>(R.id.P_00_02) //password入力用
        val SIGNbutton = findViewById<Button> (R.id.P_00_03)    //ログインボタン
        val CREATEbutton = findViewById<Button> (R.id.P_00_04)  //CREATEボタン

        //確認用テキスト
        val tx = findViewById<TextView>(R.id.tx)

        //ログインボタンクリック時
        SIGNbutton.setOnClickListener {
            try{
                //入力されたメールアドレスとパスワードを元にAPIでログインチェックを行う
                //①userIdとpasswordの入力チェック
                if (userIdText.text.toString() == "" && PasswordText.text.toString() == ""){
                    Toast.makeText(applicationContext, "メールアドレスとパスワードを入力して下さい", Toast.LENGTH_SHORT)
                }

                //②-1  userIdとpasswordの値を元にwebAPIを叩く
                //APIで使用するURL
                val url =
                    "http://click.ecc.ac.jp/ecc/whisper_e/loginAuth.php?" +
                            "userId=" + userIdText.text.toString() +
                            "&password=" + PasswordText.text.toString()
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
                        //"errMsg"の中身をerrMsgに格納
                        //var errMsg = json.getString("errMsg")

                        if (data1 == "success") {
                            //DefinesのUSERIDにuserIdTextを保持
                            Defines.USER_ID = userIdText.text.toString()
                            Defines.PASS_WARD = PasswordText.text.toString()
                            val intent = Intent(applicationContext, S_01_01::class.java)
                            startActivity(intent)
                        } else {    //②-2  APIの結果がerrだった場合、エラーメッセージを表示する
                            //"errMsg"の中身をerrMsgに格納
                            var errMsg = json!!.getString("errMsg")
                            this@S_00_01.runOnUiThread {
                                Toast.makeText(applicationContext, errMsg, Toast.LENGTH_LONG).show()
                            }

                        }
                        //tx.post { tx.text = ips}

                    }

                    //エラー処理
                    override fun onFailure(call: Call, e: IOException) {
                        this@S_00_01.runOnUiThread {
                            Log.d("test", e.message.toString())
                            Toast.makeText(applicationContext, "通信失敗", Toast.LENGTH_LONG).show()
                        }
                    }

                })
            } catch (e: JSONException) {
                e.printStackTrace();
            }

        }

        //帰ってきた結果Error(userIdとpasswordの入力チェック)
            //val toast = Toast.makeText(this, "これは Toast を表示するテストです。", Toast.LENGTH_LONG)
            //toast.show()

        //ユーザ登録画面へ移動する
        //CREATEボタンクリック時
        CREATEbutton.setOnClickListener {
            val intent = Intent(this, S_00_02::class.java)
            startActivity(intent)
        }

    }



}