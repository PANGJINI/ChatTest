package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.myapplication.databinding.FragmentFlirtingBinding

class FragmentFlirting : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentFlirtingBinding

    //버튼 배열 선언
    val btns = arrayOfNulls<TextView>(15)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFlirtingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        btns[0] = binding.btnFlirting1
        btns[1] = binding.btnFlirting2
        btns[2] = binding.btnFlirting3
        btns[3] = binding.btnFlirting4
        btns[4] = binding.btnFlirting5
        btns[5] = binding.btnFlirting6
        btns[6] = binding.btnFlirting7
        btns[7] = binding.btnFlirting8
        btns[8] = binding.btnFlirting9
        btns[9] = binding.btnFlirting10
        btns[10] = binding.btnFlirting11
        btns[11] = binding.btnFlirting12
        btns[12] = binding.btnFlirting13
        btns[13] = binding.btnFlirting14
        btns[14] = binding.btnFlirting15
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
            binding.btnFlirting1.id -> { btnText = btns[0]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting2.id ->{ btnText = btns[1]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting3.id ->{ btnText = btns[2]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting4.id ->{ btnText = btns[3]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting5.id ->{ btnText = btns[4]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting6.id ->{ btnText = btns[5]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting7.id ->{ btnText = btns[6]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting8.id ->{ btnText = btns[7]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting9.id ->{ btnText = btns[8]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting10.id ->{ btnText = btns[9]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting11.id ->{ btnText = btns[10]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting12.id ->{ btnText = btns[11]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting13.id ->{ btnText = btns[12]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting14.id ->{ btnText = btns[13]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
            binding.btnFlirting15.id ->{ btnText = btns[14]?.getText().toString()
                (activity as ChatActivity).binding.messageEdit.append(" "+btnText) }
        }

    }

}