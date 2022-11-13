package com.soen490chrysalis.papilio.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.*
import com.soen490chrysalis.papilio.databinding.ActivityUserProfileBinding


class UserProfileActivity : AppCompatActivity()
{
    private lateinit var binding : ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        //Get the binding for the User Profile Activity
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Get the last signed in Google account so we can check if the user logged in with Google or through normal log in
        val acct = GoogleSignIn.getLastSignedInAccount(this)

        // If the Google account exists (which means the user used Google to log in / sign up
        if(acct != null)
        {
            //Get the photoUrl of the google profile picture of your Google account
            var googleProfilePicture = acct.photoUrl

            if(googleProfilePicture == null)
            {
                binding.userProfilePicture.setImageResource(R.drawable.user_pfp_example)
            }
            else
            {
                Glide.with(this)
                    .load(acct.photoUrl)
                    .circleCrop()
                    .into(binding.userProfilePicture)
            }

            binding.userProfileName.setText(acct.displayName)
            binding.userProfileEmail.setText("Email: "+acct.email)
        }
        else
        {
            val user = FirebaseAuth.getInstance().currentUser
            user?.let{

                binding.userProfileName.setText("John Doe")
                binding.userProfileEmail.setText("Email: " + user.email)
                binding.userProfilePicture.setImageResource(R.drawable.user_pfp_example)
            }

        }


        var actionBar = supportActionBar
        super.onCreate(savedInstanceState)

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "User Profile"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}