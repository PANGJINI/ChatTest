package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.viewbinding.ViewBindings
import com.example.myapplication.databinding.FragmentGreetingBinding

class FragmentGreeting : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentGreetingBinding
    //val simpleChatActivity = View.inflate(context, R.layout.activity_simple_chat, null)
    //val simpleChatActivity:SimpleChatActivity = SimpleChatActivity()

    //버튼 배열 선언
    val btns = arrayOfNulls<Button>(4)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGreetingBinding.inflate(inflater, container, false)
        //return inflater.inflate(R.layout.fragment_greeting, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        btns[0] = binding.btnGreeting1
        btns[1] = binding.btnGreeting2
        btns[2] = binding.btnGreeting3
        btns[3] = binding.btnGreeting4
        btns[0]?.setOnClickListener(this)
//        btns.forEach { btns ->
//            btns?.setOnClickListener(this)
//        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        ////리스너 안먹음???? 왜?????
        ////데이터를 다른 액티비티로 넘기기




    //SimpleChatActivity().binding.messageEdit.setText(btnText)
        //var messageEdit = simpleChatActivity.findViewById<EditText>(R.id.message_edit).setText(btnText)
        //simpleChatActivity.messageEdit.setText(btnText)


    }

    override fun onClick(v: View?) {

        var btnText = btns[0]?.getText().toString()
        Log.e("fragment", btnText)
        Toast.makeText(context, btnText, Toast.LENGTH_LONG).show()
    }

}