package com.example.kotlin_coroutines

import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class ServiceName() {
    private var cnt = 0     //Kotlin Coroutineで使用するカウンタ変数

    suspend fun main():String = runBlocking {
        val n = 2
        val plus_1 = async {
            delay(1000L)
            n + 1
        }
        val mult_3 = async {
            delay(1000L)
            n * 3
        }

        val t = measureTimeMillis {
            val amount = plus_1.await() + mult_3.await()
            println(amount)
        }
        return@runBlocking "Time: ${t}ms"
    }

    suspend fun ServiceAll(tx1: TextView, tx2: TextView):String = runBlocking {                             // Dispatchers.Main
        var await1: Unit? = null
        var await2: Unit? = null

        val resultA = async {
            ServiceA(tx1) // Dispatchers.Main for `get`
        }
        val resultB = async {
            ServiceB(tx2) // Dispatchers.Main for `get`
        }
        val serAtime = measureTimeMillis {
            val amount = resultA.await()
            await1 = amount
            println(amount)
        }
        val serBtime = measureTimeMillis {
            val amount = resultB.await()
            await2 = amount
            println(amount)
        }
        var serAlltime = "サービスA：${await1}, サービスB：${await2}"

        Log.w("結果(サービスA): ", "Time: ${serAtime}ms")                 // Dispatchers.Main
        Log.w("結果(サービスB): ", "Time: ${serBtime}ms")                 // Dispatchers.Main
        Log.w("結果(サービスAll): ", "Time: ${serAlltime}ms")                 // Dispatchers.Main
        return@runBlocking "Time: ${serAlltime}ms"
    }

    //
    suspend fun ServiceA(it: TextView) {
        var textView1 = it

        cnt++
        textView1.text = "サービスA: {$cnt}"

        delay(1000L) // suspend functionを直接呼べる
    }

    suspend fun ServiceB(it: TextView) {
        var textView2 = it

        cnt++
        textView2.text = "サービスB: {$cnt}"

        delay(2000L) // suspend functionを直接呼べる
    }
}