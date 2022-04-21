package com.example.fuzzysearch_front.sample

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.method.ScrollingMovementMethod
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fuzzysearch_front.R
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.IOException

class TranslatePlus : AppCompatActivity(), TextToSpeech.OnInitListener {
    companion object {
        const val CAMERA_REQUEST_CODE = 1               //カメラの使用許可コード
        const val CAMERA_PERMISSION_REQUEST_CODE = 2    //カメラの権限許可のコード
        const val QR_REQUEST_CODE = 49374               //QRコードの使用許可のコード
        private const val READ_REQUEST_CODE: Int = 42   //読み込み許可のコード
    }

    internal var qrScanIntegrator: IntentIntegrator? = null     //QRコードのスキャン

    val client = OkHttpClient()     //OkHttpオブジェクト生成(グローバル変数)
    var json: JSONObject? = null   //JSONオブジェクト生成(グローバル変数)
    private var tts: TextToSpeech? = null   //音声読み上げ
    private val TAG = "TestTTS"
    var apitext: TextView? = null
    val TextList: List<String> = listOf("指定なし", "QRコード", "ギャラリー", "カメラ")
    var TextIndex = 0

    //UI部品
    lateinit var QRButtoun: Button          //QRコード読み取りボタン
    lateinit var GalleryButtoun: Button     //ギャラリー開くボタン
    lateinit var CameraButtoun: Button      //カメラ撮影ボタン
    lateinit var BeforeTTSButton: Button    //音声読み上げボタン(翻訳前)
    lateinit var AfterTTSButton: Button     //音声読み上げボタン(翻訳後)
    lateinit var BeforeTTSText: Text        //音声読み上げテキスト(翻訳前)
    lateinit var AfterTTSText: Text         //音声読み上げテキスト(翻訳後)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity_translate_plus)
        //ttsインスタンス生成
        tts = TextToSpeech(this, this)

        var pictureButton: Button = findViewById(R.id.pictureButton)
        val getrequest = findViewById<Button>(R.id.getrequest)
        val qrbutton = findViewById<Button>(R.id.qrbutton)
        apitext = findViewById<TextView>(R.id.apitext)
        var hbtext = findViewById<TextView>(R.id.home_before_text2)

        //ScrollingMovementMethod(): テキストバッファをスクロールして移動キーを解釈する移動方法。
        hbtext.movementMethod = ScrollingMovementMethod()

        //QRコードの読み取りボタン
        qrbutton.setOnClickListener {
            TextIndex = 1

            //ttsButton.setOnClickListener(this)
            qrScanIntegrator = IntentIntegrator(this)

            // 画面の回転をさせない (今回は縦画面に固定)
            qrScanIntegrator?.setOrientationLocked(true)

            // QR 読み取り後にビープ音がなるのを止める
            qrScanIntegrator?.setBeepEnabled(false)

            // スキャン開始 (QR アクティビティ生成)
            qrScanIntegrator?.initiateScan()
        }

        //JSONの取得確認ボタン
        getrequest.setOnClickListener {
            try {

                //②-1  userIdとpasswordの値を元にwebAPIを叩く
                //APIで使用するURL
                val url = "https://zipcloud.ibsnet.co.jp/api/search?zipcode=7830060"
                //"https://10.0.2.2:5000/translate"
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
                        var data1 = json!!.getString("status")
                        this@TranslatePlus.runOnUiThread {
                            apitext!!.text = data1
                            speechText(apitext!!)
                        }
                        //speechText(apitext!!)

                        //"errMsg"の中身をerrMsgに格納
                        //var errMsg = json.getString("errMsg")

                        /*
                        if (data1 == "success") {
                            //DefinesのUSERIDにuserIdTextを保持
                            Defines.USER_ID = userIdText.text.toString()
                            Defines.PASS_WARD = PasswordText.text.toString()
                            val intent = Intent(applicationContext, S_02_01::class.java)
                            startActivity(intent)
                        } else {    //②-2  APIの結果がerrだった場合、エラーメッセージを表示する
                            //"errMsg"の中身をerrMsgに格納
                            var errMsg = json!!.getString("errMsg")
                            this@S_00_01.runOnUiThread {
                                Toast.makeText(applicationContext, errMsg, Toast.LENGTH_LONG).show()
                            }

                        }
                        */
                        //tx.post { tx.text = ips}

                    }

                    //エラー処理
                    override fun onFailure(call: Call, e: IOException) {
                        this@TranslatePlus.runOnUiThread {
                            Log.d("test", e.message.toString())
                            Toast.makeText(applicationContext, "通信失敗", Toast.LENGTH_LONG).show()
                        }
                    }

                })
            } catch (e: JSONException) {
                e.printStackTrace();
            }
        }

        //ボタンが押されたらギャラリーを開く
        pictureButton.setOnClickListener {
            TextIndex = 2
            /* アプリがACTION_OPEN_DOCUMENTインテントを開始すると、
             * 条件に一致するすべてのドキュメント プロバイダが表示される */
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                /* カテゴリ（CATEGORY_OPENABLE）をインテントに追加すると、
                 * 結果がフィルタリングされ、画像ファイルなどの開くことができるドキュメントのみが表示される */
                addCategory(Intent.CATEGORY_OPENABLE)
                //typeを指定することで結果をさらにフィルタリングし、画像 MIME データタイプのドキュメントのみを表示
                type = "image/*"
            }
            startActivityForResult(intent, READ_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        val btnCamera = findViewById<Button>(R.id.button4)
        //val takePicture = findViewById<ImageView>(R.id.imageView2)

        //カメラを起動するボタン
        btnCamera.setOnClickListener {
            TextIndex = 3
            // カメラ機能を実装したアプリが存在するかチェック
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let {
                if (checkCameraPermission()) {
                    takePicture()
                } else {
                    grantCameraPermission()
                }
            } ?: Toast.makeText(this, "カメラを扱うアプリがありません", Toast.LENGTH_LONG).show()
        }
    }


    /**
     * 読み取り後に呼ばれるメソッド
     * requestCode: 要求コード → ギャラリーから戻ってきたことを識別する
     * resultCode: 結果コード → 操作が成功した場合RESULT_OKが、ユーザーがバックアウトしたり、
     *                        何らかの理由で失敗したりした場合はRESULT_CANCELEDが返る
     * resultData: 取得したデータ → 選択したドキュメントを指すURIにはresultData.dataでアクセスできる
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var textaaa = findViewById<TextView>(R.id.textView)
        val fixedSize = 400

        //text1 = findViewById(R.id.home_before_text2)

        val cameraImage = findViewById<ImageView>(R.id.imageView2)

        if (TextIndex == 1) {
            Log.d("${TextList[TextIndex]}のrequestCode", "$requestCode")
            Log.d("${TextList[TextIndex]}のresultCode", "$resultCode")
            Log.d("${TextList[TextIndex]}のdata", "$data")
            // QRコードの結果の取得
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                //result.contents で取得した値を参照できる
                apitext!!.text = result.contents.toString()

                Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
                speechText(apitext!!)   //音声読み上げ
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }

        //写真が選択された後の動き
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("${TextList[TextIndex]}のrequestCode", "$requestCode")
        Log.d("${TextList[TextIndex]}のresultCode", "$resultCode")
        Log.d("${TextList[TextIndex]}のdata", "$data")

        //ピッカーでドキュメントを選択して、操作が成功（RESULT_OK）していないとき
        if (resultCode != RESULT_OK) {
            return  //処理を抜ける
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    data?.data?.also { uri ->
                        var inputStream = contentResolver?.openInputStream(uri)

                        /////////
                        val options = BitmapFactory.Options()//情報取得
                        //ByteArrayOutputStream baos = new ByteArrayOutputStream()
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeStream(inputStream, null, options)

                        val scaleX: Float = (options.outWidth / fixedSize).toFloat()// 画像の横サイズ/400
                        val scaleY: Float = (options.outHeight / fixedSize).toFloat()// 画像の縦サイズ/400
                        options.inSampleSize =
                            Math.floor(java.lang.Float.valueOf(Math.max(scaleX, scaleY)).toDouble())
                                .toInt()

                        options.inJustDecodeBounds = false;
                        inputStream = contentResolver?.openInputStream(uri)
                        //////
                        var imagefile = BitmapFactory.decodeStream(inputStream, null, options)
                        //val image = Bitmap.createScaledBitmap(imagefile, fixedSize, fixedSize, true)//400*400
                        cameraImage.setImageBitmap(imagefile)

                        //Bitmapをbyte配列に変換する
                        val stream = ByteArrayOutputStream()
                        imagefile!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val bytes = stream.toByteArray()

                        //byte配列をBase64(String)に変換する
                        val strBase64: String = Base64.encodeToString(bytes, Base64.NO_WRAP)

                        //Log.d("ベース６４結果","${strBase64}")
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
                //Log.e("読み取りデータ", "$data")
                //Log.d("", "")
            }
        }


        //カメラ関係
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.extras?.get("data")?.let {
                val wkBitmap = it as Bitmap

                //scaleXの高さをfixedSize固定
                val scaleX = (wkBitmap.width * fixedSize) / wkBitmap.height.toDouble()

                val image = Bitmap.createScaledBitmap(
                    wkBitmap,
                    scaleX.toInt(),
                    fixedSize,
                    true
                ) //width * height(400)
                cameraImage.setImageBitmap(image)
            }
            //Log.e("読み取りデータ", "$data")
            //Log.d("", "")
        }
    }

    //カメラ関係
    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }


    override fun onInit(status: Int) {
        // TTS初期化
        if (TextToSpeech.SUCCESS == status) {
            Log.d(TAG, "initialized")
        } else {
            Log.e(TAG, "failed to initialize")
        }
    }

    //カメラのアクセス許可を確認
    private fun checkCameraPermission() = PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)

    //許可カメラアクセス許可
    private fun grantCameraPermission() =
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )

    //要求のアクセス許可の結果
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                takePicture()
            }
        }
    }


    private fun shutDown() {
        if (null != tts) {
            // to release the resource of TextToSpeech
            tts!!.shutdown()
        }
    }

    private fun speechText(apitext: TextView) {
        //var apitext = findViewById<TextView>(R.id.apitext)
        Log.d("title", "2")
        val string = apitext?.text.toString()
        //val string = text1?.text.toString()

        if (0 < string.length) {
            if (tts!!.isSpeaking) {
                tts!!.stop()
                return
            }
            setSpeechRate()
            setSpeechPitch()
            if (Build.VERSION.SDK_INT >= 21) {
                // SDK 21 以上
                tts!!.speak(string, TextToSpeech.QUEUE_FLUSH, null, "messageID")
                Log.d("title", "1")
            }
            setTtsListener()
        }
    }

    // 読み上げのスピード
    private fun setSpeechRate() {
        if (null != tts) {
            tts!!.setSpeechRate(1.0.toFloat())
        }
    }

    // 読み上げのピッチ
    private fun setSpeechPitch() {
        if (null != tts) {
            tts!!.setPitch(1.0.toFloat())
        }
    }

    // 読み上げの始まりと終わりを取得
    private fun setTtsListener() {
        if (Build.VERSION.SDK_INT >= 21) {
            val listenerResult =
                tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onDone(utteranceId: String) {
                        Log.d(TAG, "progress on Done $utteranceId")
                    }

                    override fun onError(utteranceId: String) {
                        Log.d(TAG, "progress on Error $utteranceId")
                    }

                    override fun onStart(utteranceId: String) {
                        Log.d(TAG, "progress on Start $utteranceId")
                    }
                })
            if (listenerResult != TextToSpeech.SUCCESS) {
                Log.e(TAG, "failed to add utterance progress listener")
            }
        } else {
            Log.e(TAG, "Build VERSION is less than API 15")
        }
    }

}
