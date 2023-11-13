package com.example.myapplication

import android.content.Context
import android.content.Intent
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.NonDisposableHandle.parent

class ChatRoomAdapter(private val context: Context?, private val chatRoomList2: ArrayList<Message>):
    RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {

    lateinit var binding: ItemChatroomlistBinding
    lateinit var _binding: FragmentChatRoomListBinding
    var mAuth = FirebaseAuth.getInstance()
    var mDbRef = FirebaseDatabase.getInstance().reference
    val currentUser = mAuth.currentUser?.uid
    var storage = FirebaseStorage.getInstance()

    //데이터 개수 가져오기
    override fun getItemCount(): Int {
        return chatRoomList2.size
    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemChatroomlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        _binding = FragmentChatRoomListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentRoom = chatRoomList2[position]

        var gender: String

        if (currentUser == currentRoom.sendId) {
            holder.userName.text = currentRoom.receiveName
            holder.lastMessage.text = currentRoom.message
            var imgRef = storage.reference.child("images/IMAGE_${currentRoom.receiveId}_.png")
            imgRef.downloadUrl.addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    if (context != null) {
                        Glide.with(context).load(task.result).into(holder.circleView)
                    }
                    mDbRef.child("user").child(currentRoom.receiveId.toString()).child("gender")
                        .addValueEventListener(object :ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                gender = snapshot.getValue(String::class.java).toString()
                                if(gender == "남성") {
                                    holder.circleView.borderColor = holder.blueColor
                                } else {
                                    holder.circleView.borderColor = holder.pinkColor
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {    }
                        })

                }
            }
        } else {
            holder.userName.text = currentRoom.sendName
            holder.lastMessage.text = currentRoom.message
            var imgRef = storage.reference.child("images/IMAGE_${currentRoom.sendId}_.png")
            imgRef.downloadUrl.addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    if (context != null) {
                        Glide.with(context).load(task.result).into(holder.circleView)
                    }
                    mDbRef.child("user").child(currentRoom.sendId.toString()).child("gender")
                        .addValueEventListener(object :ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                gender = snapshot.getValue(String::class.java).toString()
                                if(gender == "남성") {
                                    holder.circleView.borderColor = holder.blueColor
                                } else {
                                    holder.circleView.borderColor = holder.pinkColor
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {    }
                        })
                }
            }
        }




        //채팅방 목록 선택 시 채팅 액티비티로 연결
        holder.itemView.setOnClickListener{
            val intent = Intent(context, ChatActivity::class.java)
            if(currentUser == currentRoom.sendId) {
                intent.putExtra("receiverName", currentRoom.receiveName)
                intent.putExtra("receiverId", currentRoom.receiveId)
            } else {
                intent.putExtra("receiverName", currentRoom.sendName)
                intent.putExtra("receiverId", currentRoom.sendId)
            }
            context!!.startActivity(intent)
        }
    }//BindViewHolder 끝



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val circleView: CircleImageView = itemView.findViewById(R.id.circleView)
        val lastMessage: TextView = itemView.findViewById(R.id.user_last_message)
        val blueColor = ContextCompat.getColor(itemView.context, R.color.blue)
        val pinkColor = ContextCompat.getColor(itemView.context, R.color.pink)


    }
}