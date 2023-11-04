package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentChatListBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


/*
 * 대화중인 채팅방 보여주는 프래그먼트
 */
class FragmentChatList : Fragment() {

    lateinit var binding: FragmentChatListBinding
    lateinit var mDbRef: DatabaseReference
    //lateinit var chatRoomAdapter: ChatRoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatListBinding.inflate(inflater)
        mDbRef = Firebase.database.reference

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(context)
        //binding.chatRecyclerView.adapter = chatRoomAdapter

        return binding.root
    }


}