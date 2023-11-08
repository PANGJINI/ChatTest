package com.example.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityCommentTestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import android.app.AlertDialog

/*
 * 댓글을 작성 후 버튼을 누르면 리사이클러 뷰에 표시되도록
 */
class CommentTestActivity : AppCompatActivity() {

    lateinit var binding: ActivityCommentTestBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference
    lateinit var commentList: ArrayList<Comments>   //댓글을 담을 리스트
    lateinit var keyList: ArrayList<String>
    lateinit var adapter: MyAdapter
    lateinit var gameRoom: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        val currentUser = mAuth.currentUser?.uid
        gameRoom = "1번밸겜"

        commentList = ArrayList()
        keyList=ArrayList()
        adapter = MyAdapter(this, commentList, currentUser, keyList, gameRoom)
        binding.balanceRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.balanceRecyclerView.adapter = adapter


        var writerName = ""
        mDbRef.child("user").child(currentUser!!).child("name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    writerName = snapshot.getValue(String::class.java).toString()
                }
                override fun onCancelled(error: DatabaseError) { }
            })

        val time = System.currentTimeMillis()
        val currentTime = SimpleDateFormat("yyyy/MM/dd HH:mm").format(Date(time)).toString()


        //전송 버튼 클릭시 db에 댓글 내용 추가
        binding.btnSend.setOnClickListener {
            val content = binding.editComment.text.toString()
            val commentObject = Comments(content, currentUser, writerName, currentTime)

            //디비에 메시지 데이터 저장
            mDbRef.child("Comments").child(gameRoom).push()
                .setValue(commentObject).addOnSuccessListener {
                }
            binding.editComment.setText("")
        }


        //리사이블러뷰에 댓글 내용을 추가하기
        mDbRef.child("Comments").child(gameRoom).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentList.clear()
                    keyList.clear()
                    for(postSnapshot in snapshot.children) {
                        val data = postSnapshot.getValue(Comments::class.java)
                        val uidKey: String = postSnapshot.key.toString()
                        commentList.add(data!!)
                        keyList.add(uidKey)
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) { }
            })




    }//onCreate 끝


    class MyAdapter(
        private val context: Context,
        private val commentList: ArrayList<Comments>,
        private val currentUser: String?,
        private val keyList: ArrayList<String>,
        private val gameRoom: String
    ):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
            val writerName: TextView = itemView.findViewById(R.id.commentName)
            val content: TextView = itemView.findViewById(R.id.commentContent)
            val time: TextView = itemView.findViewById(R.id.commentTime)
            val removeButton: Button = itemView.findViewById(R.id.btnCommentRemove)
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
                        val selectedKey = keyList[position]
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

    }
}