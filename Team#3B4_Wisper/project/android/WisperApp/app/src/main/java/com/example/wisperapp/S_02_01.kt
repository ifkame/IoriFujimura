package com.example.wisperapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException

//サーチ画面
class S_02_01 : M_00_01(), MoveFollowListener {
    var Section = 1                                             //検索切り替え変数
    val client = OkHttpClient()
    var JSONArrayList: JSONArray? = null
    var JSONArrayText: ArrayList<String> = arrayListOf("userList", "whisperList")
    lateinit var SearchRView: RecyclerView              //ユーザー詳細用の RecyclerViewのオブジェクト生成
    lateinit var s1Adapter: FollowInfoAdapter                //フォロー詳細アダプターのオブジェクト生成
    lateinit var s2Adapter: GoodCntAdapter                  //イイね数アダプターのオブジェクト生成
    lateinit var FollowInfoList: ArrayList<FollowInfoData>    //フォロー情報リストのオブジェクト生成
    lateinit var GoodCntList: ArrayList<GoodCntData>    //イイね数リストのオブジェクト生成
    var JsonUserIdList : ArrayList<String> ?= null          //ユーザー詳細情報から取得したユーザーIDを格納する ArrayList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        FollowInfoList = ArrayList<FollowInfoData>()  //読み込み時にタイムライン情報リストの内容をリセット
        GoodCntList = ArrayList<GoodCntData>()  //読み込み時にタイムライン情報リストの内容をリセット
        JsonUserIdList = ArrayList<String>()        //読み込み時にユーザーIDリストの内容をリセット

        //レイアウトからID指定したオブジェクト取得
        val radioGroup = findViewById<RadioGroup>(R.id.S_02_01)                     //検索用 RadioGroup
        val searchbutton = findViewById<Button> (R.id.S_02_03)              //検索用 Button
        val keyword = findViewById<EditText> (R.id.S_02_02)                 //検索キーワード入力用 EditText
        SearchRView = findViewById<RecyclerView> (R.id.S_02_04)             //検索結果格納用 RecyclerView

        val userRButton = findViewById<RadioButton>(R.id.userRadioButton)   //ユーザー用 RadioButton
        val submitRButton = findViewById<RadioButton>(R.id.submitTextRadioButton)   //投稿用 RadioButton
        //radioGroup.clearCheck() //選択状態を全てクリア
        radioGroup.check(R.id.userRadioButton)//最初に "ユーザー"ラジオボタンをクリック状態にしておく
        /*// 選択状態のRadioButtonのIDを取得
        val id = radioGroup.checkedRadioButtonId

        // IDをもとに、選択状態のRadioButtonを取得し、ボタンのテキストを出力
        val checkedRadioButton = findViewById<RadioButton>(id)
        println(checkedRadioButton.text)*/

        //検索ボタンクリック時
        searchbutton.setOnClickListener {
            FollowInfoList = ArrayList<FollowInfoData>()  //読み込み時にタイムライン情報リストの内容をリセット
            GoodCntList = ArrayList<GoodCntData>()  //読み込み時にタイムライン情報リストの内容をリセット
            JsonUserIdList = ArrayList<String>()        //読み込み時にユーザーIDリストの内容をリセット

            if(Section == 0) {   //ラジオボタンが押されていないとき
                Toast.makeText(this, "ラジオボタンが押されていません", Toast.LENGTH_SHORT)
                return@setOnClickListener   //クリックリスナーの処理から抜ける
            }

            try {
                val url =
                    "http://click.ecc.ac.jp/ecc/whisper_e/search.php?" +
                            "section=" + Section +
                            "&string=" + keyword.text.toString()

                val request = Request.Builder()
                    .url(url).get().build()

                client.newCall(request).enqueue(object : Callback {

                    //接続成功
                    override fun onResponse(call: Call, response: Response) {
                        //ips 帰ってきた結果を格納
                        var ips = response.body!!.string()
                        //ipsをjson形式で　"json"に格納
                        val json = JSONObject(ips)
                        //"result"の中身をJSON_resultに格納
                        var JSON_result = json.getString("result")

                        Log.d("JSONArrayText", JSONArrayText[Section-1])

                        //"errMsg"の中身をerrMsgに格納
                        //var errMsg = json.getString("errMsg")
                        if (JSON_result == "success") {
                            //選択されたラジオボタンで取ってくるリストの値が Nullでないとき
                            if (json.optString(JSONArrayText[Section-1]) != "null") {
                                //選択されたラジオボタンによって取るリストを変える
                                JSONArrayList = json.getJSONArray(JSONArrayText[Section-1])

                                //検索結果表示
                                //$icon→ドロイド固定,$goodFlg→真偽値でイイね画像変更
                                for (i in 0 until JSONArrayList!!.length()) {
                                    if (Section == 1) {         //検索ラジオボタンクリック時
                                        //取得してきたJSONデータを格納
                                        var JSON_userId = JSONArrayList!!.getJSONObject(i).getString("userId")          //"userId"の中身をJSON_userIdに格納
                                        var JSON_userName = JSONArrayList!!.getJSONObject(i).getString("userName")      //"userName"の中身をJSON_userNameに格納
                                        var JSON_icon = JSONArrayList!!.getJSONObject(i).getString("icon")              //"iconPath"の中身をJSON_iconPathに格納
                                        //var JSON_wCnt = JSONArrayList!!.getJSONObject(i).getInt("whisperCount")               //"whisperCount"の中身をJSON_whisperCountに格納
                                        var JSON_f1Cnt = JSONArrayList!!.getJSONObject(i).getInt("followCount")         //"followCount"の中身をJSON_followCountに格納
                                        var JSON_f2Cnt = JSONArrayList!!.getJSONObject(i).getInt("followerCount")       //"followerCount"の中身をJSON_followerCountに格納

                                        if (JSON_icon == ""){
                                            JSON_icon = 0.toString()
                                        }

                                        //followListにJSONのデータを格納
                                        FollowInfoList.add(
                                            FollowInfoData(
                                                Defines.ICON_IMAGE_LIST[JSON_icon.toInt()],
                                                JSON_userName,
                                                JSON_f1Cnt,
                                                JSON_f2Cnt
                                            )
                                        )
                                        JsonUserIdList!!.add(JSON_userId)   //JSONで取得したユーザーIDを格納する

                                    } else if (Section == 2) {  //投稿ラジオボタンクリック時
                                        //取得してきたJSONデータを格納
                                        //(使用禁止[null→Int出来ない])var JSON_whisperNo = JSONArrayList!!.getJSONObject(i).getInt("whisperNo")     //"whisperNo"の中身をJSON_whisperNoに格納
                                        var JSON_userId = JSONArrayList!!.getJSONObject(i)
                                            .getString("userId")                //"userId"の中身をJSON_userIdに格納
                                        var JSON_userName = JSONArrayList!!.getJSONObject(i)
                                            .getString("userName")      //"userName"の中身をJSON_userNameに格納
                                        var JSON_icon = JSONArrayList!!.getJSONObject(i)
                                            .getString("icon")          //"iconPath"の中身をJSON_iconPathに格納
                                        var JSON_postDate = JSONArrayList!!.getJSONObject(i)
                                            .getString("postDate")      //"postDate"の中身をJSON_postDateに格納
                                        var JSON_content = JSONArrayList!!.getJSONObject(i)
                                            .getString("content")        //"content"の中身をJSON_contentに格納
                                        var JSON_imagePath = JSONArrayList!!.getJSONObject(i)
                                            .getString("imagePath")    //"iconPath"の中身をJSON_imagePathに格納
                                        var JSON_goodCount = JSONArrayList!!.getJSONObject(i)
                                            .getInt("goodCount")       //"goodCount"の中身をJSON_goodCountに格納

                                        if (JSON_icon == "") {
                                            JSON_icon = 0.toString()
                                        }

                                        GoodCntList!!.add(
                                            GoodCntData(
                                                Defines.ICON_IMAGE_LIST[JSON_icon.toInt()],
                                                JSON_userName,
                                                JSON_content,
                                                JSON_goodCount
                                            )
                                        )
                                        JsonUserIdList!!.add(JSON_userId)   //JSONで取得したユーザーIDを格納する                                    }

                                    }
                                }

                                if (Section == 1) {
                                    SearchRView.post {  //postメソッドを使用して、ユーザー詳細用RecyclerViewにアクセスする
                                        runRViewFollow()    //フォローリストの表示関数
                                    }
                                } else if (Section == 2){
                                    SearchRView.post {  //postメソッドを使用して、ユーザー詳細用RecyclerViewにアクセスする
                                        runRViewGood()  //イイねリストの表示関数
                                    }
                                }

                            } else if (json.optString("errMsg") != "") {    //"errMsg"キーが生成されているとき
                                //"errMsg"の中身をerrMsgに格納//
                                var errMsg = json.getString("errMsg")
                                this@S_02_01.runOnUiThread {

                                    Toast.makeText(applicationContext, errMsg, Toast.LENGTH_LONG).show()
                                }

                            } else {
                                FollowInfoList = ArrayList<FollowInfoData>()  //読み込み時にタイムライン情報リストの内容をリセット
                                GoodCntList = ArrayList<GoodCntData>()  //読み込み時にタイムライン情報リストの内容をリセット

                                SearchRView.post {  //postメソッドを使用して、ユーザー詳細用RecyclerViewにアクセスする
                                    runRViewFollow()    //フォローリストの表示関数
                                    runRViewGood()      //イイねリストの表示関数
                                }
                            }
                        }
                    }


                    //エラー処理
                    override fun onFailure(call: Call, e: IOException) {
                        this@S_02_01.runOnUiThread {
                            Log.d("test", e.message.toString())
                            Toast.makeText(applicationContext, "通信失敗", Toast.LENGTH_LONG).show()
                        }
                    }

                })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            
        }

        // 選択項目変更のイベント追加
        //押されたラジオボタンに対応する値を格納(0: "エラー時",1: "ユーザー", 2: "投稿")
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.userRadioButton -> Section = 1
                R.id.submitTextRadioButton -> Section = 2
                else -> Section = 0
            }
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

    fun runRViewFollow() {  //フォロー用リストの作成
        // LayoutManagerの設定
        SearchRView.layoutManager = LinearLayoutManager(this@S_02_01)
        // sAdapterの生成と設定
        s1Adapter = FollowInfoAdapter(FollowInfoList, this@S_02_01)
/*
    RecyclerView のレイアウトサイズはアクティビティのレイアウトで変更されるので、recyclerView の setHasFixedSize を true にセットしておきます
    この設定はパフォーマンス向上のためだけに必要です。必須ではありません
    コンテンツの内容が変わっても RecyclerView のレイアウトサイズが変わらない場合に有効です
 */
        SearchRView.setHasFixedSize(true)
        SearchRView.adapter = s1Adapter
    }

    fun runRViewGood() {    //イイね用リスト作成
        // LayoutManagerの設定
        SearchRView.layoutManager = LinearLayoutManager(this@S_02_01)
        // sAdapterの生成と設定
        s2Adapter = GoodCntAdapter(GoodCntList)
/*
    RecyclerView のレイアウトサイズはアクティビティのレイアウトで変更されるので、recyclerView の setHasFixedSize を true にセットしておきます
    この設定はパフォーマンス向上のためだけに必要です。必須ではありません
    コンテンツの内容が変わっても RecyclerView のレイアウトサイズが変わらない場合に有効です
 */
        SearchRView.setHasFixedSize(true)
        SearchRView.adapter = s2Adapter
    }
}