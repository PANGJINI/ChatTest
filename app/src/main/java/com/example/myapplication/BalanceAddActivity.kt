package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.myapplication.databinding.ActivityBalanceAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class BalanceAddActivity : AppCompatActivity() {

    lateinit var binding: ActivityBalanceAddBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference
    lateinit var balanceGameList: ArrayList<BalanceGameDataModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBalanceAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        balanceGameList = ArrayList()
        val currentUser = mAuth.currentUser?.uid
        var postUserName = ""
        mDbRef.child("user").child(currentUser!!).child("name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postUserName = snapshot.getValue(String::class.java).toString()
                }
                override fun onCancelled(error: DatabaseError) { }
            })
        var postUserGender = ""
        mDbRef.child("user").child(currentUser!!).child("gender")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postUserGender = snapshot.getValue(String::class.java).toString()
                }
                override fun onCancelled(error: DatabaseError) { }
            })
        val time = System.currentTimeMillis()
        val currentTime = SimpleDateFormat("yyyy/MM/dd HH:mm").format(Date(time)).toString()



        //액션바 설정
        supportActionBar?.title = "밸런스 게임 항목 추가하기"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFF7CAC9")))


        binding.btnAdd.setOnClickListener{
            val bal1 = binding.editBalance1.text.toString()
            val bal2 = binding.editBalance2.text.toString()
            val balanceObject = BalanceGameDataModel(bal1, bal2, listOf(0, 0),currentUser, postUserName, postUserGender, currentTime)

            //db에 밸런스게임 추가하기
            mDbRef.child("BalanceGame").push()
                .setValue(balanceObject).addOnSuccessListener {
                    Toast.makeText(this, "밸런스게임이 생성되었습니다.", Toast.LENGTH_LONG).show()
                }

            //main 액티비티의 밸런스게임 탭으로 화면전환하기
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("switch_to_balance_fragment", true)
            startActivity(intent)
        }
    }//onCreate 끝


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //뒤로가기 버튼 눌렀을 때 main 액티비티의 밸겜 프래그먼트로 이동
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra("switch_to_balance_fragment", true)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}