package com.example.wisperapp

//ユーザー詳細データ
data class UserDetailData(
    val detail_iconId: Int,     //アイコン画像
    val userName: String,       //ユーザー名
    val postContent: String,    //内容
    val goodId: Int             //いいね画像
)
