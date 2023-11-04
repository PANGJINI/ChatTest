//package com.example.myapplication
//
//import android.content.Context
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.ktx.database
//import com.google.firebase.database.ktx.getValue
//import com.google.firebase.ktx.Firebase
//import de.hdodenhof.circleimageview.CircleImageView
//
//class ChatRoomAdapter(private val context: Context?, private val chatList: ArrayList<Message>):
//    RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {
//
//    var mDbRef: DatabaseReference
//    private val message = ArrayList<Message>()
//    private var uid: String? = null
//    private val destinationUsers: ArrayList<String> = arrayListOf()
//    private val chatUid = ArrayList<String>()
//
//    //자신이 포함된 채팅방의 uid를 가져오게 함
//    init {
//        mDbRef = Firebase.database.reference
//        uid = Firebase.auth.currentUser?.uid.toString()
//        Log.e("로그", "$uid")
//
//        mDbRef.child("chats").child("messages")
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (data in snapshot.children) {
//                        //message = data.getValue(Message::class.java)
//                        message.add(data.getValue<Message>()!! as Message)
//                        if(message!=null && uid == message.sendId)
//                        println(data)
//                    }
//                    notifyDataSetChanged()
//                }
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//
//            })
//    }
//
//
//    //화면 설정
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view: View = LayoutInflater.from(context).
//            inflate(R.layout.item_chatlist, parent, false)
//        return ViewHolder(view)
//    }
//
//    //데이터 설정
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        mDbRef = Firebase.database.reference
//        val chatsRef = Firebase.database.getReference("chats")
//
//        //현재 접속한 사용자의 uid
//        uid = Firebase.auth.currentUser?.uid.toString()
//
//        //chats 레퍼런스 아래 있는 채팅방 값들 가져오기
//        chatsRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (chatsSnapshot in dataSnapshot.children) {
//                    // chats 레퍼런스에 있는 각 데이터에 대한 작업을 수행
//                    val chatData = chatsSnapshot.getValue(YourChatDataClass::class.java)
//                    // 원하는 작업 수행
//                    if (chatData != null) {
//                        // chatData를 사용하여 필요한 작업 수행
//                    }
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // 에러가 발생한 경우 처리할 작업을 수행
//            }
//        })
//
//
//
//
//
//    }//BindViewHolder 끝
//
//    //데이터 개수 가져오기
//    override fun getItemCount(): Int {
//        return userList.size
//    }
//
//    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//        val userName: TextView = itemView.findViewById(R.id.user_name)
//        val lastMessage: TextView = itemView.findViewById(R.id.lastMessage)
//        val circleView: CircleImageView = itemView.findViewById(R.id.circleView)
//        val blueColor = ContextCompat.getColor(itemView.context, R.color.blue)
//        //val pinkColor = ContextCompat.getColor(itemView.context, R.color.pink)
//
//        fun borderChange (user: User) {
//            if (user.gender == "남성") circleView.borderColor = blueColor
//        }
//
//    }
//}