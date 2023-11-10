package com.example.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityBalanceGameBinding
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.storage.FirebaseStorage

/*
 * 댓글을 작성 후 버튼을 누르면 리사이클러 뷰에 표시되도록
 */
class BalanceGameActivity : AppCompatActivity() {

    lateinit var binding: ActivityBalanceGameBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference
    lateinit var commentList: ArrayList<Comments>   //댓글을 담을 리스트
    lateinit var commentKeyList: ArrayList<String>
    lateinit var adapter: MyAdapter
    lateinit var gameRoom: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBalanceGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //액션바 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "밸런스게임"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFF7CAC9")))

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        val currentUser = mAuth.currentUser?.uid

        //밸런스게임 목록 리사이클러뷰에서 받아온 데이터
        gameRoom = intent.getStringExtra("gameRoomId").toString()
        val title = intent.getStringExtra("balTitle").toString()
        val bal1 = intent.getStringExtra("bal1").toString()
        val bal2 = intent.getStringExtra("bal2").toString()
        val postUserId = intent.getStringExtra("userid").toString()
        val postUserName = intent.getStringExtra("username").toString()
        val postUserGender = intent.getStringExtra("usergender").toString()
        val postTime = intent.getStringExtra("time").toString()

        //위에서 받아온 데이터를 밸런스게임 각 항목에 추가하기
        //이미지뷰, 게시자이름, 게시 시간, 게임 타이틀
        val storage = FirebaseStorage.getInstance()
        val imgRef = storage.reference.child("images/IMAGE_${postUserId}_.png")
        imgRef.downloadUrl.addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                val context = this
                Glide.with(context)
                    .load(task.result)
                    .into(binding.balanceImage)
            }
        }
        if (postUserGender == "남성") {
            binding.balanceImage.borderColor = getResources().getColor(R.color.blue)
        }
        binding.balanceName.text = postUserName
        binding.balanceTime.text = postTime
        binding.balanceTitle.text = title
        //투표 프레임
        binding.voteBtn1.text = bal1
        binding.voteBtn2.text = bal2
        //투표 결과 프레임
        binding.voteContent1.text = bal1
        binding.voteContent2.text = bal2


//        //이미 투표한 사람이라면 결과창을 보여주기
//        mDbRef.child("BalanceGame").child(gameRoom).child("voteUserList").addListenerForSingleValueEvent(object :ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val voteUserList: MutableList<String> =
//                    snapshot.getValue(object : GenericTypeIndicator<MutableList<String>>() {})
//                        ?: mutableListOf()
//                //이미 투표한 사람이면 결과창 보여주기
//                if (voteUserList.contains(currentUser!!)) {
//                    Toast.makeText(applicationContext, "이미 투표하셨습니다.", Toast.LENGTH_SHORT).show()
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {    }
//        })




        val buttonList = ArrayList<Button>()
        buttonList.add(binding.voteBtn1)
        buttonList.add(binding.voteBtn2)

        //밸런스게임 선택 버튼 클릭리스너
        val listener = View.OnClickListener { view ->
            val database = FirebaseDatabase.getInstance()
            val reference = database.getReference("BalanceGame").child(gameRoom)

            reference.child("voteUserList").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val voteUserList: MutableList<String> = snapshot.getValue(object : GenericTypeIndicator<MutableList<String>>() {}) ?: mutableListOf()
                    //이미 투표한 사람이면 결과창 보여주기
                    if (voteUserList.contains(currentUser!!)) {
                        Toast.makeText(applicationContext, "이미 투표하셨습니다.", Toast.LENGTH_SHORT).show()

                        return
                    } else {    //투표한 사람이 아닐 때만 DB에 추가하고 다이어로그 실행
                        val selectedButton = when(view.id){
                            R.id.voteBtn1 -> { binding.voteBtn1.text }
                            R.id.voteBtn2 -> { binding.voteBtn2.text }
                            else -> { }
                        }

                        //선택한 항목이 맞는지 확인하는 다이어로그
                        val alertDialogBuilder = AlertDialog.Builder(this@BalanceGameActivity)
                         .apply {
                            setTitle("❗선택 확인❗")
                            setMessage("'${selectedButton}'에 투표하시겠어요?")
                            setPositiveButton("네") { dialog, _ ->
                                voteUserList.add(currentUser!!)
                                reference.child("voteUserList").setValue(voteUserList)


                                //투표에 참여한 경우 투표 결과 프레임으로 전환
                                binding.frameVote.visibility = View.GONE
                                binding.frameVoteResult.visibility = View.VISIBLE

                                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val voteList = dataSnapshot.child("voteCountList").getValue(object : GenericTypeIndicator<List<Int>>() {})
                                        //전체 투표 수
                                        val voteCount = voteList?.sum()?.plus(1)
                                        binding.voteCount.text = "\uD83D\uDC64 : $voteCount"
                                        when (view.id) {
                                            //선택한 버튼이 Btn1이면 리스트의 [0]번째 인덱스 증가하고 DB 업데이트
                                            R.id.voteBtn1 -> {
                                                voteList?.let {
                                                    val updatedList = it.toMutableList()
                                                    updatedList[0] = updatedList[0] + 1
                                                    reference.child("voteCountList").setValue(updatedList)

                                                    //리스트 값을 투표수, 프로그레스 바에 출력
                                                    //현재 voteList는 갱신되지 않은 값을 가지고있기 때문에 +1을 해서 출력
                                                    binding.voteCount1.text = "\uD83D\uDDF3 ${voteList.get(0)+1}"
                                                    binding.voteProgress1.progress = voteList.get(0)+1
                                                    binding.voteProgress1.max = voteCount!!
                                                    binding.voteCount2.text = "\uD83D\uDDF3 ${voteList?.get(1) ?: 0}"
                                                    binding.voteProgress2.progress = voteList?.get(1) ?: 0
                                                    binding.voteProgress2.max = voteCount!!
                                                }
                                            }
                                            R.id.voteBtn2 -> {
                                                voteList?.let {
                                                    val updatedList = it.toMutableList()
                                                    updatedList[1] = updatedList[1] + 1
                                                    reference.child("voteCountList").setValue(updatedList)

                                                    binding.voteCount1.text = "\uD83D\uDDF3️ ${voteList?.get(0) ?: 0}"
                                                    binding.voteProgress1.progress = voteList?.get(0) ?: 0
                                                    binding.voteProgress1.max = voteCount!!
                                                    binding.voteCount2.text = "\uD83D\uDDF3️ ${voteList.get(1)+1}"
                                                    binding.voteProgress2.progress = voteList.get(1)+1
                                                    binding.voteProgress2.max = voteCount!!
                                                }
                                            }
                                        }
                                    }
                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // 에러 발생 시 동작
                                        Log.e("밸겜", "The read failed: " + databaseError.code)
                                    }
                                })//addListenerForSingleValueEvent 끝

                                dialog.dismiss()
                            }
                            setNegativeButton("아니오") { dialog, _ ->
                                dialog.dismiss()
                            }
                            setCancelable(false)
                        }
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {    }
            })
        }

        for (btn in buttonList) {
            btn.setOnClickListener(listener)
        }




        //댓글을 출력할 리스트, 어댑터
        commentList = ArrayList()
        commentKeyList=ArrayList()
        adapter = MyAdapter(this, commentList, currentUser, commentKeyList, gameRoom)
        binding.commentRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.commentRecyclerView.adapter = adapter


        var writerName = ""
        mDbRef.child("user").child(currentUser!!).child("name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    writerName = snapshot.getValue(String::class.java).toString()
                }
                override fun onCancelled(error: DatabaseError) { }
            })

        val time = System.currentTimeMillis()
        val currentTime = SimpleDateFormat("yyyy/MM/dd HH:mm").format(Date(time)).toString()


        //전송 버튼 클릭시 db에 댓글 내용 추가
        binding.btnSend.setOnClickListener {
            val content = binding.editComment.text.toString()
            val commentObject = Comments(content, currentUser, writerName, currentTime)

            if(content == "") {

            } else {
                //디비에 메시지 데이터 저장
                mDbRef.child("Comments").child(gameRoom).push()
                    .setValue(commentObject).addOnSuccessListener {
                    }
                binding.editComment.setText("")
            }

        }


        /*
         * 리사이블러뷰에 댓글 추가하기
         * commentKeyList()
         * 댓글 삭제시 현재 선택된 리사이클러뷰의 키가 필요하기 때문에 댓글을 추가할 때 키 값을 저장해둠
         */
        mDbRef.child("Comments").child(gameRoom).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentList.clear()
                    commentKeyList.clear()
                    for(postSnapshot in snapshot.children) {
                        val data = postSnapshot.getValue(Comments::class.java)
                        val uidKey: String = postSnapshot.key.toString()
                        commentList.add(data!!)
                        commentKeyList.add(uidKey)
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) { }
            })




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