package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.myapplication.databinding.ActivityDataAddBinding

class DataAddActivity : AppCompatActivity() {

    lateinit var binding: ActivityDataAddBinding
    lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //액션바 설정
        supportActionBar?.title = "간편채팅 항목 추가하기"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFF7CAC9")))

        //카테고리 선택하는 스피너 설정
        category = ""
        val categoryList = arrayOf("주접&플러팅", "밈", "특수문자","이모지", "텍대")
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryList)
        binding.spinnerCategory.adapter = adapter

        //스피너에서 선택된 값을 category 변수에 저장하기
        binding.spinnerCategory.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                category = categoryList[position].toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

    //액션바에 '저장'메뉴 추가
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //'저장' 메뉴가 선택됐을 때 다시 chat activity로 돌아감
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add_save) {
            val intent = intent
            intent.putExtra("category", category)
            intent.putExtra("chatData", binding.editAdd.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
            return true
        }
        return true
    }

}