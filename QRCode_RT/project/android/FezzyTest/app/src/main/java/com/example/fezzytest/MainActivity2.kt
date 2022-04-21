package com.example.fezzytest

import android.content.Intent
import android.nfc.NfcAdapter.EXTRA_DATA
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity2 : AppCompatActivity() {
    internal var qrScanIntegrator: IntentIntegrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        //ttsButton.setOnClickListener(this)
        qrScanIntegrator = IntentIntegrator(this)

        // 画面の回転をさせない (今回は縦画面に固定)
        qrScanIntegrator?.setOrientationLocked(true)

        // QR 読み取り後にビープ音がなるのを止める
        qrScanIntegrator?.setBeepEnabled(false)

        // スキャン開始 (QR アクティビティ生成)
        qrScanIntegrator?.initiateScan()
    }
    // 読み取り後に呼ばれるメソッド
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 結果の取得
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            // result.contents で取得した値をMainActivityに送る
            intent.putExtra(EXTRA_DATA,result.contents)
            var qrdata = result.contents.toString()
            val intent = Intent(this, MainActivity::class.java).run {
                //qrdataをQRDATAをキーにして準備
                putExtra("QRDATA", qrdata)
            }
            startActivity(intent)
        }

        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
        
    }
}