package com.example.wisperapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GoodCntAdapter internal constructor(private var GoodCntDataList: List<GoodCntData>) : RecyclerView.Adapter<GoodCntAdapter.ViewHolder>() {
    // Viewの初期化
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val good_user_iconId: ImageView     //アイコン画像
        val good_userName: TextView         //ユーザー名
        val good_postContent: TextView      //内容
        val goodCnt: TextView               //イイね数

        init {
            good_user_iconId = view.findViewById(R.id.good_user_iconId)
            good_userName = view.findViewById(R.id.good_userName)
            good_postContent = view.findViewById(R.id.good_postContent)
            goodCnt = view.findViewById(R.id.goodCnt)
        }
    }

    // レイアウトの設定
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.good_cnt_view_holder, viewGroup, false)
        return ViewHolder(view)
    }

    // Viewの設定
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val UserData = GoodCntDataList[position]

        viewHolder.good_user_iconId.setImageResource(UserData.good_user_iconId)
        viewHolder.good_userName.text = UserData.good_userName.toString()
        viewHolder.good_postContent.text = UserData.good_postContent.toString()
        viewHolder.goodCnt.text = UserData.goodCnt.toString()
    }

    // 表示数を返す
    override fun getItemCount() = GoodCntDataList.size
}