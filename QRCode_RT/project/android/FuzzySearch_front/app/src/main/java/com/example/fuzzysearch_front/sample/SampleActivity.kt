package com.example.fuzzysearch_front.sample

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fuzzysearch_front.R
import com.example.fuzzysearch_front.room.*
import com.example.fuzzysearch_front.ui.history.HistoryAdapter
import com.example.fuzzysearch_front.ui.history.HistoryData

class SampleActivity : AppCompatActivity() {
    var roomRequest = RoomFunction()    //Room処理クラス呼び出し

    lateinit var mHistoryDao: HistoryDao      //ユーザーDao(アプリ内のユーザーデータベースへの抽象アクセスを可能にするメソッド)
    lateinit var mHisNumDao: HisNumDao              //表示しない履歴番号Dao(アプリ内の表示しない履歴番号データベースへの抽象アクセスを可能にするメソッド)
    lateinit var mHistoryAdapter: HistoryAdapter        //使用するユーザー情報を格納するデータリスト
    private var rHistoryList: ArrayList<History> = ArrayList<History>()  //
    private var mHistoryList: ArrayList<HistoryData> = ArrayList<HistoryData>()
    private var hisId: List<Int> = listOf()         //表示しない履歴番号を格納
    private var HistoryCnt: Int = 0                    //ユーザー情報リストの最終番号を格納

    private val swipeToDismissTouchHelper by lazy {
        getSwipeToDismissTouchHelper(mHistoryAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        val insert_button = findViewById<Button>(R.id.insert_button)               //追加用 Button
        val update_button = findViewById<Button>(R.id.update_button)               //更新用 Button
        val delete_button = findViewById<Button>(R.id.delete_button)               //削除用 Button
        val samp_view =
            findViewById<RecyclerView>(R.id.samp_recycler)             //表示用 RecyclerView

        //アプリ内のユーザーデータベースのインスタンス生成
        mHistoryDao = HistoryDatabase.getInstance(this).historyDao()
        //ユーザー配列の設定
        mHistoryAdapter = HistoryAdapter(mHistoryList)

        //アプリ内の表示しない履歴番号データベースのインスタンス生成
        mHisNumDao = HisNumDatabase.getInstance(this).hisNumDao()

        // LayoutManagerの設定
        samp_view.layoutManager = LinearLayoutManager(this)
        // sAdapterの生成と設定
        mHistoryAdapter = HistoryAdapter(mHistoryList)
        /**
         * RecyclerView のレイアウトサイズはアクティビティのレイアウトで変更されるので、recyclerView の setHasFixedSize を true にセットしておきます
         * この設定はパフォーマンス向上のためだけに必要です。必須ではありません
         * コンテンツの内容が変わっても RecyclerView のレイアウトサイズが変わらない場合に有効です
         */
        samp_view.setHasFixedSize(true)

        samp_view.adapter = mHistoryAdapter    //ユーザーデータベースの行を全件表示

        roomRequest.setHisNumRoom(mHisNumDao)   //表示しない履歴番号データベース使用のための情報を RoomFunctionに渡す
        roomRequest.setHistoryRoom(
            mHistoryDao,
            mHistoryAdapter
        )     //ユーザーデータベース使用のための情報を RoomFunctionに渡す

        rHistoryList.addAll(roomRequest.getHistoryList())      //ユーザー情報を配列に格納

        Log.d("mHistoryAdapter[1]", mHistoryAdapter.toString())


        //mHistoryAdapter.notifyDataSetChanged()

        Log.d("mHistoryAdapter[2]", mHistoryAdapter.toString())

        HistoryCnt = roomRequest.mHistoryAdapter.itemCount

        //スワイプの動きと使用するRecycleViewと連携
        swipeToDismissTouchHelper.attachToRecyclerView(samp_view)

        insert_button.setOnClickListener {
            HistoryCnt += 1
            Log.d("HistoryCnt(INS)", HistoryCnt.toString())
            //ユーザーデータベースの行追加
            roomRequest.insertHistory(
                History(
                    HistoryCnt,
                    "2021-01-01",
                    "EN",
                    "JA",
                    "This is pen.",
                    "これはペンです。"
                )
            )
        }
        update_button.setOnClickListener {
            //ユーザーデータベースの更新
            roomRequest.updateHistory(
                History(
                    HistoryCnt,
                    "2021-01-01",
                    "EN",
                    "JA",
                    "This is pen.",
                    "これはペンです。"
                )
            )
        }
        delete_button.setOnClickListener {
            HistoryCnt = 0
            Log.d("mHistoryAdapter", mHistoryAdapter.toString())
            //ユーザーデータベースの履歴削除
            roomRequest.deleteAllHistory()
        }
    }

    private fun getSwipeToDismissTouchHelper(adapter: HistoryAdapter) =
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.ACTION_STATE_IDLE,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //val timeTasks = adapter.getItems()
                adapter.notifyDataSetChanged()
                Log.d("スワイプした履歴の位置", "${viewHolder.layoutPosition}")
                val timeTasks: List<HistoryData> = adapter.getItem(viewHolder.layoutPosition)
                Log.d("スワイプした履歴内容", "$timeTasks")
                var swipNum = viewHolder.layoutPosition
                adapter.del(swipNum)
                roomRequest.deleteHistory(
                    History(
                        timeTasks.first().historyCnt,
                        timeTasks.first().historyDate,
                        timeTasks.first().transBeforeLang,
                        timeTasks.first().transAfterLang,
                        timeTasks.first().transBeforeText,
                        timeTasks.first().transAfterText
                    ), swipNum
                )
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val itemView = viewHolder.itemView
                val background = ColorDrawable(Color.RED)
                val deleteIcon = AppCompatResources.getDrawable(
                    this@SampleActivity,
                    R.drawable.icon_garbagecan
                )
                val iconMarginVertical =
                    (viewHolder.itemView.height - deleteIcon!!.intrinsicHeight) / 2

                if (dX.toInt() == 0) {      //指を話したときに背景とアイコンの位置を戻す
                    deleteIcon.setBounds(0, 0, 0, 0)
                    background.setBounds(0, 0, 0, 0)
                } else {
                    deleteIcon.setBounds(
                        itemView.left + iconMarginVertical,
                        itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMarginVertical
                    )
                    background.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.right + dX.toInt(),
                        itemView.bottom
                    )
                }

                background.draw(c)
                deleteIcon.draw(c)
            }
        })
}