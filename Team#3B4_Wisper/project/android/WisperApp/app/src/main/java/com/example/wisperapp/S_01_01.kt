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
import java.util.*
import kotlin.collections.ArrayList

//HOMEタイムライン画面
class S_01_01 : M_00_01(), ChangeGoodListener, MoveUserListener {
    val client = OkHttpClient()                             //OkHttpオブジェクト生成(グローバル変数)
    var json : JSONObject? = null                           //JSONオブジェクト生成(グローバル変数)
    var whisperList : JSONArray? = null                     //ユーザー詳細用の RecyclerViewで使う JSONArray
    lateinit var mAdapter: UserDetailAdapter                //ユーザー詳細アダプターのオブジェクト生成
    var TimeLineList: ArrayList<UserDetailData>? = null     //タイムライン情報リストのオブジェクト生成
    lateinit var UserDetailRView: RecyclerView              //ユーザー詳細用の RecyclerViewのオブジェクト生成
    var JsonUserIdList : ArrayList<String> ?= null          //タイムライン情報から取得したユーザーIDを格納する ArrayList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hometime)

        JsonUserIdList = ArrayList<String>()        //読み込み時にユーザーIDリストの内容をリセット
        TimeLineList = ArrayList<UserDetailData>()  //読み込み時にタイムライン情報リストの内容をリセット

        //レイアウトからID指定したオブジェクト取得
        val IconImage = findViewById<ImageView>(R.id.P_01_01)          //ユーザ名入力用
        val UserName = findViewById<TextView>(R.id.P_01_02)            //Email入力用
        val PostContext = findViewById<TextView>(R.id.P_01_03)         //password入力用
        UserDetailRView = findViewById<RecyclerView>(R.id.P_01_04)     //ユーザー詳細用

        try{
            //ログインユーザ情報の表示データを取得する
            //APIで使用するURL
            val url1 =
                "http://click.ecc.ac.jp/ecc/whisper_e/userInfo.php?" +
                        "userId=" + Defines.USER_ID                       //ログイン画面で取得した userId で実行
            //GET通信で使用するデータ
            val request1 = Request.Builder()
                .url(url1).get().build()

            //ログインユーザ情報のデータを取得
            client.newCall(request1).enqueue(object : Callback {

                //接続成功
                override fun onResponse(call: Call, response: Response) {
                    //ips 帰ってきた結果を格納
                    var ips = response.body!!.string()
                    //ipsをjson形式で　"json"に格納
                    json = JSONObject(ips)

                    val JSON_result = json!!.getString("result")    //"result"の中身をJSON_resultに格納
                    //val JSON_userId = json!!.getString("userId")    //"userId"の中身をJSON_userIdに格納
                    val JSON_userName = json!!.getString("userName") //"userName"の中身をJSON_userNameに格納
                    val JSON_profile = json!!.getString("profile")  //"profile"の中身をJSON_profileに格納
                    val JSON_icon = json!!.optString("icon")        //"icon"の中身をJSON_iconに格納

                    //データ取得成功
                    if (JSON_result == "success") {

                        this@S_01_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                            //ユーザー情報表示
                            if (JSON_icon == ""){   //画像変更していないとき
                                IconImage.setImageResource(R.mipmap.ic_launcher)    //ドロイド君のアイコン画像
                                Defines.MY_ICON_IMAGE = 0
                            } else {                            //プロフィール編集でアイコン画像変更しているとき
                                IconImage.setImageResource(Defines.ICON_IMAGE_LIST[JSON_icon.toInt()])  //選択したアイコン画像
                                Defines.MY_ICON_IMAGE = JSON_icon.toInt()
                            }
                            UserName.text = JSON_userName.toString()
                            PostContext.text = JSON_profile.toString()
                        }
                    } else {    //データ取得失敗
                        //"errMsg"の中身をerrMsgに格納
                        var JSON_errMsg = json!!.getString("errMsg")
                        this@S_01_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                            Toast.makeText(applicationContext, JSON_errMsg, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    //tx.post { tx.text = ips}
                }

                //エラー処理
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("test", e.message.toString())
                    this@S_01_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                        Toast.makeText(applicationContext, "通信失敗", Toast.LENGTH_LONG).show()
                    }
                }

            })

            //タイムライン情報の表示データを取得する
            //APIで使用するURL
            val url2 =
                "http://click.ecc.ac.jp/ecc/whisper_e/timelineInfo.php?" +
                        "userId=" + Defines.USER_ID /* +
                        "&From=0&To=10" */
            //GET通信で使用するデータ
            val request2 = Request.Builder()
                .url(url2).get().build()

            //タイムライン情報のデータを取得
            client.newCall(request2).enqueue(object : Callback {

                //接続成功
                override fun onResponse(call: Call, response: Response) {
                    //ips 帰ってきた結果を格納
                    var ips = response.body!!.string()
                    //ipsをjson形式で　"json"に格納
                    json = JSONObject(ips)
                    var JSON_result = json!!.getString("result")                                          //"result"の中身をJSON_RESULTに格納

                    //データ取得成功
                    if (JSON_result == "success") {
                        //[whisperList]に値が入って
                        if (!json!!.isNull("whisperList")) {
                            whisperList = json!!.getJSONArray("whisperList")
                            //タイムライン情報表示
                            //$icon→ドロイド固定,$goodFlg→真偽値でイイね画像変更
                            for (i in  0 .. whisperList!!.length()-1) {
                                //var JSON_whisperNo = whisperList!!.getJSONObject(i).getInt("whisperNo")   //"whisperNo"の中身をJSON_whisperNoに格納
                                var JSON_userId = whisperList!!.getJSONObject(i).getString("userId")      //"userId"の中身をJSON_userIdに格納
                                var JSON_userName = whisperList!!.getJSONObject(i).getString("userName")  //"userId"の中身をJSON_userNameに格納
                                var JSON_icon = whisperList!!.getJSONObject(i).optString("iconPath")          //"icon"の中身をJSON_iconに格納
                                //var JSON_postDate = whisperList!!.getJSONObject(i).getString("postDate")  //"postDate"の中身をJSON_postDateに格納
                                var JSON_content = whisperList!!.getJSONObject(i).getString("content")    //"content"の中身をJSON_contentに格納
                                //var JSON_image = whisperList!!.getJSONObject(i).getString("image")        //"image"の中身をJSON_imageに格納
                                var JSON_goodFlg = whisperList!!.getJSONObject(i).getBoolean("goodFlg")   //"goodFlg"の中身をJSON_goodFlgに格納

                                if (JSON_icon == ""){
                                    JSON_icon = 0.toString()
                                }

                                //イイねFlagでイイねボタンを変える
                                if (JSON_goodFlg) {
                                    TimeLineList!!.add(UserDetailData(Defines.ICON_IMAGE_LIST[JSON_icon.toInt()],JSON_userName, JSON_content, android.R.drawable.btn_star_big_on))
                                }else{
                                    TimeLineList!!.add(UserDetailData(Defines.ICON_IMAGE_LIST[JSON_icon.toInt()],JSON_userName, JSON_content, android.R.drawable.btn_star_big_off))
                                }
                                JsonUserIdList!!.add(JSON_userId)   //JSONで取得したユーザーIDを格納する
                            }

                            UserDetailRView.post {  //postメソッドを使用して、ユーザー詳細用RecyclerViewにアクセスする
                                // LayoutManagerの設定
                                UserDetailRView.layoutManager = LinearLayoutManager(this@S_01_01)
                                // CustomAdapterの生成と設定
                                mAdapter =
                                    UserDetailAdapter(TimeLineList!!, this@S_01_01, this@S_01_01)
/*
    RecyclerView のレイアウトサイズはアクティビティのレイアウトで変更されるので、recyclerView の setHasFixedSize を true にセットしておきます
    この設定はパフォーマンス向上のためだけに必要です。必須ではありません
    コンテンツの内容が変わっても RecyclerView のレイアウトサイズが変わらない場合に有効です
 */
                                UserDetailRView.setHasFixedSize(true)
                                UserDetailRView.adapter = mAdapter
                            }
                        }
                    } else {    //データ取得失敗
                        //"errMsg"の中身をerrMsgに格納
                        var JSON_errMsg = json!!.getString("errMsg")
                        this@S_01_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                            Toast.makeText(applicationContext, JSON_errMsg, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    //tx.post { tx.text = ips}
                }

                //エラー処理
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("test", e.message.toString())
                    this@S_01_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                        Toast.makeText(applicationContext, "通信失敗", Toast.LENGTH_LONG).show()
                    }
                }

            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    //アイコン画像クリック時の処理
    override fun IconTapped(userdetail: UserDetailData, position: Int) {
        //Definesに選択された値を保持
        Defines.SELECT_USER_ID = JsonUserIdList!![position]
        Defines.SELECT_COMENT = userdetail.postContent
        Defines.SELECT_IMAGE = userdetail.detail_iconId
        Defines.SELECT_GOOD = userdetail.goodId
        //選択したユーザーの画面に遷移する
        val intent = Intent(applicationContext, S_05_01::class.java)
        startActivity(intent)
    }

    //イイね画像クリック時の処理
    override fun GoodTapped(userdetail: UserDetailData, position: Int) {
        //一応実行時にエラーが出た時の別解
        var SelectGoodFlg : Boolean
        var SelectwhisperNo = whisperList!!.getJSONObject(position).getString("whisperNo")
        //val AfterUserDetailRView = findViewById<RecyclerView>(R.id.P_01_04)     //ユーザー詳細用

        if (userdetail.goodId === android.R.drawable.btn_star_big_off) {    //イイね画像がOFFのとき
            TimeLineList!![position] = UserDetailData(userdetail.detail_iconId, userdetail.userName, userdetail.postContent, android.R.drawable.btn_star_big_on)
            SelectGoodFlg = true    //選択したリストのイイね情報を追加する(true)
        } else {    //イイね画像がONのとき(それ以外)
            TimeLineList!![position] = UserDetailData(userdetail.detail_iconId, userdetail.userName, userdetail.postContent, android.R.drawable.btn_star_big_off)
            SelectGoodFlg = false   //選択したリストのイイね情報を削除する(false)
        }

        try {
            //タイムライン情報の表示データを取得する
            //APIで使用するURL
            val url3 =
                "http://click.ecc.ac.jp/ecc/whisper_e/goodCtl.php?" +
                        "userId=" + Defines.USER_ID  +
                        "&whisperNo=" + SelectwhisperNo +
                        "&goodFlg=" + SelectGoodFlg
            //GET通信で使用するデータ
            val request3 = Request.Builder()
                .url(url3).get().build()

            //タイムライン情報のデータを取得
            client.newCall(request3).enqueue(object : Callback {

                //接続成功
                override fun onResponse(call: Call, response: Response) {
                    //ips 帰ってきた結果を格納
                    var ips = response.body!!.string()
                    //ipsをjson形式で　"json"に格納
                    json = JSONObject(ips)
                    var JSON_result = json!!.getString("result")                                          //"result"の中身をJSON_RESULTに格納

                    //データ取得成功
                    if (JSON_result == "success") {
                        this@S_01_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                            Toast.makeText(applicationContext, SelectGoodFlg.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {    //データ取得失敗
                        //"errMsg"の中身をerrMsgに格納
                        var JSON_errMsg = json!!.getString("errMsg")
                        this@S_01_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                            Toast.makeText(applicationContext, JSON_errMsg, Toast.LENGTH_LONG).show()
                        }
                    }
                    //tx.post { tx.text = ips}
                }

                //エラー処理
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("test", e.message.toString())
                    this@S_01_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                        Toast.makeText(applicationContext, "通信失敗", Toast.LENGTH_LONG).show()
                    }
                }

            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        // リストの再読み込み
        mAdapter.notifyDataSetChanged()
    }
}