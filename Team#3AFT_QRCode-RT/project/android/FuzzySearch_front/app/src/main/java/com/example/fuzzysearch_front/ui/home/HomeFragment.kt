package com.example.fuzzysearch_front.ui.home

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.method.ScrollingMovementMethod
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fuzzysearch_front.Defines
import com.example.fuzzysearch_front.databinding.FragmentHomeBinding
import com.example.fuzzysearch_front.room.Lang
import com.example.fuzzysearch_front.room.LangDao
import com.example.fuzzysearch_front.room.LangDatabase
import com.example.fuzzysearch_front.room.RoomFunction
import com.example.http_post.OkHttp3Callback
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class HomeFragment : Fragment(), OkHttp3Callback.ApiTranslateCallback,
    OkHttp3Callback.ApiSelectTranslateCallback,
    OkHttp3Callback.ApiLanguageCallback, TextToSpeech.OnInitListener {
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        const val CAMERA_REQUEST_CODE = 1               //カメラの使用許可コード
        const val CAMERA_PERMISSION_REQUEST_CODE = 2    //カメラの権限許可のコード
        const val QR_REQUEST_CODE = 49374               //QRコードの使用許可のコード
        private const val READ_REQUEST_CODE: Int = 42   //読み込み許可のコード
    }

    /* API & Room 関連 */
    var apiRequest = OkHttp3Callback()  //API処理クラス呼び出し
    var roomRequest = RoomFunction()    //Room処理クラス呼び出し

    lateinit var mLangDao: LangDao      //言語Dao(アプリ内のユーザーデータベースへの抽象アクセスを可能にするメソッド)
    lateinit var mLangAdapter: ArrayAdapter<String>     //使用する言語情報を格納するデータリスト
    private var mLangList: List<Lang> = listOf()    //言語情報リスト

    // 選択できる言語リスト
    private var spinnerItems: ArrayList<String> = ArrayList<String>()
    private var select_lang: Int = 1    //言語スピナーの選択した位置
    private lateinit var spinnerAdapter: ArrayAdapter<String>   //言語スピナーで使用するデータリスト

    private var select_trans: Int = 1   //翻訳方法を選ぶスピナーの選択した位置

    /* インスタンス */
    internal var qrScanIntegrator: IntentIntegrator? = null     //QRコードのスキャンに関するクラス
    private var tts: TextToSpeech? = null                       //音声読み上げに関するクラス
    private var packageManager: PackageManager? = null          //パッケージ管理(カメラあるかどうか？)するクラス
    private var contentResolver: ContentResolver? =
        null        //ファイルの位置に関して"content://・・・"形式のURIを管理するクラス

    /* textToSpeech関係 */
    private val TAG = "TestTTS"
    val TextList: List<String> = listOf("指定なし", "ギャラリー", "カメラ", "QRコード")
    var TextIndex = 0

    private lateinit var beforeTransText: EditText      //翻訳前の入力テキスト
    private lateinit var afterTransText: EditText       //翻訳後の入力テキスト
    private lateinit var beforeVoiceButton: ImageButton //翻訳前のテキスト読み上げボタン
    private lateinit var afterVoiceButton: ImageButton  //翻訳後のテキスト読み上げボタン
    private lateinit var btnGallery: Button             //ギャラリーボタン
    private lateinit var btnCamera: Button              //カメラボタン
    private lateinit var btnQR: ImageButton             //QRコードボタン
    private lateinit var btnTranslate: ImageButton      //翻訳ボタン
    private lateinit var afterLangSpinner: Spinner      //翻訳後言語スピナー
    private lateinit var selectTransSpinner: Spinner    //翻訳方法を選ぶスピナー
    private lateinit var SpanText: TextView             //スペースを空けるためのテキスト
    private lateinit var GalleryImage: ImageView        //ギャラリー用画像
    private lateinit var CameraImage: ImageView         //カメラ用画像

    /**
     * ライフサイクル
     * アプリの状態遷移によって行われるイベント
     */
    //View作成されたとき
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tts = TextToSpeech(requireContext(), this)  //ttsインスタンス生成
        this.packageManager =
            requireActivity().packageManager      //Fragment上にあるアクティビティからPackageManagerを取得
        this.contentResolver =
            requireActivity().contentResolver    //Fragment上にあるアクティビティからContentResolverを取得

        //BindingしたUI部品を連携する
        beforeTransText = binding.homeBeforeText            //翻訳前テキスト
        afterTransText = binding.homeAfterText              //翻訳後テキスト
        beforeVoiceButton = binding.homeBeforeVoice         //翻訳前読み上げボタン
        afterVoiceButton = binding.homeAfterVoice           //翻訳後読み上げボタン
        btnGallery = binding.homeGalleryButton              //ギャラリーボタン
        btnCamera = binding.homeCameraButton                //カメラボタン
        btnQR = binding.homeQRButton                        //QRコードボタン
        btnTranslate = binding.homeTranslateButton          //翻訳ボタン
        afterLangSpinner = binding.homeAfterLangSpinner     //翻訳後言語スピナー
        selectTransSpinner = binding.homeTranslateSpinner   //翻訳方法を選ぶスピナー
        SpanText = binding.SpanTextView                     //スペースを空けるためのテキスト
        GalleryImage = binding.homeGalleryImageView         //ギャラリー用画像
        CameraImage = binding.homeCameraImageView           //カメラ用画像

        selectTransSpinner.visibility = View.GONE           //今回翻訳方法を選ぶスピナーは使わないので非表示にする
        btnGallery.visibility = View.GONE                   //今回ギャラリーは使わないので非表示にする
        btnCamera.visibility = View.GONE                    //今回カメラは使わないので非表示にする
        binding.SpanTextView.visibility = View.GONE         //今回スペースを空けるためのテキストは使わないので非表示にする
        binding.homeCameraImageView.visibility = View.GONE  //今回ギャラリーは使わないので非表示にする
        binding.homeGalleryImageView.visibility = View.GONE //今回カメラは使わないので非表示にする

        //ScrollingMovementMethod(): テキストバッファをスクロールして移動キーを解釈する移動方法。
        beforeTransText.movementMethod = ScrollingMovementMethod()

        apiRequest.setApiLanguageCallback(this)     //言語取得コールバック呼び出し
        apiRequest.setApiTranslateCallback(this)    //翻訳コールバック呼び出し
        apiRequest.setApiSelectTranslateCallback(this)    //翻訳コールバック呼び出し

        //アプリ内の言語データベースのインスタンス生成
        mLangDao = LangDatabase.getInstance(requireContext()).langDao()
        //言語配列の設定
        mLangAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arrayListOf())

        roomRequest.setLangRoom(mLangDao, mLangAdapter)     //言語データベース使用のための情報を RoomFunctionに渡す
        mLangList = roomRequest.getLangList()               //言語情報を配列に格納

        spinnerItems = ArrayList<String>()      //言語スピナーアイテムをリセット

        /**
         * 言語を取得するAPI
         */
        if (mLangList.isNotEmpty()) {  //言語データベース配列リストが空でないとき
            for (i in mLangList.indices) {
                spinnerItems.add(mLangList[i].select_lang)  //言語データベース内のデータを格納
            }
        }
        apiRequest.getAPI_language()    //選択できる言語取得APIを実行

        // Adapterの作成（Adapterのパラメータ設定）
        spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item, spinnerItems
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // spinner に adapter をセット
        // View Binding
        afterLangSpinner.adapter = spinnerAdapter

        Log.d("言語ID確認", Defines.LOGIN["lang"].toString())
        //ログイン情報が登録されているとき
        if (Defines.LOGIN["lang"] != "") {
            //翻訳後の言語を登録した母語を選択した状態にする
            afterLangSpinner.setSelection(Defines.LOGIN["lang"] as Int - 1)
        }

        // 言語スピナーを押したとき
        afterLangSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                //　アイテムが選択された時
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?, position: Int, id: Long
                ) {
                    val spinnerParent = parent as Spinner               //この言語スピナーオブジェクトをスピナーとして作成
                    val item = spinnerParent.selectedItem as String     //作成したスピナーから選択したアイテムを取得
                    // View Binding
                    //binding.createSelectText.text = item        //選択したアイテムを表示
                    select_lang = position + 1                    //選択した位置を選択した言語として格納
                    Log.d("選択された言語Spinnerの位置", select_lang.toString())
                }

                //　アイテムが選択されなかった
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //特に処理なし
                }
            }

        // 翻訳方法を選ぶスピナーを押したとき
        selectTransSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                //　アイテムが選択された時
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?, position: Int, id: Long
                ) {
                    val spinnerParent =
                        parent as Spinner               //この翻訳方法を選ぶスピナーオブジェクトをスピナーとして作成
                    val item = spinnerParent.selectedItem as String     //作成したスピナーから選択したアイテムを取得
                    // View Binding
                    //binding.createSelectText.text = item        //選択したアイテムを表示
                    select_trans = position + 1                    //選択した位置を選択した言語として格納
                    Log.d("選択された「翻訳方法を選ぶスピナー」の位置", select_trans.toString())
                }

                //　アイテムが選択されなかった
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //特に処理なし
                }
            }

        //翻訳前テキストの音声読み上げボタンを押したとき
        beforeVoiceButton.setOnClickListener {
            speechText(beforeTransText)   //翻訳する前の音声読み上げ
        }

        //翻訳語テキストの音声読み上げボタンを押したとき
        afterVoiceButton.setOnClickListener {
            speechText(afterTransText)   //翻訳した後の音声読み上げ
        }


        //ギャラリーボタンを押したとき
        btnGallery.setOnClickListener {
            TextIndex = 1
            /* アプリがACTION_OPEN_DOCUMENTインテントを開始すると、
             * 条件に一致するすべてのドキュメント プロバイダが表示される */
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                /* カテゴリ（CATEGORY_OPENABLE）をインテントに追加すると、
                 * 結果がフィルタリングされ、画像ファイルなどの開くことができるドキュメントのみが表示される */
                addCategory(Intent.CATEGORY_OPENABLE)
                //typeを指定することで結果をさらにフィルタリングし、画像 MIME データタイプのドキュメントのみを表示
                type = "image/*"
            }
            startActivityForResult(intent, HomeFragment.READ_REQUEST_CODE)
        }

        //QRコードボタンを押したとき
        btnQR.setOnClickListener {
            TextIndex = 3

            //ttsButton.setOnClickListener(this)
            //qrScanIntegrator = IntentIntegrator(this.activity)
            qrScanIntegrator = IntentIntegrator.forSupportFragment(this)
            Log.d("qrScanIntegrator", qrScanIntegrator.toString())
            // 画面の回転をさせない (今回は縦画面に固定)
            qrScanIntegrator?.setOrientationLocked(true)

            // QR 読み取り後にビープ音がなるのを止める
            qrScanIntegrator?.setBeepEnabled(false)

            // スキャン開始 (QR アクティビティ生成)
            qrScanIntegrator?.initiateScan()
            Log.d("a", "確認")
        }

        //翻訳ボタンを押したとき
        btnTranslate.setOnClickListener {
//            if (select_trans == 1) {            //母国語(1)を選択時
//                apiRequest.postTranslate(
//                    Defines.MY_TOKEN,
//                    beforeTransText.text.toString()
//                )       //母国語翻訳APIを実行
//            } else if (select_trans == 2) {     //選択言語(2)を選択時
//                apiRequest.postSelectTranslate(
//                    Defines.MY_TOKEN,
//                    beforeTransText.text.toString(),
//                    select_lang
//                )       //選択言語翻訳APIを実行
//            }
            if (InputCheck()) { //空白スペースor空の入力チェック(自作)で入力がされているとき
                apiRequest.postSelectTranslate(
                    Defines.MY_TOKEN,
                    beforeTransText.text.toString(),
                    select_lang
                )       //選択言語翻訳APIを実行
            }
        }

        //翻訳前Editのフォーカスが変更したとき
        beforeTransText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {    //フォーカスが外れたとき
                //InputMethodManager をキャストしながら取得
                //入力メソッドにアクセスするメソッド
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)   //キーボードを閉じるメソッド

                //翻訳前テキストがないとき
                if (beforeTransText.text.toString().trim().isNullOrEmpty()) {
                    beforeVoiceButton.setBackgroundResource(com.example.fuzzysearch_front.R.color.no_active)     //翻訳前読み上げボタンを「アクティブ背景」色に変更
                    return@setOnFocusChangeListener
                }
                beforeVoiceButton.setBackgroundResource(com.example.fuzzysearch_front.R.color.active)     //翻訳前読み上げボタンを「アクティブ背景」色に変更
            }
        }

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            //beforeTransText.setText(it)
        })

        return root
    }

    //Viewが破棄されるとき
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Fragmentが破棄されるとき
    override fun onDestroy() {
        Log.d("status", "Fragmentの終了")
        shutDown()    //音声読み上げの終了
        super.onDestroy()
    }

    //終了時の処理
    private fun shutDown() {
        if (null != tts) {      //textToSpeechがnullでないとき
            //ネイティブリソース解放のため、textToSpeechは使わなくなるタイミングでshutdownメソッドを呼ぶ
            tts!!.shutdown()    //音声読み上げの終了
        }
    }

    //Activityが表示されたとき
    override fun onResume() {
        super.onResume()
        //val takePicture = findViewById<ImageView>(R.id.imageView2)

        //カメラボタンを押したとき
        btnCamera.setOnClickListener {
            TextIndex = 2
            // カメラ機能を実装したアプリが存在するかチェック
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager!!)?.let {
                if (checkCameraPermission()) {
                    takePicture()
                } else {
                    grantCameraPermission()
                }
            } ?: Toast.makeText(requireContext(), "カメラを扱うアプリがありません", Toast.LENGTH_LONG).show()
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
        Log.d("b", "確認")
        val fixedSize = 400

        val cameraImage = binding.homeCameraImageView

        if (TextIndex == 3) {
            Log.d("a", "確認")
            Log.d("${TextList[TextIndex]}のrequestCode", "$requestCode")
            Log.d("${TextList[TextIndex]}のresultCode", "$resultCode")
            Log.d("${TextList[TextIndex]}のdata", "$data")
            // QRコードの結果の取得
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result.contents != null) {
                //result.contents で取得した値を参照できる
                    beforeTransText.setText(result.contents.toString())     //翻訳前テキストに取得した値を代入
                //翻訳前テキストがあるとき
                if (!beforeTransText.text.toString().trim().isNullOrEmpty())
                    beforeVoiceButton.setBackgroundResource(com.example.fuzzysearch_front.R.color.active)     //翻訳前読み上げボタンを「アクティブ背景」色に変更
                //Toast.makeText(requireContext(), result.contents, Toast.LENGTH_LONG).show()
            } else {
                //翻訳前テキストがないとき
                if (beforeTransText.text.toString().trim().isNullOrEmpty())
                    beforeVoiceButton.setBackgroundResource(com.example.fuzzysearch_front.R.color.no_active)     //翻訳前読み上げボタンを「アクティブ背景」色に変更
                super.onActivityResult(requestCode, resultCode, data)
            }
        }


        //写真が選択された後の動き
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("${TextList[TextIndex]}のrequestCode", "$requestCode")
        Log.d("${TextList[TextIndex]}のresultCode", "$resultCode")
        Log.d("${TextList[TextIndex]}のdata", "$data")

        //ピッカーでドキュメントを選択して、操作が成功（RESULT_OK）していないとき
        if (resultCode != AppCompatActivity.RESULT_OK) {
            return  //処理を抜ける
        }
        when (requestCode) {
            HomeFragment.READ_REQUEST_CODE -> {
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

                        Log.d("ベース６４結果", "${strBase64}")
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
                //Log.e("読み取りデータ", "$data")
                //Log.d("", "")
            }
        }


        //カメラ関係
        if (requestCode == HomeFragment.CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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

    /**
     * カメラ使用時の設定(カメラ関係)
     */

    //カメラを起動(撮る)ときの処理
    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        startActivityForResult(intent, HomeFragment.CAMERA_REQUEST_CODE)
    }

    //カメラのアクセス許可を確認
    private fun checkCameraPermission() = PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)

    //許可カメラアクセス許可
    private fun grantCameraPermission() =
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            HomeFragment.CAMERA_PERMISSION_REQUEST_CODE
        )

    //要求のアクセス許可の結果
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == HomeFragment.CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                takePicture()
            }
        }
    }

    /**
     * textToSpeech(音声読み上げ関係)
     */

    //音声読み上げ(TextToSpeech)の初期設定
    override fun onInit(status: Int) {
        // TTS初期化
        if (TextToSpeech.SUCCESS == status) {
            Log.d(TAG, "initialized")
        } else {
            Log.e(TAG, "failed to initialize")
        }
    }

    //音声読み上げ実行
    private fun speechText(apitext: EditText) {
        Log.d("title", "2")
        val string = apitext?.text.toString()   //読み上げるテキストをString変換
        Log.d("読み上げたいText", string)

        if (0 < string.length) {    //テキストがあるとき
            if (tts!!.isSpeaking) {     //音声読み上げが使えないとき
                tts!!.stop()        //音声読み上げの停止
                return
            }
            setSpeechRate()     //読み上げのスピード
            setSpeechPitch()    //読み上げのピッチ
            if (Build.VERSION.SDK_INT >= 21) {  // SDK 21 以上
                /**
                 * textToSpeech.speak()
                 *  - text: 読み上げるテキスト
                 *  - queueMode: キューイングモード(QUEUE_ADDまたはQUEUE_FLUSH)
                 *  - params: パラメータを渡せます。(KEY_PARAM_STREAM,KEY_PARAM_VOLUME,KEY_PARAM_PAN)
                 *  - utteranceId: 固有の識別子
                 */
                tts!!.speak(string, TextToSpeech.QUEUE_FLUSH, null, "messageID")    //音声読み上げの開始
                Log.d("title", "1")
            }
            setTtsListener()    //読み上げの始まりと終わりを取得
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

    /**
     * コールバック(API関係)
     */
    //母国語翻訳API接続成功時
    override fun translate_success(obj: JSONObject) {
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            //Toast.makeText(context, obj.toString(), Toast.LENGTH_SHORT).show()
            Log.e("トークン", obj.optString("result"))
            afterTransText.setText(obj.optString("result").toString())  //翻訳後テキスト表示
        }
    }

    //母国語翻訳API接続失敗時
    override fun translate_failed(obj: JSONObject) {
    }

    //言語の取得成功時
    override fun language_success(obj: JSONArray?) {
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            roomRequest.deleteLang()
            //言語データベース配列リストが空でないとき
            if (mLangList.isNotEmpty()) spinnerItems = ArrayList<String>()  //言語スピナーアイテムをリセット
            for (i in 0 until obj!!.length()) {
                var lang_id = obj!!.getJSONObject(i).getInt("lang_id")
                var lang_name = obj!!.getJSONObject(i).getString("lang_name")
                Log.d("", "言語番号[%d]: %s".format(lang_id, lang_name))
                spinnerItems.add(lang_name)
                roomRequest.insertLang(Lang(lang_id, lang_name))
                afterLangSpinner.post {    //言語スピナーに post()して UIThreadを操作する
                    spinnerAdapter.notifyDataSetChanged()   // リストの再読み込み
                }
            }
            Log.e("Room格納した値", "${roomRequest.getLang()}")
        }
    }

    //言語の取得失敗時
    override fun language_failed() {
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            for (i in mLangList.indices) {
                spinnerItems.add(mLangList[i].select_lang)
                Log.d("a", i.toString())
            }
        }
    }

    //選択言語翻訳API接続成功時
    override fun select_translate_success(obj: JSONObject) {
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            afterVoiceButton.setBackgroundResource(com.example.fuzzysearch_front.R.color.transparent)     //翻訳後読み上げボタンを「透明」色に変更
            //.makeText(context, obj.toString(), Toast.LENGTH_SHORT).show()
            Log.e("トークン", obj.optString("result"))
            afterTransText.setText(obj.optString("result").toString())  //翻訳後テキスト表示
        }
    }

    //選択言語翻訳API接続失敗時
    override fun select_translate_failed(obj: JSONObject) {
    }

    //入力チェックを行うメソッド
    fun InputCheck(): Boolean {
        if (beforeTransText.text.toString().trim().isNullOrEmpty()) {  //翻訳前テキストの入力がないとき
            Toast.makeText(requireContext(), "翻訳前テキストを入力してください", Toast.LENGTH_SHORT).show()
            beforeTransText.setText("")    //半角全角スペース除去
            beforeVoiceButton.setBackgroundResource(com.example.fuzzysearch_front.R.color.no_active)     //翻訳前読み上げボタンを「非アクティブ背景」色に変更
            return false
        }
        beforeVoiceButton.setBackgroundResource(com.example.fuzzysearch_front.R.color.transparent)     //翻訳前読み上げボタンを「透明」色に変更
        Log.d("Status", "入力成功")
        return true
    }
}