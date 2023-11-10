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
import android.widget.Toast
import com.example.myapplication.databinding.ActivityDataAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataAddActivity : AppCompatActivity() {

    lateinit var binding: ActivityDataAddBinding
    lateinit var category: String
    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        val currentUser = mAuth.currentUser?.uid

        //액션바 설정
        supportActionBar?.title = "간편채팅 항목 추가하기"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFF7CAC9")))

        //카테고리 선택하는 스피너 설정
        category = ""
        val categoryList = arrayOf("My", "주접&플러팅", "밈", "특수문자","이모지", "텍대")
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


        //저장하기 버튼 눌렀을 때 카테고리 값에 따라 알맞은 DB에 저장
        binding.btnDataAdd.setOnClickListener {
            var addData = binding.editAdd.text.toString()
            if(addData == "") {
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            } else {
                val categoryRef = when(category) {
                    "My" -> mDbRef.child("simpleChat").child("myData").child(currentUser!!)
                    "주접&플러팅" -> mDbRef.child("simpleChat").child("flirt")
                    "밈" -> mDbRef.child("simpleChat").child("meme")
                    "특수문자" -> mDbRef.child("simpleChat").child("specialChar")
                    "텍대" -> mDbRef.child("simpleChat").child("textReplace")
                    "이모지" -> mDbRef.child("simpleChat").child("emoji")
                    else -> mDbRef.child("simpleChat")
                }

                categoryRef.orderByChild("chatData").equalTo(addData).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {    // 데이터가 이미 존재하는 경우
                            Toast.makeText(this@DataAddActivity, "이미 존재하는 데이터입니다.", Toast.LENGTH_SHORT).show()
                        } else {                    // 데이터가 존재하지 않으면 저장
                            categoryRef.push().setValue(SimpleChatDataModel(addData))
                            binding.editAdd.setText("")
                            Toast.makeText(this@DataAddActivity, "저장에 성공했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@DataAddActivity, "데이터 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }//onClickListener 끝

    }

//    //'저장' 메뉴가 선택됐을 때 다시 chat activity로 돌아감
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if(item.itemId == R.id.menu_add_save) {
//            if(binding.editAdd.text.toString() == "") {
//                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
//            } else {
//                val intent = intent
//                intent.putExtra("category", category)
//                intent.putExtra("chatData", binding.editAdd.text.toString())
//                setResult(Activity.RESULT_OK, intent)
//                finish()
//                return true
//            }
//        }
//        return true
//    }


}