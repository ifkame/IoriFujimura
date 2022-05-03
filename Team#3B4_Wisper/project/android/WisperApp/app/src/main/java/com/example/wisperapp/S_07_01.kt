package com.example.wisperapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class S_07_01 : M_00_01(), MoveFollowListener {

    val client = OkHttpClient()                             //OkHttpオブジェクト生成(グローバル変数)
    var json: JSONObject? = null                            //JSONオブジェクト生成(グローバル変数)
    var followList: JSONArray? = null
    lateinit var fAdapter: FollowInfoAdapter                //フォロー詳細アダプターのオブジェクト生成
    lateinit var FollowInfoList: ArrayList<FollowInfoData>  //フォロー情報リストのオブジェクト生成
    lateinit var FollowRView: RecyclerView                  //フォロー詳細用の RecyclerViewのオブジェクト生成
    var JsonUserIdList : ArrayList<String> ?= null          //ユーザー詳細情報から取得したユーザーIDを格納する ArrayList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.follow)

        FollowInfoList = ArrayList<FollowInfoData>()  //読み込み時にタイムライン情報リストの内容をリセット
        JsonUserIdList = ArrayList<String>()        //読み込み時にユーザーIDリストの内容をリセット

        FollowRView = findViewById<RecyclerView>(R.id.follow_RecyclerView)     //ユーザー詳細用

        try {
            //タイムライン情報の表示データを取得する
            //APIで使用するURL
            val url1=
                "http://click.ecc.ac.jp/ecc/whisper_e/followerInfo.php?" +
                        "userId=" + Defines.SELECT_USER_ID
            //GET通信で使用するデータ
            val request1 = Request.Builder()
                .url(url1).get().build()

            //タイムライン情報のデータを取得
            client.newCall(request1).enqueue(object : Callback {

                //接続成功
                override fun onResponse(call: Call, response: Response) {
                    //ips 帰ってきた結果を格納
                    var ips = response.body!!.string()
                    //ipsをjson形式で　"json"に格納
                    json = JSONObject(ips)
                    var JSON_result = json!!.getString("result")      //"result"の中身をJSON_RESULTに格納

                    //データ取得成功
                    if (JSON_result == "success") {
                        //[followList]に値が入っているとき
                        if (!json!!.isNull("followList")) {
                            followList = json!!.getJSONArray("followList")
                            //タイムライン情報表示
                            //$icon→ドロイド固定,
                            for (i in 0 until followList!!.length()) {
                                var JSON_userId = followList!!.getJSONObject(i).getString("userId")         //"userId"の中身をJSON_userIdに格納
                                var JSON_userName = followList!!.getJSONObject(i).getString("userName")     //"userId"の中身をJSON_userNameに格納
                                var JSON_icon = followList!!.getJSONObject(i).getString("iconPath")         //"icon"の中身をJSON_iconに格納
                                //var JSON_postDate = followList!!.getJSONObject(i).getString("postDate")           //"postDate"の中身をJSON_postDateに格納
                                //var JSON_wCnt = followList!!.getJSONObject(i).getString("whisperCount")           //"whisperCount"の中身をJSON_wCntに格納
                                var JSON_f1Cnt = followList!!.getJSONObject(i).getInt("followCount")        //"followCount"の中身をJSON_f1Cntに格納
                                var JSON_f2Cnt = followList!!.getJSONObject(i).getInt("followerCount")      //"followerCount"の中身をJSON_f2Cntに格納

                                if (JSON_icon == ""){
                                    JSON_icon = 0.toString()
                                }

                                //followListにJSONのデータを格納
                                FollowInfoList!!.add(
                                    FollowInfoData(
                                        Defines.ICON_IMAGE_LIST[JSON_icon.toInt()],
                                        JSON_userName,
                                        JSON_f1Cnt,
                                        JSON_f2Cnt
                                    )
                                )
                                JsonUserIdList!!.add(JSON_userId)   //JSONで取得したユーザーIDを格納する
                            }

                            FollowRView.post {  //postメソッドを使用して、ユーザー詳細用RecyclerViewにアクセスする
                                // LayoutManagerの設定
                                FollowRView.layoutManager = LinearLayoutManager(this@S_07_01)
                                // fAdapterの生成と設定
                                fAdapter = FollowInfoAdapter(FollowInfoList!!, this@S_07_01)
/*
    RecyclerView のレイアウトサイズはアクティビティのレイアウトで変更されるので、recyclerView の setHasFixedSize を true にセットしておきます
    この設定はパフォーマンス向上のためだけに必要です。必須ではありません
    コンテンツの内容が変わっても RecyclerView のレイアウトサイズが変わらない場合に有効です
 */
                                FollowRView.setHasFixedSize(true)
                                FollowRView.adapter = fAdapter
                            }
                        }
                    } else {    //データ取得失敗
                        //"errMsg"の中身をerrMsgに格納
                        var JSON_errMsg = json!!.getString("errMsg")
                        this@S_07_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                            Toast.makeText(applicationContext, JSON_errMsg, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    //tx.post { tx.text = ips}
                }

                //エラー処理
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("test", e.message.toString())
                    this@S_07_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                        Toast.makeText(applicationContext, "通信失敗", Toast.LENGTH_LONG).show()
                    }
                }

            })

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    //アイコン画像クリック時の処理
    override fun IconFTapped(follow: FollowInfoData, position: Int) {
        //Definesに選択された値を保持
        Defines.SELECT_USER_ID = JsonUserIdList!![position]
        //選択したユーザーの画面に遷移する
        val intent = Intent(applicationContext, S_05_01::class.java)
        startActivity(intent)
    }
}