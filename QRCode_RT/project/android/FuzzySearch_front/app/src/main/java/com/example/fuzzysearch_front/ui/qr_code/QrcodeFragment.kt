package com.example.fuzzysearch_front.ui.qr_code

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fuzzysearch_front.databinding.FragmentQrcodeBinding
import com.example.http_post.OkHttp3Callback
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File

class QrcodeFragment : Fragment(), OkHttp3Callback.ApiCreateQRCallback {

    private lateinit var qrcodeViewModel: QrcodeViewModel
    private var _binding: FragmentQrcodeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var apiRequest = OkHttp3Callback()              //API処理クラス呼び出し

    private var shareQRcode: Uri? = null            //共有するQRコード

    private lateinit var QRimageView: ImageView     //QRコードの画像
    private lateinit var QRCreateButton: Button     //QRコード作成ボタン
    private lateinit var QRShareButton: Button      //QRコード共有ボタン
    private lateinit var QRcodeText: TextView       //QRコード作成テキスト
    private lateinit var QRsetText: TextView        //送るQRコードテキスト

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        qrcodeViewModel =
            ViewModelProvider(this).get(QrcodeViewModel::class.java)

        _binding = FragmentQrcodeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //BindingしたUI部品を連携する
        QRimageView = binding.qrImage               //QRコードの画像
        QRCreateButton = binding.qrCreateButton     //QRコード作成ボタン
        QRShareButton = binding.qrShareButton       //QRコード共有ボタン
        QRcodeText = binding.qrCreateEditText       //QRコード作成テキスト
        QRsetText = binding.qrSendShareText         //送るQRコードテキスト

        QRimageView.visibility = View.GONE     //QRコード画像作成まで非表示

        apiRequest.setApiCreateQRCallback(this)                 //QRコード作成コールバック呼び出し

        //QRコード作成ボタンを押したとき
        QRCreateButton.setOnClickListener {
            apiRequest.getCreateQR(QRcodeText.text.toString())      //QRコード作成APIを実行
        }

        //QRコード共有ボタンを押したとき
        QRShareButton.setOnClickListener {
            if (shareQRcode != null) {  //QRコードの作成時に取得するUriがnullでないとき
                ShareFunction()     //共有のときに行う処理
            } else {
                Toast.makeText(requireContext(), "QRコードを作成してから共有ボタンを押してください！", Toast.LENGTH_SHORT).show()
                //QRsetText.hint = "QRコードを作成してから共有ボタンを押してください！(エラーメッセージ)"     //共有するQRコードの状態として格納
            }
        }

        //QRコード作成テキストのフォーカスが変更したとき
        QRcodeText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {    //フォーカスが外れたとき
                //InputMethodManager をキャストしながら取得
                //入力メソッドにアクセスするメソッド
//                val imm =
//                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(v.windowToken, 0)   //キーボードを閉じるメソッド

                //QRコード作成テキストがないとき
                if (QRcodeText.text.toString().trim().isNullOrEmpty()) {
                    QRShareButton.setBackgroundColor(com.example.fuzzysearch_front.R.color.btn_no_act)     //ログインボタンを「非アクティブButtonカラー」色に変更
                    return@setOnFocusChangeListener
                }
                QRShareButton.setBackgroundColor(com.example.fuzzysearch_front.R.color.btn_act)     //ログインボタンを「アクティブButtonカラー」色に変更
            }
        }


        qrcodeViewModel.text.observe(viewLifecycleOwner, Observer {
            //QRcodeText.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //シェアで行う処理をまとめたもの
    fun ShareFunction() {
//        val imageUri = resources.openRawResource(resId).use { input ->
//            val file = File("$cacheDir/create_qrcode.png")  //画像名は指定できる
//            file.createNewFile()
//            file.outputStream().use { output ->
//                input.copyTo(output)
//            }
//            FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
//        }

        var imageUri = shareQRcode  //QRコード作成APIの結果から取得したUri
        ShareCompat.IntentBuilder(requireContext()).apply {
            setChooserTitle("share title")      //この共有のアクティビティチューザーに使用されるタイトルを設定
            setText("これはQRコード[中身(${QRsetText.text})]です。")   //共有の一部として送信されるリテラルテキストデータ(本文のデフォルト)を設定

            /** setStream(streamUri: Uri?)
             *  共有する必要のあるデータにストリームURIを設定します。
             *  これにより、現在設定されているすべてのストリームURIが置き換えられ、単一ストリームのACTION_SENDインテントが生成されます。*/
            setStream(imageUri)
            setType("image/png")                //ファイルの種類を指定
        }.startChooser()    //共有タブ表示
    }

    // Base64 to Bitmap
    @Throws(IllegalArgumentException::class)
    fun convert(base64Str: String): Bitmap {
        var decodedBytes = Base64.decode(
            base64Str.substring(base64Str.indexOf(",") + 1),
            Base64.DEFAULT
        )

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    // Bitmap to URI
    private fun getImageUri(context: Context, bitmapImage: Bitmap): Uri {
        var bytes = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        var sdcard = Environment.getExternalStorageDirectory()
        if (sdcard != null) {
            var mediaDir = File(sdcard, "DCIM/Camera")
            if (!mediaDir.exists()) {
                mediaDir.mkdirs()
            }
        }
        var path = MediaStore.Images.Media.insertImage(
            context.getContentResolver(),
            bitmapImage,
            "IMG_" + System.currentTimeMillis(),
            null
        )
        return Uri.parse(path)
    }

    //QRコード作成の結果取得時
    override fun createQR_success(obj: JSONObject) {
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            //Toast.makeText(requireContext(), obj.toString(), Toast.LENGTH_SHORT).show()
            Log.d("JSON結果", obj.toString())
            QRimageView.visibility = View.VISIBLE     //QRコード画像作成時に表示
            var Base64toBitmap: Bitmap = convert(obj.getString("qr_data"))  //取得した Base64 → Bitmap
            QRimageView.setImageBitmap(Base64toBitmap)                      //BitmapをImageViewに画像として格納
            shareQRcode = getImageUri(requireContext(), Base64toBitmap)     //Bitmap → Uri して共有Uriに格納
            QRsetText.text = QRcodeText.text.toString()                     //送るQRコードの内容を表示

        }
    }

    //QRコード作成の取得失敗時
    override fun createQR_failed(obj: String?) {
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            Toast.makeText(requireContext(), "QRコードにする文字を入力してください", Toast.LENGTH_LONG).show()
        }
    }
}