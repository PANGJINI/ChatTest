package com.example.myapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentChatRoomListBinding
import com.example.myapplication.databinding.ItemChatroomlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.NonDisposableHandle.parent

class ChatRoomAdapter(private val context: Context?, private val chatRoomList: ArrayList<Message>):
    RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {

    lateinit var binding: ItemChatroomlistBinding
    lateinit var _binding: FragmentChatRoomListBinding
    var mAuth = FirebaseAuth.getInstance()
    var currentUser = mAuth.currentUser?.uid
    var storage = FirebaseStorage.getInstance()
    var onChatUser: ArrayList<String> = ArrayList()

    //데이터 개수 가져오기
    override fun getItemCount(): Int {
        return chatRoomList.size
    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemChatroomlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        _binding = FragmentChatRoomListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e("아오", "$position 번째 대화중인상대 : $onChatUser")
        val userChat = chatRoomList[position]

        //보낸 사람이 현재 유저일 때
        if(userChat.sendId == currentUser) {
            binding.layout.visibility = View.VISIBLE
            //받은 사람 이름이 arrayList에 없을 때만 리사이클러뷰를 출력한다
            if(!onChatUser.contains(userChat.receiveName)) {
                holder.userName.text = userChat.receiveName
                var imgRef = storage.reference.child("images/IMAGE_${userChat.receiveId}_.png")
                imgRef.downloadUrl.addOnCompleteListener{ task ->
                    if(task.isSuccessful) {
                        if (context != null) {
                            Glide.with(context).load(task.result).into(holder.circleView)
                        }
                    }
                }
                onChatUser.add(userChat.receiveName!!)
            }else {
                //binding.layout.visibility = View.GONE
            }
        } else {    //보낸 사람이 현재 유저가 아닐 때
            //보낸 사람 이름이 arrayList에 없을 때만 리사이클러뷰를 출력한다
            if(!onChatUser.contains(userChat.sendName)) {
                holder.userName.text = userChat.sendName
                var imgRef = storage.reference.child("images/IMAGE_${userChat.sendId}_.png")
                imgRef.downloadUrl.addOnCompleteListener{ task ->
                    if(task.isSuccessful) {
                        if (context != null) {
                            Glide.with(context).load(task.result).into(holder.circleView)
                        }
                    }
                }
                onChatUser.add(userChat.sendName!!)
            } else {
                //binding.layout.visibility = View.GONE
            }
        }
    }//BindViewHolder 끝



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val circleView: CircleImageView = itemView.findViewById(R.id.circleView)
//        val blueColor = ContextCompat.getColor(itemView.context, R.color.blue)
//        //val pinkColor = ContextCompat.getColor(itemView.context, R.color.pink)
//
//        fun borderChange (user: User) {
//            if (user.gender == "남성") circleView.borderColor = blueColor
//        }

    }
}