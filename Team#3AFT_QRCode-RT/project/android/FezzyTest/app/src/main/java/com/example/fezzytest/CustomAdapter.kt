package com.example.fezzytest

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/*interface LayOutClickListner {
    fun LayoutTapped(custom:Animal, position: Int)
}*/

class CustomAdapter(private val animalList: ArrayList<Animal>/*, private val Llistner: LayOutClickListner*/): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // Viewの初期化
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val image: ImageView
        val layout : LinearLayout
        //val scroll : ScrollView
        val home: TextView
        val translation: TextView


        init {
            //image = view.findViewById(R.id.image)
            layout = view.findViewById(R.id.home_before_layout)
            //scroll = view.findViewById(R.id.home_before_scroll)
            home = view.findViewById(R.id.home_before_voice)
            translation = view.findViewById(R.id.home_before_text)

        }
    }



    // レイアウトの設定
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item, viewGroup, false)
        val translation: TextView

        return ViewHolder(view)
    }

    // Viewの設定
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val animal = animalList[position]

        /*
        viewHolder.scroll.setOnClickListener {
            viewHolder.translation.requestFocus()

        }

         */

        //viewHolder.image.setImageResource(animal.imageId)
        viewHolder.home.text = animal.language
        //viewHolder.translation.text = animal.Transtext
        /*
        viewHolder.translation.setOnClickListener {
            viewHolder.translation.text = animal.Transtext
            viewHolder.translation.performClick()
        }



        viewHolder.layout.setOnClickListener {//レイアウトクリック時

            //viewHolder.translation.requestFocus()
            //Log.w("ここに", "入ってます！")
            viewHolder.translation.text = animal.Transtext
            viewHolder.translation.requestFocus()
        }

         */

    }

    // 表示数を返す
    override fun getItemCount() = animalList.size
}