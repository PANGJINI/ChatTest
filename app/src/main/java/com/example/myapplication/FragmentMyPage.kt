package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.FragmentMyPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class FragmentMyPage : Fragment() {

    lateinit var binding: FragmentMyPageBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        val currentUser = mAuth.currentUser?.uid

        //이미지뷰의 크기를 정사각형으로 출력하기 위한 부분
        //layout_width의 너비값를 가져와서 높이값으로 설정해준다
        val imageView = binding.myImage
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
        val imgRef = storage.reference.child("images/IMAGE_${currentUser}_.png")
        imgRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                //글라이드에서 이미지 가져와서 circleView에 설정하기
                if (this != null) {
                    Glide.with(this).load(task.result).into(binding.myImage)
                }
            }
        }

        //나머지 프로필 내용 받아오기
        mDbRef.child("user").child(currentUser!!).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val username = dataSnapshot.child("name").getValue(String::class.java)
                binding.myName.text = username
                val area = dataSnapshot.child("area").getValue(String::class.java)
                binding.myArea.text = "[ $area, "
                val age = dataSnapshot.child("age").getValue(String::class.java)
                binding.myAge.text = "${age}세 "
                val gender = dataSnapshot.child("gender").getValue(String::class.java)
                binding.myGender.text = "$gender ]"
                val intro = dataSnapshot.child("introduction").getValue(String::class.java)
                binding.myIntro.text = intro
            }
            override fun onCancelled(error: DatabaseError) { }
        })


        return binding.root
    }


}