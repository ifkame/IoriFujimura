package com.example.wisperapp

data class FollowInfoData(
    val iconId: Int,                //アイコン画像
    val user_name_text: String,     //ユーザー名
    val followCnt: Int,             //フォロー数
    val followerCnt: Int            //フォロワー数
)
