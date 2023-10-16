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

    //버튼 배열 선언
    val btns = arrayOfNulls<Button>(4)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGreetingBinding.inflate(inflater)
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
        btns.forEach { btns ->
            btns?.setOnClickListener(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onClick(v: View?) {
        var btnText: String
        when(v?.id) {
            binding.btnGreeting1.id -> { btnText = btns[0]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnGreeting2.id ->{ btnText = btns[1]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnGreeting3.id ->{ btnText = btns[2]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnGreeting4.id ->{ btnText = btns[3]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
        }

    }

}