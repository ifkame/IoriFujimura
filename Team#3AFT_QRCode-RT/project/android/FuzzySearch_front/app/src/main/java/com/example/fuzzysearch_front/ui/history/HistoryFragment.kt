package com.example.fuzzysearch_front.ui.history

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fuzzysearch_front.Defines
import com.example.fuzzysearch_front.R
import com.example.fuzzysearch_front.databinding.FragmentHistoryBinding
import com.example.fuzzysearch_front.room.*
import com.example.http_post.OkHttp3Callback
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryFragment : Fragment(), OkHttp3Callback.ApiHistoryCallback {
    //    var periodindex: Int = 0
    var apiRequest = OkHttp3Callback()  //API処理クラス呼び出し
    var roomRequest = RoomFunction()    //Room処理クラス呼び出し

    /**
     * API から取得するデータセットを想定したプロパティ。
     */
    //private val dataSet = mutableListOf<String>()
    lateinit var mHistoryDao: HistoryDao            //履歴Dao(アプリ内の履歴データベースへの抽象アクセスを可能にするメソッド)
    lateinit var mHisNumDao: HisNumDao              //表示しない履歴番号Dao(アプリ内の表示しない履歴番号データベースへの抽象アクセスを可能にするメソッド)
    lateinit var mHistoryAdapter: HistoryAdapter    //使用する履歴情報を格納するデータリスト
    private var rHistoryList: ArrayList<History> = ArrayList<History>()  //
    private var mHistoryList: ArrayList<HistoryData> = ArrayList<HistoryData>()
    private var hisId: List<Int> = listOf()         //表示しない履歴番号を格納
    private var HistoryCnt: Int = 0                    //ユーザー情報リストの最終番号を格納

    private val swipeToDismissTouchHelper by lazy {
        getSwipeToDismissTouchHelper(mHistoryAdapter)
    }

    /**
     * API に問い合わせ中は true になる。
     */
    private var nowLoading = false

    //private lateinit var myAdapter: MyAdapter
    private lateinit var historyAdapter: HistoryAdapter

    private val handler = Handler()

    private val progressBar by lazy { binding.progressBar }

    private lateinit var historyViewModel: HistoryViewModel
    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val samp_view = binding.recyclerView        //表示用 RecyclerView

        progressBar.visibility = View.VISIBLE       // API 問い合わせ中は 表示 となる。
        apiRequest.setApiHistoryCallback(this@HistoryFragment)  //履歴コールバック設定

        //アプリ内の履歴データベースのインスタンス生成
        mHistoryDao = HistoryDatabase.getInstance(requireContext()).historyDao()
        //履歴配列の設定
        mHistoryAdapter = HistoryAdapter(mHistoryList)

        //アプリ内の表示しない履歴番号データベースのインスタンス生成
        mHisNumDao = HisNumDatabase.getInstance(requireContext()).hisNumDao()

        // LayoutManagerの設定
        samp_view.layoutManager = LinearLayoutManager(requireContext())
        /**
         * RecyclerView のレイアウトサイズはアクティビティのレイアウトで変更されるので、recyclerView の setHasFixedSize を true にセットしておきます
         * この設定はパフォーマンス向上のためだけに必要です。必須ではありません
         * コンテンツの内容が変わっても RecyclerView のレイアウトサイズが変わらない場合に有効です
         */
        samp_view.setHasFixedSize(true)
        samp_view.adapter = mHistoryAdapter     //履歴データベースとRecyclerViewと関連付け


        roomRequest.setHisNumRoom(mHisNumDao)   //表示しない履歴番号データベース使用のための情報を RoomFunctionに渡す
        //履歴データベース使用のための情報を RoomFunctionに渡す
        roomRequest.setHistoryRoom(
            mHistoryDao,
            mHistoryAdapter
        )

        /**
         * 履歴(Room)取得
         * 0: onCreate()時(最初)に取得
         * 1: onCreate()以降(2回目~)に取得
         */
        history_get_room(0)
//        history_get_room(1)
        hisId = roomRequest.getHisNumList()     //表示しない履歴番号を取得
        /**
         * 履歴を取得するAPI
         */
        if (rHistoryList.isNotEmpty()) {  //履歴データベース配列リストが空でないとき
            rHistoryList = roomRequest.getHistoryList()      //ユーザー情報を配列に格納
            Log.e("Roomの中身", rHistoryList.toString())
        }
        /*//配列の要素数を超えてエラー落ちすることを防ぐために中身を一新する
        roomRequest.deleteAllHistory()
        roomRequest.deleteAllHisNum()*/

        apiRequest.getAPI_history(Defines.MY_TOKEN)            //履歴コールバック実行

        HistoryCnt = roomRequest.mHistoryAdapter.itemCount

        //スワイプの動きと使用するRecycleViewと連携
        swipeToDismissTouchHelper.attachToRecyclerView(samp_view)

        historyViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    /**
//     * API でリストデータを取得することを想定したメソッド。
//     */
//    private suspend fun fetch(index: Int): List<HistoryData> {
//
//        // API 問い合わせの待ち時間を仮想的に作る。
//        handler.post { progressBar.visibility = View.VISIBLE }
//        //delay(200)
//        handler.post { progressBar.visibility = View.INVISIBLE }
//        //Log.e("", "$periodindex")
//
//        return when (index) {
//            /*in 0..90 -> dataSet.slice(index..index + 9)
//            in 91..99 -> dataSet.slice(index..99)
//            else -> listOf()*/
//            in 0 until periodindex - (periodindex % 5) -> mHistoryList.slice(index..index + 4) as List<HistoryData>
//            in periodindex - (periodindex % 5)..periodindex -> mHistoryList.slice(index..periodindex) as List<HistoryData>
//            else -> listOf()
//        }
//    }
//
//    /**
//     * API でリストデータを取得して画面に反映することを想定したメソッド。
//     */
//    private suspend fun fetchAndUpdate(index: Int) {
//        val fetchedData = withContext(Dispatchers.Default) {
//            fetch(index)
//        } as List<HistoryData>
//
//        // 取得したデータを画面に反映。
//        if (fetchedData.isNotEmpty()) {
//            handler.post {
//                handler.post { historyAdapter.add(fetchedData) }
//            }
//        }
//        // 問い合わせが完了したら false に戻す。
//        nowLoading = false
//    }
//
//    /**
//     * リストの下端までスクロールしたタイミングで発火するリスナー。
//     */
//    inner class InfiniteScrollListener : RecyclerView.OnScrollListener() {
//        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//            super.onScrolled(recyclerView, dx, dy)
//
//            // アダプターが保持しているアイテムの合計
//            val itemCount = historyAdapter.itemCount
//            // 画面に表示されているアイテム数
//            val childCount = recyclerView.childCount
//            val manager = recyclerView.layoutManager as LinearLayoutManager
//            // 画面に表示されている一番上のアイテムの位置
//            val firstPosition = manager.findFirstVisibleItemPosition()
//
//            // 何度もリクエストしないようにロード中は何もしない。
//            if (nowLoading) {
//                return
//            }
//
//            // 以下の条件に当てはまれば一番下までスクロールされたと判断できる。
//            if (itemCount == childCount + firstPosition) {
//                // API 問い合わせ中は true となる。
//                nowLoading = true
//                GlobalScope.launch {
//                    fetchAndUpdate(historyAdapter.itemCount)
//                }
//            }
//        }
//    }

    override fun history_success(obj: JSONObject) {
        // 別スレッドで反映
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            roomRequest.deleteAllHistory()
            val JSONArrayList = obj.getJSONArray("data")
            if (JSONArrayList != JSONObject.NULL) {
                //Toast.makeText(context, obj.toString(), Toast.LENGTH_SHORT).show()
                for (i in 0 until JSONArrayList.length()) {
                    var historyNumber = JSONArrayList.getJSONObject(i).optInt("data_id")
                    var historyDate = JSONArrayList.getJSONObject(i).optString("recoded_date")
                    var after_lang_code =
                        JSONArrayList.getJSONObject(i).optString("after_lang_code")
                    var before_lang_code =
                        JSONArrayList.getJSONObject(i).optString("before_lang_code")
                    var before_translation =
                        JSONArrayList.getJSONObject(i).optString("before_translation")
                    var after_translation =
                        JSONArrayList.getJSONObject(i).optString("after_translation")
                    //Log.d("", "言語番号[%d]: %s → %s, %s → %s".format(i, before_lang_code, after_lang_code, before_translation, after_translation))
                    //Log.d("date","${SimpleDateFormat("yyyy/MM/dd").format(Date(historyDate))}")

                    //成功時に登録したユーザー情報リストを生成
                    var HistorySuccessData =
                        HistoryData(
                            historyNumber,
                            SimpleDateFormat("yyyy/MM/dd").format(Date(historyDate)),
                            before_lang_code,
                            after_lang_code,
                            before_translation,
                            after_translation
                        )

                    var HistoryRoomData =
                        History(
                            historyNumber,
                            SimpleDateFormat("yyyy/MM/dd").format(Date(historyDate)),
                            before_lang_code,
                            after_lang_code,
                            before_translation,
                            after_translation
                        )

                    mHistoryList.add(HistorySuccessData)
                    roomRequest.insertHistory(HistoryRoomData)     //ユーザーデータベースの行追加
                    Log.d("HistorySuccessData", HistorySuccessData.toString())
                    Log.d("HistoryRoomData", HistoryRoomData.toString())
//                    Toast.makeText(requireContext(), "${HistorySuccessData}", Toast.LENGTH_SHORT).show()
                    //mHistoryList += HistorySuccessData
                }
                progressBar.visibility = View.INVISIBLE     // API 問い合わせ終了時は 非表示 となる。

//                periodindex = mHistoryList.lastIndex
            } else {
                Toast.makeText(context, "データが存在しません", Toast.LENGTH_SHORT).show()
            }

            //Log.v("", "$periodindex")
//            binding.run {
//                val listData = runBlocking { fetch(0) }
//                historyAdapter = HistoryAdapter(listData as List<HistoryData>)
//                binding.recyclerView.also {
//                    it.layoutManager = LinearLayoutManager(context)
//                    it.adapter = historyAdapter
//                    it.addItemDecoration(
//                        DividerItemDecoration(
//                            context,
//                            DividerItemDecoration.VERTICAL
//                        )
//                    )
//                    it.addOnScrollListener(InfiniteScrollListener())
//                }
//                progressBar.visibility = View.INVISIBLE
//            }
        }

    }

    override fun history_failed(obj: JSONObject) {
        val status: Int = obj.getInt("status")
        if (status == 400) {
            // 別スレッドで反映
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post {
                //配列の要素数を超えてエラー落ちすることを防ぐために中身を一新する
                roomRequest.deleteAllHistory()
                roomRequest.deleteAllHisNum()
                progressBar.visibility = View.INVISIBLE       // API 問い合わせ終了時は 非表示 となる。
                Toast.makeText(requireContext(), "履歴がありません", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     *
     */
    private fun history_get_room(his_check: Int) {
        if (his_check == 0) {
            rHistoryList = roomRequest.getHistoryList()    //履歴情報を配列に格納

            if (rHistoryList.isNotEmpty()) {  //言語データベース配列リストが空でないとき
                mHistoryList = ArrayList<HistoryData>()
                for (i in rHistoryList.indices) {
                    //言語データベース内のデータを格納
                    mHistoryList.add(
                        HistoryData(
                            rHistoryList[i].history_number,
                            rHistoryList[i].history_date,
                            rHistoryList[i].trans_before_lang,
                            rHistoryList[i].trans_after_lang,
                            rHistoryList[i].trans_before_text,
                            rHistoryList[i].trans_before_text
                        )
                    )
                }

            }
        } else if (his_check == 1) {
            roomRequest.getHistory()
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
                    requireContext(),
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