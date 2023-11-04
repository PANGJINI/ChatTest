package com.example.myapplication
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.jar.Manifest

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    private lateinit var mDbRef: DatabaseReference
    private var imageUri: Uri? = null


    //갤러리에서 받아온 이미지로 이미지뷰 설정하기
    private val getContent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
        try{
            //이미지 로딩
            var inputStream = contentResolver.openInputStream(it.data!!.data!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream!!.close()
            //inputStream = null
            bitmap?.let { binding.userImageView.setImageBitmap(bitmap) } ?: let{
                Log.e("프로필", "프로필 이미지 삽입되었음")
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    //        result: ActivityResult ->
//        if (result.resultCode == RESULT_OK) {
//            imageUri = result.data?.data    //이미지 경로 원본
//            binding.userImageView.setImageURI(imageUri)   //이미지 뷰를 바꿈
//            Log.d("image","프로필 바꾸기 성공")
//        } else{
//            Log.d("image", "프로필 바꾸기 실패")
//        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //액션바 설정
        supportActionBar?.title = "회원가입"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFF7CAC9")))

        mAuth = Firebase.auth   //인증 초기화
        mDbRef = Firebase.database.reference    //DB초기화
        storage = FirebaseStorage.getInstance()

        //지역 정보 설정하는 스피너 설정
        var area: String = ""
        val areaList = arrayOf("서울", "인천", "경기", "강원", "충청", "전라", "경상", "제주")
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, areaList)
        binding.spinnerArea.adapter = adapter

        //스피너에서 선택된 값을 area 변수에 저장하기
        binding.spinnerArea.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent:AdapterView<*>?, view:View?, position: Int, id: Long) {
                area = areaList[position].toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        // 프로필 이미지뷰 클릭이벤트
        binding.userImageView.setOnClickListener{
            //갤러리 접근권한 설정
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            var profileCheck = false

            //갤러리 열기
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type="image/*"
            getContent.launch(intent)

            //val intentImage = Intent(Intent.ACTION_PICK)
            //intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            //getContent.launch(intentImage)
            //profileCheck = true
        }


        // 회원가입 버튼 이벤트
        binding.btnSignUp.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val name = binding.editName.text.toString()
            val age = binding.editAge.text.toString()
            val intro = binding.editIntroduction.text.toString()

//            // 이미지 파일 경로
//            val imagePath = "users/${mAuth.currentUser?.uid}/profile.jpg"
//            val imageRef = mDbRef.child(imagePath)

            val genderId = binding.editGender.checkedRadioButtonId
            // 선택된 라디오 버튼의 ID로 라디오 버튼 찾기
            val genderS = findViewById<RadioButton>(genderId)
            // 선택된 라디오 버튼의 텍스트 읽어오기
            val gender = genderS.text.toString()

            val mbti: MutableList<String> = mutableListOf()
            if (binding.mbti1.isChecked) {
                mbti.add(binding.mbti1.text.toString())
            }
            if (binding.mbti2.isChecked) {
                mbti.add(binding.mbti2.text.toString())
            }
            if (binding.mbti3.isChecked) {
                mbti.add(binding.mbti3.text.toString())
            }
            if (binding.mbti4.isChecked) {
                mbti.add(binding.mbti4.text.toString())
            }
            if (binding.mbti5.isChecked) {
                mbti.add(binding.mbti5.text.toString())
            }
            if (binding.mbti6.isChecked) {
                mbti.add(binding.mbti6.text.toString())
            }
            if (binding.mbti7.isChecked) {
                mbti.add(binding.mbti7.text.toString())
            }
            if (binding.mbti8.isChecked) {
                mbti.add(binding.mbti8.text.toString())
            }
            if (binding.mbti9.isChecked) {
                mbti.add(binding.mbti9.text.toString())
            }
            if (binding.mbti10.isChecked) {
                mbti.add(binding.mbti10.text.toString())
            }
            if (binding.mbti11.isChecked) {
                mbti.add(binding.mbti11.text.toString())
            }
            if (binding.mbti12.isChecked) {
                mbti.add(binding.mbti12.text.toString())
            }
            if (binding.mbti13.isChecked) {
                mbti.add(binding.mbti13.text.toString())
            }
            if (binding.mbti14.isChecked) {
                mbti.add(binding.mbti14.text.toString())
            }
            if (binding.mbti15.isChecked) {
                mbti.add(binding.mbti15.text.toString())
            }
            if (binding.mbti16.isChecked) {
                mbti.add(binding.mbti16.text.toString())
            }


            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {     // 회원가입 성공시
                        val user = Firebase.auth.currentUser
                        val userId = user?.uid
                        val intent: Intent = Intent(this@SignUpActivity, LoginActivity::class.java)

                        //storage에 이미지 업로드
                        Upload(userId!!)
                        //firebase에 사용자 정보 업로드
                        addUserDatabase(
                            email,
                            mAuth.currentUser?.uid!!,
                            name,
                            age,
                            gender,
                            area,
                            mbti,
                            intro
                        )
                        //로그인 화면으로 전환하고 회원가입 성공 메세지 띄우기
                        startActivity(intent)
                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()

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


    fun Upload(userId: String) {
        //var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imgFileName = "IMAGE_${userId}_.png"
        var storageRef = storage?.reference?.child("images")?.child(imgFileName)

        storageRef?.putFile(imageUri!!)?.addOnSuccessListener {
            Log.d("하아", "스토리지에 이미지 업로드 성공")
        }
    }

    //addUserDatabase : 데이터베이스에 사용자 저장하는 함수
    private fun addUserDatabase(
        email: String,
        uId: String,
        name: String,
        age: String,
        gender: String,
        area: String,
        mbti: MutableList<String>,
        introduction: String
    ) {
        mDbRef.child("user").child(uId).setValue(
            User(
                email, uId, name, age, gender, area,
                mbti.toString(), introduction
            )
        )
    }
}
