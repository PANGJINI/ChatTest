package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityChatBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    lateinit var binding: ActivityChatBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference

    //받는 사람의 채팅룸 값 = 보낸사람uid + 받는사람uid
    private lateinit var receiverRoom: String
    //보낸 사람의 채팅룸 값 = 받는사람uid + 보낸사람uid
    private lateinit var senderRoom: String

    private lateinit var messageList: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //뒤로가기 버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //뷰페이저에 ViewPagerAdapter 연결하기
        binding.viewpager.adapter = ViewPagerAdapter(this)

        //탭과 뷰페이저 연결하기
        var tabTextList = listOf("인사", "취미", "농담하기", "플러팅하기")
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = tabTextList[position]
        }.attach()

        //초기화
        messageList = ArrayList()
        val messageAdapter: MessageAdapter = MessageAdapter(this, messageList)

        //리사이클러뷰
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

        // 넘어온 상대방의 name, uId 데이터를 receiver-- 변수에 담기
        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uId").toString()
        //액션바에 상대방 이름을 보여주기
        supportActionBar?.title = receiverName

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        //접속자의 uId
        val senderUid = mAuth.currentUser?.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        //메세지 전송 이벤트
        binding.sendBtn.setOnClickListener{
            val message = binding.messageEdit.text.toString()
            val messageObject = Message(message, senderUid)

            //메시지 데이터 저장
            mDbRef.child("chats").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom).child("messages").push()
                        .setValue(messageObject)
                }
            binding.messageEdit.setText("")
        }

        //데이터 추가 리스너
        mDbRef.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshat in snapshot.children) {
                        val message = postSnapshat.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        //extended fab 클릭시 간편채팅, 일반채팅으로 화면전환
        binding.extendedFab.setOnClickListener {
            val btnText: String = binding.extendedFab.getText().toString()

            when (btnText) {
                "간편채팅" -> { binding.frameChat.visibility = GONE
                    binding.frameSimpleChat.visibility = VISIBLE
                    binding.extendedFab.setText("일반채팅")
                }
                "일반채팅" -> { binding.frameChat.visibility = VISIBLE
                    binding.frameSimpleChat.visibility = GONE
                    binding.extendedFab.setText("간편채팅")
                }
            }

        }
    } //oncreate 끝
}