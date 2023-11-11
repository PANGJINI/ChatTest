package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.disklrucache.DiskLruCache.Value
import com.example.myapplication.databinding.FragmentChatRoomListBinding
import com.google.android.play.integrity.internal.m
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


/*
 * 대화중인 채팅방 보여주는 프래그먼트
 */
class FragmentChatRoomList : Fragment() {

    lateinit var binding: FragmentChatRoomListBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var chatRoomList: ArrayList<Message>   //현재 유저가 포함된 채팅방 목록
    lateinit var chatRoomList2: ArrayList<Message>
    var onChatUser: ArrayList<String> = ArrayList()
    lateinit var adapter: ChatRoomAdapter
    lateinit var senderRoomList: ArrayList<Message>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatRoomListBinding.inflate(inflater)
        mAuth = Firebase.auth
        mDbRef = Firebase.database.reference

        chatRoomList = ArrayList()
        chatRoomList2 = ArrayList()
        senderRoomList = ArrayList()
        adapter = ChatRoomAdapter(context, chatRoomList2)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.chatRecyclerView.adapter = adapter

        //현재 접속자의 uid
        val currentUid = mAuth.currentUser?.uid

        mDbRef.child("chats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatRoomList2.clear()
                onChatUser.clear()
                for(postSnapshot in snapshot.children) {
                    var senderRoom = postSnapshot.key

                    //senderRoom의 값이 현재 접속자의 uid로 끝나는 레퍼런스의 message 레퍼런스에 접근한다
                    if(senderRoom != null && senderRoom.endsWith(currentUid!!)) {
                        mDbRef.child("chats").child(senderRoom).child("messages")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    senderRoomList.clear()

                                    for (postSnapshot in snapshot.children) {
                                        var data = postSnapshot.getValue(Message::class.java)
                                        senderRoomList.add(data!!)
                                    }
                                    // senderRoomList를 time을 기준으로 내림차순 정렬
                                    var sortedList = senderRoomList.sortedByDescending { it.time }
                                    // chatRoomList에 정렬된 데이터 추가
                                    chatRoomList.clear()
                                    chatRoomList.addAll(sortedList)


                                    for (message in chatRoomList) {
                                        if (message.sendId == currentUid) {
                                            if (!onChatUser.contains(message.receiveName)) {
                                                onChatUser.add(message.receiveName!!)
                                                chatRoomList2.add(message)
                                            }
                                        } else {
                                            if (!onChatUser.contains(message.sendName)) {
                                                onChatUser.add(message.sendName!!)
                                                chatRoomList2.add(message)
                                            }
                                        }
                                    }

                                    adapter.notifyDataSetChanged()
                                    Log.e("채팅", "어댑터 갱신 후: ${chatRoomList2.map { it.message }}")
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // 오류 처리
                                }
                            })

                    }//if

                }
                //adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


//        //현재 접속자의 senderRoom 레퍼런스에 속한 messages를 chatRoomList에 가져온다
//        mDbRef.child("chats").addChildEventListener(object : ChildEventListener{
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                //chats 레퍼런스의 자식 레퍼런스들을 senderRoom에 하나씩 가져온다
//                var senderRoom = snapshot.key
//
//
//            }//onChildAdded
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//
//            }
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//            }
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//            }
//            override fun onCancelled(error: DatabaseError) {
//            }
//
//        }) //addChildEventListener 끝


        return binding.root
    }


}