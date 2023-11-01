package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentUserListBinding
import com.example.myapplication.databinding.UserListLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/*
 * 유저리스트 보여주는 프래그먼트
 */
class FragmentUserList : Fragment() {

    lateinit var binding: FragmentUserListBinding
    lateinit var _binding: UserListLayoutBinding
    lateinit var userAdapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var userList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserListBinding.inflate(inflater)
        _binding = UserListLayoutBinding.inflate(layoutInflater)
        mAuth = Firebase.auth   //인증 초기화
        mDbRef = Firebase.database.reference    //DB초기화
        userList = ArrayList()  //리스트 초기화
        userAdapter = UserAdapter(context, userList)

        binding.userRecycelrView.layoutManager = LinearLayoutManager(context)
        binding.userRecycelrView.adapter = userAdapter

        //사용자 정보 가져와서 유저리스트에 넣기
        mDbRef.child("user").addValueEventListener(object: ValueEventListener {
            //onDataChange  데이터 변경 시 실행
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children) {    //children 내에 있는 데이터만큼 반복
                    //유저 정보
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uId){
                        userList.add(currentUser!!)
                    }
                }
                //notifyDataSetChanged() 리사이클러뷰의 리스트를 업데이트 할 때 사용(리스트 크기, 아이템 모두 변경 가능)
                userAdapter.notifyDataSetChanged()
            }
            //onCancelled  오류 발생 시 실행
            override fun onCancelled(error: DatabaseError) {

            }
        })


        return binding.root
    }
}