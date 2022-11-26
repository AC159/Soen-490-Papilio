package com.soen490chrysalis.papilio.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.soen490chrysalis.papilio.R
import com.soen490chrysalis.papilio.databinding.AccountMenuFragmentBinding
import kotlinx.android.synthetic.main.account_menu_fragment.*
import kotlinx.android.synthetic.main.account_menu_fragment.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [UserProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountMenuFragment : Fragment()
{
    private lateinit var binding : AccountMenuFragmentBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccountMenuFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.account_menu_fragment, container, false)
        val profileButton = view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.account_user_profile)
        val quizbutton = view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.account_activity_quiz)

        profileButton?.setOnClickListener {
            val intent = Intent(this.activity, UserProfileActivity::class.java)
            startActivity(intent)
        }
        quizbutton?.setOnClickListener {
            val intent = Intent(this.activity, QuizPart1::class.java)
            startActivity(intent)
        }

        return view
    }
}