package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivitySimpleChatBinding
import com.example.myapplication.databinding.FragmentGreetingBinding
import com.google.android.material.tabs.TabLayoutMediator

class SimpleChatActivity : AppCompatActivity() {

    lateinit var binding: ActivitySimpleChatBinding
    //lateinit var fragmentGreeting: FragmentGreeting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //fragmentGreeting = FragmentGreeting()

        //뒤로가기 버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //뷰페이저에 ViewPagerAdapter 연결하기
        binding.viewpager.adapter = ViewPagerAdapter(this)

        //탭과 뷰페이저 연결하기
        var tabTextList = listOf("인사", "취미", "농담하기", "플러팅하기")
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = tabTextList[position]
        }.attach()

        //extended fab 클릭시 ChatActivity로 이동
        binding.extendedFab.setOnClickListener {
            val intent = Intent(this@SimpleChatActivity, ChatActivity::class.java)
            startActivity(intent)
        }
    }
}