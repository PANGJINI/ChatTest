package com.example.myapplication

import android.R
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityModifyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class ModifyActivity : AppCompatActivity() {
    lateinit var binding: ActivityModifyBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference
    lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        val currentUser = mAuth.currentUser?.uid

        //액션바 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   //뒤로가기버튼
        supportActionBar?.title = "내 정보 수정하기"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFF7CAC9")))

        var myArea: String = ""     //DB에서 가져온 지역 데이터
        var area: String = ""       //스피너에서 선택할 지역 데이터
        val areaList = arrayOf("서울", "인천", "경기", "강원", "충청", "전라", "경상", "제주")
        var adapter = ArrayAdapter(this, R.layout.simple_list_item_1, areaList)
        binding.spinnerArea.adapter = adapter

        //스피너에서 선택된 값을 area 변수에 저장하기
        binding.spinnerArea.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                area = areaList[position].toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {   }
        }

        //Firestorage에 있는 user 이미지 가져오기
        val imgRef = storage.reference.child("images/IMAGE_${currentUser}_.png")
        imgRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                //글라이드에서 이미지 가져와서 circleView에 설정하기
                if (this != null) {
                    Glide.with(this).load(task.result).into(binding.userImageView)
                }
            }
        }

        //기존 User DB에 있는 내용 가져와서 세팅하기
        mDbRef.child("user").child(currentUser!!).addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val email = snapshot.child("email").getValue(String::class.java).toString()
                binding.email.text = email
                val name = snapshot.child("name").getValue(String::class.java).toString()
                binding.editName.setText(name)
                val age = snapshot.child("age").getValue(String::class.java).toString()
                binding.editAge.setText(age)
                val intro = snapshot.child("introduction").getValue(String::class.java).toString()
                binding.editIntroduction.setText(intro)

                val gender = snapshot.child("gender").getValue(String::class.java).toString()
                if(gender == "남성") {
                    binding.editMale.isChecked = true
                } else {
                    binding.editFemale.isChecked = true
                }

                myArea = snapshot.child("area").getValue(String::class.java).toString()
                // myarea의 값이 areaList에 있는지 확인하고 해당 인덱스로 스피너 선택
                val index = areaList.indexOf(myArea)
                if (index != -1) {
                    binding.spinnerArea.setSelection(index)
                }

                val mbtiData = snapshot.child("mbti").getValue(String::class.java).toString()
                val startIndex = mbtiData.indexOf("[") + 1
                val endIndex = mbtiData.indexOf("]")
                // mbti만 추출
                val mbti = if (startIndex >= 0 && endIndex > startIndex) {
                    mbtiData.substring(startIndex, endIndex)
                } else {
                    ""
                }
                when (mbti) {
                    "ISTJ" -> binding.mbti1.isChecked = true
                    "ISFJ" -> binding.mbti2.isChecked = true
                    "INFJ" -> binding.mbti3.isChecked = true
                    "INTJ" -> binding.mbti4.isChecked = true
                    "ISTP" -> binding.mbti5.isChecked = true
                    "ISFP" -> binding.mbti6.isChecked = true
                    "INFP" -> binding.mbti7.isChecked = true
                    "INTP" -> binding.mbti8.isChecked = true
                    "ESTP" -> binding.mbti9.isChecked = true
                    "ESFP" -> binding.mbti10.isChecked = true
                    "ENFP" -> binding.mbti11.isChecked = true
                    "ENTP" -> binding.mbti12.isChecked = true
                    "ESTJ" -> binding.mbti13.isChecked = true
                    "ESFJ" -> binding.mbti14.isChecked = true
                    "ENFJ" -> binding.mbti15.isChecked = true
                    "ENTJ" -> binding.mbti16.isChecked = true
                }

                imageUri = snapshot.child("imageUrl").getValue(String::class.java)?.toUri()
            }
            override fun onCancelled(error: DatabaseError) {    }
        })


        // 프로필 이미지뷰 클릭하면 갤러리 연결
        binding.userImageView.setOnClickListener{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            var profileCheck = false

            //갤러리 열기
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type="image/*"
            getContent.launch(intent)
        }


        // 정보 수정 버튼 누르면 USER DB 업데이트
        binding.btnModify.setOnClickListener {
            val email = binding.email.text.toString()
            val name = binding.editName.text.toString()
            val age = binding.editAge.text.toString()
            val intro = binding.editIntroduction.text.toString()

            val genderId = binding.editGender.checkedRadioButtonId
            var gender = ""
            if(genderId == -1) {
                Toast.makeText(this, "정보 수정 실패 :: 성별을 선택하세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 선택된 라디오 버튼의 ID로 라디오 버튼 찾기
                val genderS = findViewById<RadioButton>(genderId)
                // 선택된 라디오 버튼의 텍스트 읽어오기
                gender = genderS.text.toString()
            }

            val mbti: ArrayList<String> = arrayListOf()
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

            if (binding.editName.text.isBlank() ||
                binding.editAge.text.isBlank() ||
                binding.editIntroduction.text.isBlank() ||
                genderId == -1
            ) {
                Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {    //모든 항목이 입력된 경우에 DB 업데이트
                val userObject = User(email, currentUser, name, age, gender, area, mbti.toString(), intro)
                Upload(currentUser, userObject)
            }

        }

    }//onCreate 끝


    //갤러리에서 받아온 이미지로 이미지뷰 설정하기
    private val getContent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
            result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data    //이미지 경로 원본
            binding.userImageView.setImageURI(imageUri)   //이미지 뷰를 바꿈
            Log.d("signUp","프로필 바꾸기 성공")
        } else{
            Log.d("signUp", "프로필 바꾸기 실패")
        }
    }//getContent 끝


    fun Upload(currentUser: String, userObject: User) {
        if (imageUri != null) {
            var imgFileName = "IMAGE_${currentUser}_.png"
            var storageRef = storage?.reference?.child("images")?.child(imgFileName)
            storageRef?.putFile(imageUri!!)?.addOnSuccessListener {
                Toast.makeText(this, "프로필 이미지가 반영되지 않으면 어플을 재실행해주세요😅", Toast.LENGTH_LONG).show()

                mDbRef.child("user").child(currentUser).setValue(userObject)

                //내 정보 화면으로 전환
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra("switch_to_mypage_fragment", true)
                startActivity(intent)
                finish()

            }
        } else {
            mDbRef.child("user").child(currentUser).setValue(userObject)
            Toast.makeText(this, "회원 정보를 수정했습니다.", Toast.LENGTH_SHORT).show()

            //내 정보 화면으로 전환
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("switch_to_mypage_fragment", true)
            startActivity(intent)
            finish()
        }



//        if(imageUri == null) {
//            Toast.makeText(this, "프로필 이미지를 등록하세요.", Toast.LENGTH_LONG).show()
//        } else {
//            //새로 들어온 파일로 기존 user 파일이 덮어짐
//            storageRef?.putFile(imageUri!!)?.addOnSuccessListener { task ->
//                storageRef.downloadUrl.addOnSuccessListener { uri ->
//                    //DB에 회원정보 올리기
//
//                }
//
//                //내 정보 화면으로 전환
//                val intent = Intent(this, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                intent.putExtra("switch_to_mypage_fragment", true)
//                startActivity(intent)
//                finish()
//            }?.addOnFailureListener { exception ->
//                Log.e("수정", "uri ========== $imageUri")
//                Log.e("signUp", "스토리지에 이미지 업로드 실패: $exception")
//                Toast.makeText(this, "정보 수정 실패 :: 이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }


    }//Upload 끝

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // 뒤로가기 버튼이 클릭되었을 때 main 액티비티의 두 번째 탭으로 이동
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra("switch_to_mypage_fragment", true)
                startActivity(intent)
                //finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}