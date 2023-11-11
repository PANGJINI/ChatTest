package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.databinding.ActivityChatBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class ChatActivity : AppCompatActivity() {
    companion object {
        val ADD_ACTIVITY_REQUEST_CODE = 1
    }

    //뷰바인딩
    lateinit var binding: ActivityChatBinding
    //채팅 내용 설정을 위한 변수들
    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    //받는 사람의 채팅룸 값 = 보낸사람uid + 받는사람uid
    private lateinit var receiverRoom: String
    //보낸 사람의 채팅룸 값 = 받는사람uid + 보낸사람uid
    private lateinit var senderRoom: String
    private lateinit var messageList: ArrayList<Message>
    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        //뒤로가기 버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //액션바 색상 설정
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFF7CAC9")))


        //뷰페이저에 ViewPagerAdapter 연결하기
        binding.viewpager.adapter = ViewPagerAdapter(this)

        //탭과 뷰페이저 연결하기
        //var tabTextList = listOf("주접&플러팅", "밈", "특수문자", "텍대", "이모지", "밸런스게임", "논쟁")
        var tabTextList = listOf("My", "주접&플러팅", "밈", "특수문자", "이모지", "텍대")
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = tabTextList[position]
        }.attach()

        //채팅 리사이클러뷰에 어댑터 연결하기
        messageList = ArrayList()
        val messageAdapter: MessageAdapter = MessageAdapter(this, messageList)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter


        //유저리스트에서 넘어온 상대방의 name, uId 데이터를 receiver-- 변수에 담기
        receiverName = intent.getStringExtra("receiverName").toString()
        receiverUid = intent.getStringExtra("receiverId").toString()
        //액션바에 상대방 이름을 보여주기
        supportActionBar?.title = receiverName


        //접속자 uid, 접속자 이름, 채팅 전송 시간
        val senderUid = mAuth.currentUser?.uid
        var senderName = ""
        if (senderUid != null) {
            mDbRef.child("user").child(senderUid).child("name")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        senderName = snapshot.getValue(String::class.java).toString()
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
        val time = System.currentTimeMillis()
        val currentTime = SimpleDateFormat("HH:mm").format(Date(time)).toString()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid


        //메세지 전송 이벤트
        binding.sendBtn.setOnClickListener{
            val message = binding.messageEdit.text.toString()
            val messageObject = Message(message, senderUid, receiverUid, senderName, receiverName, currentTime)

            if(message != "") {
                //디비에 메시지 데이터 저장
                mDbRef.child("chats").child(senderRoom).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom).child("messages").push()
                            .setValue(messageObject)
                    }
                binding.messageEdit.setText("")

                //메세지를 보냈을 때 간편채팅 화면이라면, 일반채팅 화면으로 전환해줌
                val btnText: String = binding.fabSimpleChat.getText().toString()
                if(btnText == "일반채팅") { binding.frameChat.visibility = VISIBLE
                    binding.frameSimpleChat.visibility = GONE
                    binding.fabAdd.visibility = GONE
                    binding.fabSimpleChat.setText("간편채팅")
                }
            }


        }

        //채팅 리사이클러뷰에 메시지 내용을 추가
        mDbRef.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        //항목 추가 플로팅버튼 누르면 DataAddActivity로 이동
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, DataAddActivity::class.java)
            startActivityForResult(intent, ADD_ACTIVITY_REQUEST_CODE)
        }

        //간편채팅/일반채팅 플로팅버튼
        binding.fabSimpleChat.setOnClickListener {
            val btnText: String = binding.fabSimpleChat.getText().toString()

            when (btnText) {
                "간편채팅" -> { binding.frameChat.visibility = GONE
                    binding.frameSimpleChat.visibility = VISIBLE
                    binding.fabSimpleChat.setText("일반채팅")
                    binding.fabAdd.visibility = VISIBLE
                }
                "일반채팅" -> { binding.frameChat.visibility = VISIBLE
                    binding.frameSimpleChat.visibility = GONE
                    binding.fabSimpleChat.setText("간편채팅")
                    binding.fabAdd.visibility = GONE
                }
            }

        }
    } //oncreate 끝


    //간편채팅 프래그먼트들을 연결해주는 뷰페이저 어댑터
    class ViewPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
        private lateinit var viewPagerAdapter: ViewPagerAdapter
        val fragments = listOf<Fragment>(FragmentMyData(), FragmentFlirting(), FragmentMeme(), FragmentSpecialChar(),FragmentEmoji(), FragmentTextReplace())

        //프래그먼트 페이지 수 반환
        override fun getItemCount(): Int = fragments.size

        //프래그먼트 객체 얻기
        override fun createFragment(position: Int): Fragment = fragments[position]

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val fragment = FragmentChatRoomList()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.chat_recyclerView, fragment)
                    .commit()
                return true}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val chatRoomListFragment = FragmentChatRoomList()
        supportFragmentManager.beginTransaction().replace(R.id.chat_recyclerView, chatRoomListFragment).commit()
    }


    //채팅방에서 진입 시 채팅방으로 나오는 코드
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.chat_recyclerView)
//
//        if (currentFragment is FragmentChatRoomList) {
//            val chatRoomListFragment = FragmentChatRoomList()
//            supportFragmentManager.beginTransaction().replace(R.id.chat_recyclerView, chatRoomListFragment).commit()
//
//
//        } else {
//            // 그 외의 경우에는 기본 뒤로 가기 동작을 수행
//            super.onBackPressed()
//        }
    }


