package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

    //댓글 리사이클러뷰와 연결되는 MyAdapter
    class MyAdapter(
        private val context: Context,
        private val commentList: ArrayList<Comments>,
        private val currentUser: String?,
        private val commentKeyList: ArrayList<String>,
        private val gameRoom: String
    ): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val writerName: TextView = itemView.findViewById(R.id.commentName)
            val content: TextView = itemView.findViewById(R.id.commentContent)
            val time: TextView = itemView.findViewById(R.id.commentTime)
            val removeButton: ImageButton = itemView.findViewById(R.id.btnCommentRemove)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comments, parent, false)
            return MyViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return commentList.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val currentComment = commentList[position]
            holder.writerName.text = currentComment.writerName
            holder.content.text = currentComment.content
            holder.time.text = currentComment.writeTime

            //댓글 작성자 uid가 현재 접속자 uid와 같을 때만 댓글을 지울 수 있다
            if (currentComment.writerId == currentUser) {
                holder.removeButton.visibility = View.VISIBLE
                holder.removeButton.setOnClickListener {
                    //댓글을 정말 삭제할건지 alertDialog를 띄워준다
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("삭제 확인")
                    builder.setMessage("댓글을 삭제하시겠습니까?")
                    builder.setPositiveButton("예") { dialog, which ->
                        val selectedKey = commentKeyList[position]
                        val ref = FirebaseDatabase.getInstance().getReference("Comments").child(gameRoom)
                        ref.child(selectedKey).removeValue().addOnSuccessListener {
                            Log.d("삭제 성공", "데이터 삭제 완료")
                        }
                    }
                    builder.setNegativeButton("아니오") { dialog, which ->
                        dialog.dismiss()
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            } else {
                holder.removeButton.visibility = View.INVISIBLE
                holder.removeButton.setOnClickListener(null)
            }
        }

    }//어댑터 끝
