package com.example.wisperapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class S_05_01 : M_00_01(), ChangeGoodListener, MoveUserListener {
    var Section = 1                                                                 //検索切り替え変数
    val client = OkHttpClient()                                                     //OkHttpオブジェクト生成(グローバル変数)
    var JSONArrayList: JSONArray? = null                                            //ラジオボタンで選択されたリスト JSONArrayList
    var JSONArrayText: ArrayList<String> = arrayListOf("whisperList", "goodList")   //ラジオボタンで選択されるリストのテキスト
    var json : JSONObject? = null                                                   //JSONオブジェクト生成(グローバル変数)
    lateinit var mAdapter: UserDetailAdapter                                        //ユーザー詳細アダプターのオブジェクト生成
    var UserDetailList: ArrayList<UserDetailData>? = null                           //ユーザー詳細情報リストのオブジェクト生成
    lateinit var UserDetailRView: RecyclerView                                      //ユーザー詳細用の RecyclerViewのオブジェクト生成
    var JsonUserIdList : ArrayList<String> ?= null                                  //ユーザー詳細情報から取得したユーザーIDを格納する ArrayList
    var JSON_userFollowFlg: Boolean = false                                         //フォロー状態格納フラグ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userdetail)

        val MyFollowCnt = findViewById<TextView>(R.id.UD_followCnt)                 //自分のフォロー数 Text
        val MyFollowerCnt = findViewById<TextView>(R.id.UD_followerCnt)             //自分のフォロワー数 Text

        //レイアウトからID指定したオブジェクト取得
        val FollowButton = findViewById<Button>(R.id.UD_followButton)               //フォロー切り替え用 Button
        val radioGroup = findViewById<RadioGroup>(R.id.UD_radioGroup)               //ささやき詳細切り替え用 RadioGroup
        UserDetailRView = findViewById<RecyclerView>(R.id.UD_RecyclerView)          //ユーザー詳細用 RecyclerView

        radioGroup.check(R.id.UD_WhisperRadioButton)//最初に "ウィスパー一覧"ラジオボタンをクリック状態にしておく

        if (Defines.SELECT_USER_ID == Defines.USER_ID){     //自分のユーザーが選択されたとき
            FollowButton.visibility = View.INVISIBLE        //フォロー切り替え用 Button の非表示
        }

        UserDetailProcess()     //OkHttpの処理を行う関数

        // 選択項目変更のイベント追加
        //押されたラジオボタンに対応する値を格納(0: "エラー時",1: "ユーザー", 2: "投稿")
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.UD_WhisperRadioButton -> Section = 1
                R.id.UD_GoodRadioButton -> Section = 2
                else -> Section = 0
            }
            Log.d("ラジオボタン","到達した！")
            UserDetailProcess()     //OkHttpの処理を行う関数
        }

        //フォロー数の数字クリック時
        MyFollowCnt.setOnClickListener{
            //フォロー一覧画面に遷移する
            val intent = Intent(applicationContext, S_07_01::class.java)
            startActivity(intent)
        }

        //フォロワー数の数字クリック時
        MyFollowerCnt.setOnClickListener{
            //フォロー一覧画面に遷移する
            val intent = Intent(applicationContext, S_08_01::class.java)
            startActivity(intent)
        }

        //フォローボタンクリック時
        FollowButton.setOnClickListener {
            JSON_userFollowFlg = !JSON_userFollowFlg
            try {
                //タイムライン情報の表示データを取得する
                //APIで使用するURL
                val url2 =
                    "http://click.ecc.ac.jp/ecc/whisper_e/followCtl.php?" +
                            "userId=" + Defines.USER_ID  +
                            "&followUserId=" + Defines.SELECT_USER_ID +
                            "&followFlg=" + JSON_userFollowFlg
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
                            this@S_05_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                                Toast.makeText(applicationContext, JSON_userFollowFlg.toString(), Toast.LENGTH_SHORT).show()
                                if (!JSON_userFollowFlg) {   //未フォローのとき
                                    FollowButton.text = "フォローする"    //フォロー → 未フォロー
                                    JSON_userFollowFlg = false
                                } else {
                                    FollowButton.text = "フォロー中"     //未フォロー → フォロー
                                    JSON_userFollowFlg = true
                                }
                            }
                        } else {    //データ取得失敗
                            //"errMsg"の中身をerrMsgに格納
                            var JSON_errMsg = json!!.getString("errMsg")
                            this@S_05_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                                Toast.makeText(applicationContext, JSON_errMsg, Toast.LENGTH_LONG).show()
                            }
                        }
                        //tx.post { tx.text = ips}
                    }

                    //エラー処理
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("test", e.message.toString())
                        this@S_05_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
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

    //端末の戻るボタンクリック時の処理
    override fun onBackPressed() {
        if (Defines.BEFORE_USER_ID.lastIndex > 0) {     //以前選択したユーザーIDが存在するとき
            Defines.SELECT_USER_ID = Defines.BEFORE_USER_ID[Defines.BEFORE_USER_ID.lastIndex]   //選択したユーザーIDに以前選択したユーザーIDを格納
            Defines.BEFORE_USER_ID.removeAt(Defines.BEFORE_USER_ID.lastIndex)                   //最後に追加した以前選択したユーザーIDを削除
        }
        finish()    //前の画面に戻る
    }

    //アイコン画像クリック時の処理
    override fun IconTapped(userdetail: UserDetailData, position: Int) {
        //Definesに選択された値を保持
        Defines.BEFORE_USER_ID.add(Defines.SELECT_USER_ID)      //画面遷移時に以前選択したユーザーIDを追加する
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
        var SelectwhisperNo = JSONArrayList!!.getJSONObject(position).getString("whisperNo")
        //val AfterUserDetailRView = findViewById<RecyclerView>(R.id.P_01_04)     //ユーザー詳細用

        if (userdetail.goodId === android.R.drawable.btn_star_big_off) {    //イイね画像がOFFのとき
            UserDetailList!![position] = UserDetailData(userdetail.detail_iconId, userdetail.userName, userdetail.postContent, android.R.drawable.btn_star_big_on)
            SelectGoodFlg = true    //選択したリストのイイね情報を追加する(true)
        } else {    //イイね画像がONのとき(それ以外)
            UserDetailList!![position] = UserDetailData(userdetail.detail_iconId, userdetail.userName, userdetail.postContent, android.R.drawable.btn_star_big_off)
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
                        this@S_05_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                            Toast.makeText(applicationContext, SelectGoodFlg.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {    //データ取得失敗
                        //"errMsg"の中身をerrMsgに格納
                        var JSON_errMsg = json!!.getString("errMsg")
                        this@S_05_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                            Toast.makeText(applicationContext, JSON_errMsg, Toast.LENGTH_LONG).show()
                        }
                    }
                    //tx.post { tx.text = ips}
                }

                //エラー処理
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("test", e.message.toString())
                    this@S_05_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
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

    //ユーザー詳細画面で行う処理
    fun UserDetailProcess(){
        JsonUserIdList = ArrayList<String>()        //読み込み時にユーザーIDリストの内容をリセット
        UserDetailList = ArrayList<UserDetailData>()  //読み込み時にタイムライン情報リストの内容をリセット

        //レイアウトからID指定したオブジェクト取得
        val MyIcon = findViewById<ImageView>(R.id.UD_iconId)                        //自分のアイコン画像用 ImageView
        val MyFollowCnt = findViewById<TextView>(R.id.UD_followCnt)                 //自分のフォロー数 Text
        val MyFollowerCnt = findViewById<TextView>(R.id.UD_followerCnt)             //自分のフォロワー数 Text
        val MyName = findViewById<TextView>(R.id.UD_NameText)                       //自分のユーザー名用 Text
        val MyContext = findViewById<TextView>(R.id.UD_Content)                     //自分のプロフィール用 Text
        val FollowButton = findViewById<Button>(R.id.UD_followButton)               //フォロー切り替え用 Button

        try{
            //ログインユーザ情報の表示データを取得する
            //APIで使用するURL
            val url1 =
                "http://click.ecc.ac.jp/ecc/whisper_e/userWhisperInfo.php?" +
                        "userId=" + Defines.SELECT_USER_ID +
                        "&loginUserId=" + Defines.USER_ID                       //ログイン画面で取得した userId で実行
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

                    val JSON_result = json!!.getString("result")                    //"result"の中身をJSON_resultに格納
                    val JSON_userId = json!!.getString("userId")                    //"userId"の中身をJSON_userIdに格納
                    val JSON_userName = json!!.getString("userName")                //"userName"の中身をJSON_userNameに格納
                    val JSON_profile = json!!.getString("profile")                  //"profile"の中身をJSON_profileに格納
                    var JSON_icon = json!!.optString("iconPath")                    //"iconPath"の中身をJSON_iconに格納
                    JSON_userFollowFlg = json!!.getBoolean("userFollowFlg")         //"userFollowFlg"の中身をJSON_userFollowFlgに格納
                    var JSON_f1Cnt = json!!.optInt("followCount")                   //"followCount"の中身をJSON_followCountに格納
                    var JSON_f2Cnt = json!!.optInt("followerCount")                 //"followerCount"の中身をJSON_followerCountに格納

                    if (JSON_icon == ""){
                        JSON_icon = 0.toString()
                    }

                    //データ取得成功
                    if (JSON_result == "success") {
                        this@S_05_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                            //ユーザー情報表示
                            MyIcon.setImageResource(Defines.ICON_IMAGE_LIST[JSON_icon.toInt()]) //選択したアイコン画像に変更
                            //[IconImage.setImageResource(JSON_icon.toInt()) 画像変更するなら]
                            MyFollowCnt.text = JSON_f1Cnt.toString()
                            MyFollowerCnt.text = JSON_f2Cnt.toString()
                            MyName.text = JSON_userName.toString()
                            MyContext.text = JSON_profile.toString()
                            if (JSON_userFollowFlg){
                                FollowButton.text = "フォロー中"
                            } else {
                                FollowButton.text = "フォローする"
                            }
                        }
                        //選択されたラジオボタンで取ってくるリストの値が Nullでないとき
                        if (json!!.optString(JSONArrayText[Section-1]) != "null") {
                            //選択されたラジオボタンによって取るリストを変える
                            JSONArrayList = json!!.getJSONArray(JSONArrayText[Section-1])

                            //タイムライン情報表示
                            //$icon→ドロイド固定,$goodFlg→真偽値でイイね画像変更
                            for (i in  0 .. JSONArrayList!!.length()-1) {
                                //var JSON_whisperNo = JSONArrayList!!.getJSONObject(i).getInt("whisperNo")   //"whisperNo"の中身をJSON_whisperNoに格納
                                var JSON_userId = JSONArrayList!!.getJSONObject(i).getString("userId")      //"userId"の中身をJSON_userIdに格納
                                var JSON_userName = JSONArrayList!!.getJSONObject(i).getString("userName")  //"userId"の中身をJSON_userNameに格納
                                var JSON_icon = JSONArrayList!!.getJSONObject(i).getString("iconPath")          //"icon"の中身をJSON_iconに格納
                                //var JSON_postDate = JSONArrayList!!.getJSONObject(i).getString("postDate")  //"postDate"の中身をJSON_postDateに格納
                                var JSON_content = JSONArrayList!!.getJSONObject(i).getString("content")    //"content"の中身をJSON_contentに格納
                                //var JSON_image = JSONArrayList!!.getJSONObject(i).getString("image")        //"image"の中身をJSON_imageに格納
                                var JSON_goodFlg = JSONArrayList!!.getJSONObject(i).getBoolean("goodFlg")   //"goodFlg"の中身をJSON_goodFlgに格納

                                if (JSON_icon == ""){
                                    JSON_icon = 0.toString()
                                }

                                //イイねFlagでイイねボタンを変える
                                if (JSON_goodFlg) {
                                    UserDetailList!!.add(UserDetailData(Defines.ICON_IMAGE_LIST[JSON_icon.toInt()], JSON_userName, JSON_content, android.R.drawable.btn_star_big_on))
                                }else{
                                    UserDetailList!!.add(UserDetailData(Defines.ICON_IMAGE_LIST[JSON_icon.toInt()], JSON_userName, JSON_content, android.R.drawable.btn_star_big_off))
                                }
                                JsonUserIdList!!.add(JSON_userId)   //JSONで取得したユーザーIDを格納する
                            }

                            UserDetailRView.post {  //postメソッドを使用して、ユーザー詳細用RecyclerViewにアクセスする
                                // LayoutManagerの設定
                                UserDetailRView.layoutManager = LinearLayoutManager(this@S_05_01)
                                // CustomAdapterの生成と設定
                                mAdapter =
                                    UserDetailAdapter(UserDetailList!!, this@S_05_01, this@S_05_01)
/*
RecyclerView のレイアウトサイズはアクティビティのレイアウトで変更されるので、recyclerView の setHasFixedSize を true にセットしておきます
この設定はパフォーマンス向上のためだけに必要です。必須ではありません
コンテンツの内容が変わっても RecyclerView のレイアウトサイズが変わらない場合に有効です
*/
                                UserDetailRView.setHasFixedSize(true)
                                UserDetailRView.adapter = mAdapter
                            }
                        } else if (json!!.optString("errMsg") != "") {    //"errMsg"キーが生成されているとき
                            //"errMsg"の中身をerrMsgに格納//
                            var errMsg = json!!.getString("errMsg")
                            this@S_05_01.runOnUiThread {
                                Toast.makeText(applicationContext, errMsg, Toast.LENGTH_LONG).show()
                            }

                        } else {
                            JsonUserIdList = ArrayList<String>()        //読み込み時にユーザーIDリストの内容をリセット
                            UserDetailList = ArrayList<UserDetailData>()  //読み込み時にタイムライン情報リストの内容をリセット

                            UserDetailRView.post {  //postメソッドを使用して、ユーザー詳細用RecyclerViewにアクセスする
                                // LayoutManagerの設定
                                UserDetailRView.layoutManager = LinearLayoutManager(this@S_05_01)
                                // CustomAdapterの生成と設定
                                mAdapter =
                                    UserDetailAdapter(UserDetailList!!, this@S_05_01, this@S_05_01)
/*
RecyclerView のレイアウトサイズはアクティビティのレイアウトで変更されるので、recyclerView の setHasFixedSize を true にセットしておきます
この設定はパフォーマンス向上のためだけに必要です。必須ではありません
コンテンツの内容が変わっても RecyclerView のレイアウトサイズが変わらない場合に有効です
*/
                                UserDetailRView.setHasFixedSize(true)
                                UserDetailRView.adapter = mAdapter
                            }
                        }
                    }
                }

                //エラー処理
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("test", e.message.toString())
                    this@S_05_01.runOnUiThread {    //コールバック内はワーカースレッドから呼び出されているので、直接レイアウトを触ってはいけない
                        Toast.makeText(applicationContext, "通信失敗", Toast.LENGTH_LONG).show()
                    }
                }

            })


        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}