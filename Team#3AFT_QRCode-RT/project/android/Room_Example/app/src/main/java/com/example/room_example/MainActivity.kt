package com.example.room_example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

class MainActivity : AppCompatActivity() {
    lateinit var mUserDao: UserDao
    lateinit var mAdapter: ArrayAdapter<String>

    private var mUserList: List<User> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val insert_button = findViewById<Button>(R.id.insert_button)               //追加用 Button
        val update_button = findViewById<Button>(R.id.update_button)               //更新用 Button
        val delete_button = findViewById<Button>(R.id.delete_button)               //削除用 Button
        val list_view = findViewById<ListView>(R.id.list_view)                     //表示用 ListView

        mUserDao = UserDatabase.getInstance(this).userDao()

        mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListOf())
        list_view.adapter = mAdapter

        getUser()

        insert_button.setOnClickListener { insertUser() }
        update_button.setOnClickListener { updateUser() }
        delete_button.setOnClickListener { deleteUser() }
    }

    private fun getUser() {
        mUserList = mUserDao.getAll()
        val userInfoList = mUserList.map { it.id.toString() + " / " + it.name + " / " + it.age.toString() }
        mAdapter.clear()
        mAdapter.addAll(userInfoList)
    }

    private fun insertUser() {
        val newUser = User(0, "新規さん", 20)
        mUserDao.insert(newUser)
        getUser()
    }

    private fun updateUser() {
        if (mUserList.isEmpty()) return

        val editUser = mUserList.first()
        editUser.name = "更新されました"
        mUserDao.update(editUser)
        getUser()
    }

    private fun deleteUser() {
        if (mUserList.isEmpty()) return

        val deleteUser = mUserList.first()
        mUserDao.delete(deleteUser)
        getUser()
    }
}