package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth   //인증 초기화
        mDbRef = Firebase.database.reference    //DB초기화


        // 회원가입 버튼 이벤트
        binding.btnSignUp.setOnClickListener{
            val name = binding.editName.text.toString()
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()


            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {     // 회원가입 성공시
                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
                        Log.d("signUp", "email: $email, password: $password")

                        val intent: Intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        startActivity(intent)
                        addUserDatabase(name, email, mAuth.currentUser?.uid!!)

                    } else {                    // 회원가입 실패시
                        Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
                        Log.d("signUp", "ERROR ::::: ${task.exception}")
                    }
                }

        }
    }

    //addUserDatabase : 데이터베이스에 사용자 저장하는 함수 (이름, 이메일, uid(인증 데이터 정보))
    private fun addUserDatabase(name: String, email: String, uId: String) {
        mDbRef.child("user").child(uId).setValue(User(name, email, uId))
    }
}