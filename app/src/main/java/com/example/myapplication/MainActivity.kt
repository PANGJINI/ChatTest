package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var userAdapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var userList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth   //인증 초기화
        mDbRef = Firebase.database.reference    //DB초기화
        userList = ArrayList()  //리스트 초기화
        userAdapter = UserAdapter(this, userList)

        binding.userRecycelrView.layoutManager = LinearLayoutManager(this)
        binding.userRecycelrView.adapter = userAdapter


        //유저리스트 사용자 정보 가져오기
        mDbRef.child("user").addValueEventListener(object:ValueEventListener {
            //onDataChange  데이터 변경 시 실행
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children) {
                    //유저 정보
                    val currentUser = postSnapshot.getValue(User::class.java)
                    //Log.d("userlist", "currentUser: $currentUser")
                    if(mAuth.currentUser?.uid != currentUser?.uId){
                        userList.add(currentUser!!)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }
            //onCancelled  오류 발생 시 실행
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }   //onCreate 끝

    //생성할 메뉴를 지정하는 함수
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //메뉴 아이템 선택 기능 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            mAuth.signOut()     //로그아웃
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return true
    }

}