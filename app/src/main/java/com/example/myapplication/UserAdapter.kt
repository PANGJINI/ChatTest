package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val context: Context?, private val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).
            inflate(R.layout.item_userlist, parent, false)
        return UserViewHolder(view)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]        //currentUser에 데이터 담기
        holder.userName.text = currentUser.name     //리사이클러뷰에 사용자 이름 보여줌
        holder.userMbti.text = currentUser.mbti     //mbti 보여줌
        holder.userIntroduction.text = currentUser.introduction     //자기소개 보여줌

        //스토리지에서 이미지 받아오기
        var storage = FirebaseStorage.getInstance()
        val imgRef = storage.reference.child("images/IMAGE_${currentUser.uId}_.png")
        imgRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                //글라이드에서 이미지 가져와서 circleView에 설정하기
                if (context != null) {
                    Glide.with(context).load(task.result).into(holder.circleView)
                }
            }
        }

        //사용자 성별이 남성이면 이미지뷰 색상을 파랑으로 바꿔줌
        holder.borderChange(currentUser)

        //유저리스트 클릭 이벤트
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            //현재 유저의 이름, uid값을 chatActivity로 넘겨줌
            intent.putExtra("receiverName", currentUser.name)   //현재 클릭한 유저 이름이 받는 이름이 됨
            intent.putExtra("receiverId", currentUser.uId)      //현재 클릭한 유저 uid가 받는사람 uid
            context!!.startActivity(intent)
        }
    }

    //데이터 개수 가져오기
    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val userMbti: TextView = itemView.findViewById(R.id.user_mbti)
        val userIntroduction: TextView = itemView.findViewById(R.id.user_introduction)
        val circleView: CircleImageView = itemView.findViewById(R.id.circleView)
        val blueColor = ContextCompat.getColor(itemView.context, R.color.blue)

        fun borderChange (user: User) {
            if (user.gender == "남성") circleView.borderColor = blueColor
        }

    }
}