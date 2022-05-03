package com.example.wisperapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// インターフェースの実装
// （行う処理の中身は呼び出したクラス側で記述する）
interface MoveFollowListener {    //選択したユーザーの画面に遷移する
    fun IconFTapped(follow: FollowInfoData, position: Int)
}

class FollowInfoAdapter internal constructor
    (private var FollowInfoList: List<FollowInfoData>,
     private val Flistener: MoveFollowListener) : RecyclerView.Adapter<FollowInfoAdapter.ViewHolder>() {

    // Viewの初期化
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconId: ImageView
        val user_name_text: TextView
        val followCnt: TextView
        val followerCnt: TextView

        init {
            iconId = view.findViewById(R.id.iconId)
            user_name_text = view.findViewById(R.id.user_name_text)
            followCnt = view.findViewById(R.id.followCnt)
            followerCnt = view.findViewById(R.id.followerCnt)
        }
    }

    // レイアウトの設定
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.follow_info_view_holder, viewGroup, false)
        return ViewHolder(view)
    }

    // Viewの設定
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val FollowInfo = FollowInfoList[position]

        viewHolder.iconId.setImageResource(FollowInfo.iconId)
        viewHolder.user_name_text.text = FollowInfo.user_name_text.toString()
        viewHolder.followCnt.text = FollowInfo.followCnt.toString()
        viewHolder.followerCnt.text = FollowInfo.followerCnt.toString()

        //実行したいアイテムのsetOnClickListenerに対して関数を割り当てる
        viewHolder.iconId.setOnClickListener {//アイコン画像クリック時
            Flistener.IconFTapped(FollowInfo, position)
        }
    }

    // 表示数を返す
    override fun getItemCount() = FollowInfoList.size
}