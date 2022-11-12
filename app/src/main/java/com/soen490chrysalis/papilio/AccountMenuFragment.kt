package com.soen490chrysalis.papilio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.soen490chrysalis.papilio.databinding.ActivityAccountMenuBinding
import com.soen490chrysalis.papilio.view.UserProfileActivity
import kotlinx.android.synthetic.main.activity_account_menu.*
import kotlinx.android.synthetic.main.activity_account_menu.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [UserProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountMenuFragment : Fragment()
{
    private lateinit var binding : ActivityAccountMenuBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountMenuBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_account_menu, container, false)
        val profileButton = view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.account_user_profile)

        profileButton?.setOnClickListener {
            val intent = Intent(this.activity, UserProfileActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}