package com.example.fuzzysearch_front.sample

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fuzzysearch_front.R
import com.google.zxing.integration.android.IntentIntegrator
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream

class CameraPlusTTS: AppCompatActivity(), TextToSpeech.OnInitListener {
    companion object {
        const val CAMERA_REQUEST_CODE = 1               //カメラの使用許可コード
        const val CAMERA_PERMISSION_REQUEST_CODE = 2    //カメラの権限許可のコード
        const val QR_REQUEST_CODE = 49374               //QRコードの使用許可のコード
        private const val READ_REQUEST_CODE: Int = 42   //読み込み許可のコード
    }
    //インスタンス
    lateinit var qrScanIntegrator: IntentIntegrator //QRコードのスキャン
    lateinit var tts: TextToSpeech                  //音声読み上げ

    //UI部品
    lateinit var QRButtoun: Button          //QRコード読み取りボタン
    lateinit var GalleryButtoun: Button     //ギャラリー開くボタン
    lateinit var CameraButtoun: Button      //カメラ撮影ボタン
    lateinit var BeforeTTSButton: Button    //音声読み上げボタン(翻訳前)
    lateinit var AfterTTSButton: Button     //音声読み上げボタン(翻訳後)
    lateinit var BeforeTTSText: Text        //音声読み上げテキスト(翻訳前)
    lateinit var AfterTTSText: Text         //音声読み上げテキスト(翻訳後)

    private val TAG = "TestTTS"
    var apitext: TextView? = null
    val TextList: List<String> = listOf("指定なし", "QRコード", "ギャラリー", "カメラ")
    var TextIndex = 0

    /**
     * 初期設定
     */
    fun setQRScanIntegrator(qrscan: IntentIntegrator){  //QRコードのインスタンス情報受け渡し
        if (qrscan != null) {
            this.qrScanIntegrator = qrscan
        }
    }

    fun setTTS(context: Context, listener: TextToSpeech.OnInitListener){    //音声読み上げのインスタンス情報受け渡し
        if (context != null && listener != null){
            this.tts = TextToSpeech(context, listener)
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


    // 読み取り後に呼ばれるメソッド
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

        if (resultCode != AppCompatActivity.RESULT_OK) {
            return
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
        if (requestCode == TranslatePlus.CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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

        startActivityForResult(intent, TranslatePlus.CAMERA_REQUEST_CODE)
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
            TranslatePlus.CAMERA_PERMISSION_REQUEST_CODE
        )

    //要求のアクセス許可の結果
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == TranslatePlus.CAMERA_PERMISSION_REQUEST_CODE) {
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