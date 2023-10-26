package com.example.myapplication
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
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

    //private val interest: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth   //인증 초기화
        mDbRef = Firebase.database.reference    //DB초기화





        // 회원가입 버튼 이벤트
        binding.btnSignUp.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val name = binding.editName.text.toString()
            val nick = binding.editNick.text.toString()
            val age = binding.editAge.text.toString()
            val area = binding.editArea.text.toString()
            val number = binding.editNumber.text.toString()

//            // 이미지 파일 경로
//            val imagePath = "users/${mAuth.currentUser?.uid}/profile.jpg"
//            val imageRef = mDbRef.child(imagePath)
//



            val genderId = binding.editGender.checkedRadioButtonId
            // 선택된 라디오 버튼의 ID로 라디오 버튼 찾기
            val genderS = findViewById<RadioButton>(genderId)
            // 선택된 라디오 버튼의 텍스트 읽어오기
            val gender = genderS.text.toString()

            val interest: MutableList<String> = mutableListOf()

            if (binding.interest1.isChecked) {
                interest.add(binding.interest1.text.toString())
            }
            if (binding.interest2.isChecked) {
                interest.add(binding.interest2.text.toString())
            }
            if (binding.interest3.isChecked) {
                interest.add(binding.interest3.text.toString())
            }
            if (binding.interest4.isChecked) {
                interest.add(binding.interest4.text.toString())
            }
            if (binding.interest5.isChecked) {
                interest.add(binding.interest5.text.toString())
            }
            if (binding.interest6.isChecked) {
                interest.add(binding.interest6.text.toString())
            }
            if (binding.interest7.isChecked) {
                interest.add(binding.interest7.text.toString())
            }
            if (binding.interest8.isChecked) {
                interest.add(binding.interest8.text.toString())
            }



            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {     // 회원가입 성공시
                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()

                        Log.d("signUp", "email: $email, password: $password")

                        val intent: Intent = Intent(this@SignUpActivity, LoginActivity::class.java)

                        startActivity(intent)
                        addUserDatabase(
                            email,
                            mAuth.currentUser?.uid!!,
                            name,
                            nick,
                            age,
                            gender,
                            area,
                            number,
                            interest
                        )

                    } else {                    // 회원가입 실패시
                        Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
                        Log.d("signUp", "ERROR ::::: ${task.exception}")
                    }
                }
        }


    }

//    companion object {
//        private const val IMAGE_PICK_CODE = 1000
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
//            imageUri = data.data
//        }
//    }


    //addUserDatabase : 데이터베이스에 사용자 저장하는 함수
    private fun addUserDatabase(
        email: String,
        uId: String,
        name: String,
        nick: String,
        age: String,
        gender: String,
        area: String,
        number: String,
        interest: MutableList<String>
    ) {
        mDbRef.child("user").child(uId).setValue(
            User(
                email, uId, name, nick, age, gender, area, number,
                interest.toString()
            )
        )
    }
}