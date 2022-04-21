package com.example.kotlin_coroutines

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {

            var textView1 = findViewById<TextView>(R.id.text1)
            var textView2 = findViewById<TextView>(R.id.text2)
            var textView3 = findViewById<TextView>(R.id.text3)
            var button1 = findViewById<Button>(R.id.button1)


            button1.setOnClickListener {
                scope.launch {
                    textView3.text = ServiceName().ServiceAll(tx1 = textView1, tx2 = textView2)
                    //textView3.text = ServiceName().main()
                }
            }
        } catch (e: Exception) {
            // onCancelledメソッドと同等の処理
            Log.e(localClassName, "ここにキャンセル時の処理を記述", e)
        }
    }
}
