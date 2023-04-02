package com.soen490chrysalis.papilio.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
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
        binding = AccountMenuFragmentBinding.inflate(inflater, container, false)

        binding.accountUserProfile.setOnClickListener {
            val intent = Intent(requireActivity(), UserProfileActivity::class.java)
            startActivity(intent)
        }
        
        binding.accountActivityQuiz.setOnClickListener {
            val intent = Intent(this.activity, QuizPart1::class.java)
            startActivity(intent)
        }

        binding.accountLogout.setOnClickListener{
            //Firebase sign out
            FirebaseAuth.getInstance().signOut()

            //Google sign out
            val googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)
            googleSignInClient.signOut()

            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)

            //prevents user from using "back" to go to previous screen
            requireActivity().finish()
        }

        return binding.root
    }
}