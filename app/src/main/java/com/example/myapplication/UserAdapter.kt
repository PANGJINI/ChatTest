package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(private val context: Context, private val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).
            inflate(R.layout.user_list_layout, parent, false)
        return UserViewHolder(view)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]        //currentUser에 데이터 담기
        holder.userName.text = currentUser.name     //화면에 사용자 목록 보여줌

        //유저리스트 클릭 이벤트
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            //넘길 데이터
            intent.putExtra("name", currentUser.name)
            intent.putExtra("uId", currentUser.uId)
            context.startActivity(intent)
        }
    }

    //데이터 개수 가져오기
    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
    }
}