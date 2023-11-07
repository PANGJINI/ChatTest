package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlin.jvm.internal.FunInterfaceConstructorReference

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    lateinit var mDbRef: DatabaseReference
    lateinit var receiveId: String
    lateinit var receiveName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDbRef = FirebaseDatabase.getInstance().reference

        //뒤로가기 버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //액션바 색상 설정
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFF7CAC9")))

        //유저리스트에서 넘어온 상대방의 데이터 받기
        receiveId = intent.getStringExtra("receiverId").toString()
        receiveName = intent.getStringExtra("receiverName").toString()

        //액션바에 상대방 이름을 보여주기
        supportActionBar?.title = "$receiveName💖 님의 프로필"

        //이미지뷰의 크기를 정사각형으로 출력하기 위한 부분
        //layout_width의 너비값를 가져와서 높이값으로 설정해준다
        val imageView = binding.profileImage
        val vto = imageView.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                imageView.viewTreeObserver.removeOnPreDrawListener(this)
                val width = imageView.measuredWidth
                imageView.layoutParams.height = width
                imageView.requestLayout()
                return true
            }
        })

        //프로필 이미지 받아오기
        var storage = FirebaseStorage.getInstance()
        val imgRef = storage.reference.child("images/IMAGE_${receiveId}_.png")
        imgRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                //글라이드에서 이미지 가져와서 circleView에 설정하기
                if (this != null) {
                    Glide.with(this).load(task.result).into(binding.profileImage)
                }
            }
        }

        //나머지 프로필 내용 받아오기
        mDbRef.child("user").child(receiveId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val username = dataSnapshot.child("name").getValue(String::class.java)
                binding.profileName.text = username

                val area = dataSnapshot.child("area").getValue(String::class.java)
                binding.profileArea.text = "[ $area, "
                val age = dataSnapshot.child("age").getValue(String::class.java)
                binding.profileAge.text = "${age}세 "
                val gender = dataSnapshot.child("gender").getValue(String::class.java)
                binding.profileGender.text = "$gender ]"
                val intro = dataSnapshot.child("introduction").getValue(String::class.java)
                binding.profileIntro.text = intro
            }
            override fun onCancelled(error: DatabaseError) { }
        })

        //채팅 시작 버튼
        binding.btnStartChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            //현재 유저의 이름, uid값을 chatActivity로 넘겨줌
            intent.putExtra("receiverName", receiveName)   //현재 클릭한 유저 이름이 받는 이름이 됨
            intent.putExtra("receiverId", receiveId)      //현재 클릭한 유저 uid가 받는사람 uid
            startActivity(intent)
        }
    }//onCreate 끝

}