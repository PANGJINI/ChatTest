package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink)


        // 현재 사용자의 인증 상태를 확인
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return //로그인되어 있지 않으면 이후의 코드 실행하지 않음
        }

        //뷰페이저에 어댑터 연결하기
        binding.viewpager.adapter = ViewPagerAdapter(this)

        var tabIconList = listOf(
            R.drawable.profile_icon_gray,
            R.drawable.chat_icon_gray,
            R.drawable.balance_icon_gray,
            R.drawable.icon_pw
        )

        //탭과 뷰페이저 연결하기
        var tabTextList = listOf("사용자", "채팅방", "밸런스게임", "내 정보")
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = tabTextList[position]
            tab.setIcon(tabIconList[position])
        }.attach()

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 3) { // "내 정보" 탭을 선택한 경우
                    binding.logoImage.visibility = View.GONE
                    binding.profileText.visibility = View.VISIBLE
                    val pinkColor = ContextCompat.getColor(this@MainActivity, R.color.pink)
                    binding.layout.setBackgroundColor(pinkColor)
                    val whiteLogout = ContextCompat.getDrawable(this@MainActivity, R.drawable.logout_white)
                    binding.btnLogout.background = whiteLogout
                } else {
                    // 다른 탭을 선택한 경우
                    binding.logoImage.visibility = View.VISIBLE
                    binding.profileText.visibility = View.GONE
                    val whiteColor = ContextCompat.getColor(this@MainActivity, R.color.white)
                    binding.layout.setBackgroundColor(whiteColor)
                    val pinkLogout = ContextCompat.getDrawable(this@MainActivity, R.drawable.logout_5)
                    binding.btnLogout.background = pinkLogout
                }
            }
        })

        val switchToBalanceFragment = intent.getBooleanExtra("switch_to_balance_fragment", false)
        if (switchToBalanceFragment) {
            switchToBalanceFragment()
        }
        var switchToChatListFragment = intent.getBooleanExtra("switch_to_chatlist_fragment", false)
        if (switchToChatListFragment) {
            switchToChatListFragment()
        }


        //버튼 누르면 로그아웃하기
        binding.btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃")
            builder.setMessage("정말 로그아웃하시겠습니까?")

            builder.setPositiveButton("로그아웃") { dialog, which ->
                mAuth.signOut() // 로그아웃
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

    }//onCreate 끝

    class ViewPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
        private lateinit var viewPagerAdapter: ViewPagerAdapter
        val fragments = listOf<Fragment>(FragmentUserList(), FragmentChatRoomList(), FragmentBalance(), FragmentMyPage())

        //프래그먼트 페이지 수 반환
        override fun getItemCount(): Int = fragments.size

        //프래그먼트 객체 얻기
        override fun createFragment(position: Int): Fragment = fragments[position]

    }

    fun switchToBalanceFragment() {
        viewPager.currentItem = 2   //2번 인덱스 = 밸런스게임 프래그먼트
    }
    fun switchToChatListFragment() {
        viewPager.currentItem = 1   //2번 인덱스 = 밸런스게임 프래그먼트
    }

}