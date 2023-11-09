package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        window.statusBarColor = ContextCompat.getColor(this, R.color.pink)

        // 회원가입 버튼 이벤트
        binding.btnSignUp.setOnClickListener {
            val intent: Intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        // 로그인 버튼 이벤트
        binding.btnLogin.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            if (email.isNullOrBlank() || password.isNullOrBlank()) {
                // 이메일 또는 비밀번호가 null 또는 빈 문자열인 경우
                Toast.makeText(this, "이메일 또는 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show()
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {     //로그인 성공시
                            val intent: Intent =
                                Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
                            finish()
                        } else {                    //로그인 실패시
                            Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
                            Log.d("login", "ERROR ::::: ${task.exception}")
                        }
                    }
            }
        }
    }
}