package com.example.fuzzysearch_front.room

import android.util.Log
import android.widget.ArrayAdapter
import com.example.fuzzysearch_front.ui.history.HistoryAdapter
import com.example.fuzzysearch_front.ui.history.HistoryData

class RoomFunction {
    lateinit var mUserDao: UserDao                      //ユーザーDao(アプリ内のユーザーデータベースへの抽象アクセスを可能にするメソッド)
    lateinit var mUserAdapter: ArrayAdapter<String>     //使用するユーザー情報リストの設定(listViewなど)
    private var mUserList: List<User> = listOf()        //ユーザー情報リスト
    var mUserCheckMap = mutableMapOf<String, Any>()     //ユーザー情報を連想配列で格納する変更可能なMap

    lateinit var mLangDao: LangDao                      //言語Dao(アプリ内の言語データベースへの抽象アクセスを可能にするメソッド)
    lateinit var mLangAdapter: ArrayAdapter<String>     //使用する言語情報リストの設定(listViewなど)
    private var mLangList: List<Lang> = listOf()        //言語情報リスト

    lateinit var mHistoryDao: HistoryDao                //履歴Dao(アプリ内の履歴データベースへの抽象アクセスを可能にするメソッド)
    lateinit var mHistoryAdapter: HistoryAdapter  //使用する履歴情報リストの設定(listViewなど)
    private var mHistoryList: ArrayList<History> = ArrayList<History>()  //履歴情報リスト

    lateinit var mHisNumDao: HisNumDao                  //表示しない履歴番号Dao(アプリ内の表示しない履歴番号データベースへの抽象アクセスを可能にするメソッド)
    private var hisIds: List<Int> = listOf()            //表示しない履歴番号を格納
    /**************************************
     * 使用する配列情報の設定(Set or Get)
     **************************************/

    /**
     * ユーザーデータベース情報を設定(RoomFunctionにデータを渡す)
     * mUserDao: ユーザーDao(アプリ内のユーザーデータベースへの抽象アクセスを可能にするメソッド)
     * mAdapter: 使用するユーザー情報を格納するデータリスト
     */
    fun setUserRoom(mUserDao: UserDao, mAdapter: ArrayAdapter<String>) {
        if (mUserDao != null) {
            this.mUserDao = mUserDao
        }
        if (mAdapter != null) {
            this.mUserAdapter = mAdapter
        }
        getUser()   //ユーザー情報取得
    }

    //ユーザーデータベースの情報の入ったリストを返却する
    fun getUserList(): List<User> {
        if (mUserList != null) {
            return mUserList
        }
        return listOf()
    }

    /**
     * 言語データベース情報を設定(RoomFunctionにデータを渡す)
     * mLangDao: 言語Dao(アプリ内の言語データベースへの抽象アクセスを可能にするメソッド)
     * mAdapter: 使用する言語情報を格納するデータリスト
     */
    fun setLangRoom(mLangDao: LangDao, mAdapter: ArrayAdapter<String>) {
        if (mLangDao != null) {
            this.mLangDao = mLangDao
        }
        if (mAdapter != null) {
            this.mLangAdapter = mAdapter
        }
        getLang()   //ユーザー情報取得
    }

    //言語データベースの情報の入ったリストを返却する
    fun getLangList(): List<Lang> {
        if (mLangList != null) {
            return mLangList
        }
        return listOf()
    }

    /**
     * 履歴データベース情報を設定(RoomFunctionにデータを渡す)
     * mHistoryDao: 履歴Dao(アプリ内の履歴データベースへの抽象アクセスを可能にするメソッド)
     * mAdapter: 使用する履歴情報を格納するデータリスト
     */
    fun setHistoryRoom(mHistoryDao: HistoryDao, mAdapter: HistoryAdapter) {
        if (mHistoryDao != null) {
            this.mHistoryDao = mHistoryDao
        }
        if (mAdapter != null) {
            this.mHistoryAdapter = mAdapter
        }
        getHistory()   //履歴情報取得
    }

    //履歴データベースの情報の入ったリストを返却する
    fun getHistoryList(): ArrayList<History> {
        if (mHistoryList != null) {
            return mHistoryList
        }
        return ArrayList<History>()
    }

    /**
     * 表示しない履歴番号データベース情報を設定(RoomFunctionにデータを渡す)
     * mHiisNumDao: 履歴Dao(アプリ内の履歴データベースへの抽象アクセスを可能にするメソッド)
     * mAdapter: 使用する履歴情報を格納するデータリスト
     */
    fun setHisNumRoom(mHisNumDao: HisNumDao) {
        if (mHisNumDao != null) {
            this.mHisNumDao = mHisNumDao
        }
        getHisNum()   //表示しない履歴番号取得
    }

    //履歴データベースの情報の入ったリストを返却する
    fun getHisNumList(): List<Int> {
        if (hisIds != null) {
            return hisIds
        }
        return listOf()
    }

    /*********************************
     * Room処理
     ********************************/

    /**
     * Userデータベース関連
     */
    //ユーザー情報(データベース)を取得
    fun getUser() {
        mUserList = listOf()    //ユーザー情報リスト初期化
        mUserList = mUserDao.gAllUser()   //ユーザーデータベースのデータをユーザー情報リストに格納
//        val userInfoList = mUserList.map {
//            it.id.toString() + " / " + it.email + " / " +
//                    it.password + " / " + it.lang_id.toString() + " / " +
//                    it.token
//        }
        val userInfoList = mUserList.map {
            "${it.id+2}. " +
            "ユーザー：" + it.email + "\n    パスワード： " + it.password
        }
        if (mUserList.isNotEmpty()) {
            mUserCheckMap = mutableMapOf(
                "id" to mUserList.last().id,
                "user" to mUserList.last().email,
                "pass" to mUserList.last().password,
                "lang" to mUserList.last().lang_id,
                "token" to mUserList.last().token
            )
        }
        mUserAdapter.clear()
        if (userInfoList.isNotEmpty()) {
            mUserAdapter.addAll(userInfoList.last())    //登録した最終行のみ取得
        }
        // リストの再読み込み
        mUserAdapter.notifyDataSetChanged()
    }

    //ユーザー情報(データベース)を追加
    fun insertUser(UserData: User) {
        //val newUser = User(0, "新規さん", "password", 1, "token")
        val newUser = UserData
        mUserDao.iUser(newUser)
        getUser()
    }

    //ユーザー情報(データベース)を更新
    fun updateUser(UserData: User) {
        if (mUserList.isEmpty()) return

        val editUser = mUserList.first()
        editUser.email = UserData.email
        editUser.password = UserData.password
        editUser.lang_id = UserData.lang_id
        editUser.token = UserData.token
        mUserDao.uUser(editUser)
        getUser()
    }

    //ユーザー情報(データベース)を削除
    fun deleteUser() {
        if (mUserList.isEmpty()) return

        val deleteUser = mUserList.first()
        mUserDao.dUser(deleteUser)    //最初の一件削除
        getUser()
    }

    //ユーザー情報(データベース)を全件削除
    fun deleteAllUser() {
        if (mUserList.isEmpty()) return

        mUserDao.dAllUser()     //全件削除
        getUser()
    }

    /**
     * Langデータベース関連
     */
    fun getLang() {
        mLangList = listOf()    //ユーザー情報リスト初期化
        mLangList = mLangDao.gAllLang()   //ユーザーデータベースのデータをユーザー情報リストに格納
        val LangInfoList = mLangList.map {
            it.lang_id.toString() + " / " + it.select_lang
        }
        mLangAdapter.clear()
        mLangAdapter.addAll(LangInfoList)
        // リストの再読み込み
        mLangAdapter.notifyDataSetChanged()
    }

    //ユーザー情報(データベース)を追加
    fun insertLang(LangData: Lang) {
        //val newLang = Lang(0, "新規さん", "password", 1, "token")
        val newLang = LangData
        mLangDao.iLang(newLang)
        getLang()
    }

    //ユーザー情報(データベース)を更新
    fun updateLang() {
        if (mLangList.isEmpty()) return

        val editLang = mLangList.first()
        editLang.select_lang = "更新されました"
        mLangDao.uLang(editLang)
        getLang()
    }

    //ユーザー情報(データベース)を削除
    fun deleteLang() {
        if (mLangList.isEmpty()) return

        val deleteLang = mLangList.first()
        mLangDao.dAllLang()
        getLang()
    }

    /**
     * Historyデータベース関連
     */
    //ユーザー情報(データベース)を取得
    fun getHistory() {
        mHistoryList = ArrayList<History>()    //ユーザー情報リスト初期化
        /**
         * 指定した履歴を表示しないSELECT文
         * hisId: 表示しない履歴番号
         */
        getHisNum() //表示しない履歴番号を取得
        val result: HashSet<Int> = hisIds.toHashSet()
        Log.d("HashSet<Int>", result.toString())
        mHistoryList += mHistoryDao.gHistory(result)   //ユーザーデータベースのデータをユーザー情報リストに格納
//        mHistoryList += mHistoryDao.gAllHistory()   //ユーザーデータベースのデータをユーザー情報リストに格納
        Log.d("mHistoryデータ残っているモノ(Room)", mHistoryList.toString())
        Log.d("mHistoryAdapterカウント", mHistoryAdapter.itemCount.toString())
        //mHistoryAdapter.notifyItemRangeRemoved(0, mHistoryAdapter.itemCount)
        // リストの再読み込み
        mHistoryAdapter.notifyDataSetChanged()
        //mHistoryAdapter.clear()
        mHistoryAdapter.delAll()
        if (mHistoryList.isNotEmpty()) {    //取得したユーザーデータが空でないとき
            var historyInfoList: List<History> = listOf()
            for (i in mHistoryList.indices) {
                historyInfoList = listOf(
                    History(
                        mHistoryList[i].history_number,
                        mHistoryList[i].history_date,
                        mHistoryList[i].trans_before_lang,
                        mHistoryList[i].trans_after_lang,
                        mHistoryList[i].trans_before_text,
                        mHistoryList[i].trans_after_text
                    )
                )
                /*mHistoryList.map {
                    it.history_number.toString() + " / " + it.history_date + "/" +
                            it.trans_before_lang + "/" + it.trans_after_lang + "/" +
                            it.trans_before_text + "/" + it.trans_after_text
                }*/
                Log.d("historyInfoList", historyInfoList.toString())
                //一件ずつ取得
                mHistoryAdapter.add(
                    listOf(
                        HistoryData(
                            historyCnt = historyInfoList[0].history_number,
                            historyDate = historyInfoList[0].history_date,
                            transBeforeLang = historyInfoList[0].trans_before_lang,
                            transAfterLang = historyInfoList[0].trans_after_lang,
                            transBeforeText = historyInfoList[0].trans_before_text,
                            transAfterText = historyInfoList[0].trans_after_text
                        )
                    )
                )
                Log.d("HistoryData", mHistoryAdapter.toString())
            }
        }
        // リストの再読み込み
        mHistoryAdapter.notifyDataSetChanged()
    }

    //ユーザー情報(データベース)を追加
    fun insertHistory(HistoryData: History) {
        //val newHistory = History(0, "新規さん", "password", 1, "token")
        Log.d("HistoryData", HistoryData.toString())
        val newHistory =
            History(
                HistoryData.history_number,
                HistoryData.history_date,
                HistoryData.trans_before_lang,
                HistoryData.trans_after_lang,
                HistoryData.trans_before_text,
                HistoryData.trans_after_text
            )
        mHistoryDao.iHistory(newHistory)
        getHistory()
    }

    //ユーザー情報(データベース)を更新
    fun updateHistory(HistoryData: History) {
        if (mHistoryList.isEmpty()) return

        val editHistory = mHistoryList.first()
//        editHistory.history_number = HistoryData.history_number
        editHistory.history_date = "未設定"
        editHistory.trans_before_lang = "翻訳前言語"
        editHistory.trans_after_lang = "翻訳後言語"
        editHistory.trans_before_text = "翻訳前テキスト"
        editHistory.trans_after_text = "翻訳後テキスト"
        mHistoryDao.uHistory(editHistory)
        getHistory()
    }

    //ユーザー情報(データベース)を削除
    fun deleteHistory(HistoryData: History, swipIndex: Int) {
        if (mHistoryList.isEmpty()) return

        val newHistory = listOf(HistoryData).first()
        var selectHisNumber = HistoryData.history_number
        Log.d("選択した番号", selectHisNumber.toString())
        Log.d("選択した位置", swipIndex.toString())
        var deleteHistory = mHistoryList[swipIndex]
        Log.d("削除される行", deleteHistory.toString())
        Log.d("history_number", deleteHistory.history_number.toString())
        mHistoryDao.dHistory(selectHisNumber)    //最初の一件削除
        hisIds += selectHisNumber
        insertHisNum(HisNum(selectHisNumber, selectHisNumber))
        getHistory()
    }

    //ユーザー情報(データベース)を全件削除
    fun deleteAllHistory() {
        if (mHistoryList.isEmpty()) return
        Log.d("deleteAllHistory", "ここまできた")
        mHistoryDao.dAllHistory()     //全件削除
        getHistory()
    }

    /**
     * HisNumデータベース関連
     */
    //ユーザー情報(データベース)を取得
    fun getHisNum() {
        hisIds = listOf()    //ユーザー情報リスト初期化
        /**
         * 指定した履歴を表示しないSELECT文
         * hisId: 表示しない履歴番号
         */
        hisIds = mHisNumDao.gAllHisNum().distinct()   //表示しない履歴番号データベースのデータを表示しない履歴番号情報リストに格納
        Log.d("表示しない履歴番号(Room)", hisIds.toString())
    }

    //ユーザー情報(データベース)を追加
    fun insertHisNum(HisNumData: HisNum) {
        //val newHisNum = HisNum(0, "新規さん", "password", 1, "token")
        Log.d("HisNumData", HisNumData.toString())
        val newHisNum =
            HisNum(
                HisNumData.hisnum_id,
                HisNumData.his_number
            )
        mHisNumDao.iHisNum(newHisNum)
        getHisNum()
    }

    //ユーザー情報(データベース)を削除
    fun deleteHisNum(HisNumData: HisNum) {
        if (hisIds.isEmpty()) return

        var selectHisNumber = HisNumData.his_number
        Log.d("選択した番号", selectHisNumber.toString())
        mHisNumDao.dHisNum(selectHisNumber)    //最初の一件削除
        getHisNum()
    }

    //ユーザー情報(データベース)を全件削除
    fun deleteAllHisNum() {
        if (hisIds.isEmpty()) return

        Log.d("deleteAllHisNum", "ここまできた")
        mHisNumDao.dAllHisNum()     //全件削除
        getHisNum()
    }
}