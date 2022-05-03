package com.example.wisperapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

// インターフェースの実装
// （行う処理の中身は呼び出したクラス側で記述する）
interface ChangeGoodListener {  //イイね画像を切り替える
    fun GoodTapped(userdetail: UserDetailData, position: Int)
}
interface MoveUserListener {    //選択したユーザーの画面に遷移する
    fun IconTapped(userdetail: UserDetailData, position: Int)
}

//ユーザー詳細アダプター(アイコン画像, ユーザー名, 内容, いいね画像)
class UserDetailAdapter internal constructor
    (private var UserDetailDataList: List<UserDetailData>,
     private val Glistener: ChangeGoodListener,
     private val Ulistener: MoveUserListener) : RecyclerView.Adapter<UserDetailAdapter.ViewHolder>() {

    //クリックイベント実装の別解↓
    //イベント発生時に実行させたい関数の参照を保存しておくプロパティ
    //var onDetailIconClick : (View) -> Unit by Delegates.notNull()
    //var onGoodImageClick : (View) -> Unit by Delegates.notNull()

    // Viewの初期化
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val detail_iconId: ImageView    //アイコン画像
        val userName: TextView          //ユーザー名
        val postContent: TextView       //内容
        val goodId: ImageView           //いいね画像

        //レイアウトとアダプター内で使用する型を紐づける
        init {
            detail_iconId = view.findViewById(R.id.detail_iconId)   //アイコン画像オブジェクト生成
            userName = view.findViewById(R.id.userName)             //ユーザー名オブジェクト生成
            postContent = view.findViewById(R.id.postContent)       //内容オブジェクト生成
            goodId = view.findViewById(R.id.goodId)                 //いいね画像オブジェクト生成
        }
    }

    // レイアウトの設定
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        //ユーザー詳細用のRecyclerViewのパラメータ格納
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.user_detail_view_holder, viewGroup, false)
        return ViewHolder(view)     //ユーザー詳細のアイテムリスト生成
    }

    // RecyclerViewの設定(作成)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val UserDetailData = UserDetailDataList[position]   //指定された配列リストのデータをそれぞれの変数に格納

        //ユーザー詳細の ViewHolderのレイアウトに与えられた値を設定
        viewHolder.detail_iconId.setImageResource(UserDetailData.detail_iconId) //アイコン画像 → iconId
        viewHolder.userName.text = UserDetailData.userName                      //ユーザー名 → userName
        viewHolder.postContent.text = UserDetailData.postContent                //内容 → postContent
        viewHolder.goodId.setImageResource(UserDetailData.goodId)               //いいね画像 → goodId

        //実行したいアイテムのsetOnClickListenerに対して関数を割り当てる
        viewHolder.detail_iconId.setOnClickListener {//アイコン画像クリック時
            Ulistener.IconTapped(UserDetailData, position)
        }
        viewHolder.goodId.setOnClickListener {//いいね画像クリック時
            Glistener.GoodTapped(UserDetailData, position)
        }
        //クリックイベント実装の別解↓
        //viewHolder.detail_iconId.setOnClickListener(onDetailIconClick)
        //viewHolder.goodId.setOnClickListener(onGoodImageClick)
    }

    // 表示数を返す
    override fun getItemCount() = UserDetailDataList.size
}