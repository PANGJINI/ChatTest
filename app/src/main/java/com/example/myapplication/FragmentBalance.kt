package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentBalanceBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class FragmentBalance : Fragment() {

    lateinit var binding: FragmentBalanceBinding
    lateinit var mDbRef: DatabaseReference
    lateinit var adapter: MyAdapter
    lateinit var balanceGameList: ArrayList<BalanceGameDataModel>   //밸런스게임 목록을 표시할 리스트
    lateinit var gameRoomList: ArrayList<String>    //각 게임룸의 키 값을 저장할 리스트


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBalanceBinding.inflate(inflater)
        mDbRef = FirebaseDatabase.getInstance().reference
        balanceGameList = ArrayList()
        gameRoomList = ArrayList()

        adapter = MyAdapter(context,balanceGameList, gameRoomList)
        binding.balanceRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.balanceRecyclerView.adapter = adapter

        //밸런스게임 디비 가져와서 리스트에 넣기
        mDbRef.child("BalanceGame").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                balanceGameList.clear()
                gameRoomList.clear()
                for(postSnapshot in snapshot.children) {
                    val data = postSnapshot.getValue(BalanceGameDataModel::class.java)
                    val key: String = postSnapshot.key.toString()
                    balanceGameList.add(data!!)
                    gameRoomList.add(key)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) { }
        })


        //플로팅버튼 누르면 데이터 추가 액티비티로 전환
        binding.fabAdd.setOnClickListener {
            val intent = Intent(activity, BalanceAddActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }//onCreateView

    class MyAdapter(
        private val context: Context?,
        private val balanceGameList: ArrayList<BalanceGameDataModel>,
        private val gameRoomList: ArrayList<String>
    ):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val userImage: CircleImageView = itemView.findViewById(R.id.balanceImage)
            val postName: TextView = itemView.findViewById(R.id.balanceName)
            val time: TextView = itemView.findViewById(R.id.balanceTime)
            val balanceTitle: TextView = itemView.findViewById(R.id.balanceTitle)
            val voteCount: TextView = itemView.findViewById(R.id.balanceVoteCount)
            val blueColor = ContextCompat.getColor(itemView.context, R.color.blue)

            fun borderChange (game: BalanceGameDataModel) {
                if (game.postUserGender == "남성") userImage.borderColor = blueColor
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_balance_game_list, parent, false)
            return MyViewHolder(view)
        }

        override fun getItemCount(): Int {
            return balanceGameList.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val currentGame = balanceGameList[position]
            val currentRoomId = gameRoomList[position]
            val currentUser = currentGame.postUserId
            val title = "${currentGame.bal1} \n" + "  \uD83C\uDD9A ${currentGame.bal2}"
            val voteCount = currentGame.voteCountList?.sum().toString()
            holder.postName.text = currentGame.postUserName
            holder.time.text = currentGame.time
            holder.balanceTitle.text = title
            holder.voteCount.text = "\uD83D\uDDF3️️ $voteCount"

            val storage = FirebaseStorage.getInstance()
            val imgRef = storage.reference.child("images/IMAGE_${currentUser}_.png")
            imgRef.downloadUrl.addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    //글라이드에서 이미지 가져와서 circleView에 설정하기
                    if (context != null) {
                        Glide.with(context).load(task.result).into(holder.userImage)
                    }
                }
            }
            holder.borderChange(currentGame)


            //리사이클러뷰 클릭 시 밸런스 게임 화면으로 이동
            holder.itemView.setOnClickListener {
                val intent = Intent(context, BalanceGameActivity::class.java)
                //bal1, bal2, userid, username, usergender, time
                intent.putExtra("gameRoomId", currentRoomId)
                intent.putExtra("balTitle", title)
                intent.putExtra("bal1", currentGame.bal1)
                intent.putExtra("bal2", currentGame.bal2)
                intent.putExtra("userid", currentGame.postUserId)
                intent.putExtra("username", currentGame.postUserName)
                intent.putExtra("usergender", currentGame.postUserGender)
                intent.putExtra("time", currentGame.time)
                context?.startActivity(intent)
                true
            }


        }


    }


}