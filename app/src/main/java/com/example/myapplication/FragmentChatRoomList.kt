package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
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
    var onChatUser: ArrayList<String> = ArrayList()
    lateinit var adapter: ChatRoomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatRoomListBinding.inflate(inflater)
        mAuth = Firebase.auth
        mDbRef = Firebase.database.reference
        chatRoomList = ArrayList()
        adapter = ChatRoomAdapter(context, chatRoomList)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.chatRecyclerView.adapter = adapter

        //현재 접속자의 uid
        val currentUid = mAuth.currentUser?.uid

        //현재 접속자의 senderRoom 레퍼런스에 속한 messages를 chatRoomList에 가져온다
        mDbRef.child("chats").addChildEventListener(object :ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //chats 레퍼런스의 자식 레퍼런스들을 senderRoom에 하나씩 가져온다
                val senderRoom = snapshot.key
                chatRoomList.clear()
                //senderRoom의 값이 현재 접속자의 uid로 끝나는 레퍼런스의 message 레퍼런스에 접근한다
                if(senderRoom != null && senderRoom.endsWith(currentUid!!)) {
                    mDbRef.child("chats").child(senderRoom).child("messages")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                Log.e("채팅", "senderRoom : $senderRoom")
                                //chatRoomList.clear()
                                for (postSnapshot in snapshot.children) {
                                    val data = postSnapshot.getValue(Message::class.java)

                                    if(data?.sendId == currentUid) {
                                        if(!onChatUser.contains(data.receiveName)) {
                                            onChatUser.add(data.receiveName!!)
                                            chatRoomList.add(data)
                                            Log.e("채팅", "추가된 내용 : ${data.message}")
                                        }
                                    } else {
                                        if(!onChatUser.contains(data?.sendName)) {
                                            onChatUser.add(data?.sendName!!)
                                            chatRoomList.add(data)
                                            Log.e("채팅", "추가된 내용 : ${data.message}")
                                        }
                                    }


                                }
                                Log.e("채팅", "프래그먼트에서 chatRoomList ===== ${chatRoomList}")
                                adapter.notifyDataSetChanged()

                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }

        }) //addChildEventListener 끝


        return binding.root
    }


}