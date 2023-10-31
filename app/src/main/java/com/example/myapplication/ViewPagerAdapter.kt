package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    val fragments = listOf<Fragment>(FragmentFlirting(), FragmentMeme(), FragmentSpecialChar(), FragmentText())

    //프래그먼트 페이지 수 반환
    override fun getItemCount(): Int = fragments.size

    //프래그먼트 객체 얻기
    override fun createFragment(position: Int): Fragment = fragments[position]

}