package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentMemeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class FragmentMeme : Fragment() {

    lateinit var binding: FragmentMemeBinding
    lateinit var memeList: ArrayList<SimpleChatDataModel>
    lateinit var adapter: MyAdapter
    lateinit var searchView: SearchView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemeBinding.inflate(inflater)
        mAuth = Firebase.auth
        mDbRef = Firebase.database.reference
        memeList = ArrayList()
        adapter = MyAdapter(context, memeList)
        searchView = binding.searchView

        binding.memeRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.memeRecyclerView.adapter = adapter

        //SearchView에 리스너 추가
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }

        })

        //DB에 있는 간편채팅 데이터 가져와서 리스트에 넣기
        mDbRef.child("simpleChat").child("meme")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    memeList.clear()
                    for (postSnapshot in snapshot.children) {
                        val data = postSnapshot.getValue(SimpleChatDataModel::class.java)
                        if (data != null) {
                            memeList.add(data!!)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("간챗", "----------플러팅 탭 데이터 로딩 실패")
                }
            })

        return binding.root
    }//onCreateView 끝


    class MyAdapter(
        private val context: Context?,
        private val memeList: ArrayList<SimpleChatDataModel>
    ) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(), Filterable {

        private var excelSearchList: ArrayList<SimpleChatDataModel>? = null


        //리사이클러뷰와 연결하는 부분
        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.item_data)
        }

        //초기화 구문 init
        init {
            this.excelSearchList = memeList
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_simple_chat_data, parent, false)
            return MyViewHolder(view)
        }

        override fun getItemCount(): Int {
            return excelSearchList?.size ?: 0
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val currentData = excelSearchList?.get(position)?.chatData
            holder.textView.text = currentData
            //리사이클러뷰를 선택했을 때 editText에 텍스트 넣기
            holder.itemView.setOnClickListener{
                (context as ChatActivity).binding.messageEdit.setText(currentData)
            }
            //길게 클릭할 경우 텍스트 누적시키기
            holder.itemView.setOnLongClickListener {
                (context as ChatActivity).binding.messageEdit.append(" " + currentData)
                true
            }
        }

        // Filter 클래스와 관련된 부분
        override fun getFilter() : Filter {
            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence?): FilterResults {
                    var charString = charSequence.toString()
                    if(charString.isEmpty()) {
                        excelSearchList = memeList
                    } else {
                        var filteredList = ArrayList<SimpleChatDataModel>()
                        for(row in memeList) {
                            if (row.chatData.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row)
                            }
                        }
                        excelSearchList = filteredList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = excelSearchList
                    return filterResults
                }

                override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                    if (filterResults != null) {
                        excelSearchList = filterResults.values as ArrayList<SimpleChatDataModel>
                        notifyDataSetChanged()
                    }



                }

            }
        }

    }//어댑터 끝


}