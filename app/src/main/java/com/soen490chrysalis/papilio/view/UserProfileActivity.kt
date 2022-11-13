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
            // Get the photoUrl of the google profile picture of your Google account
            var googleProfilePicture = acct.photoUrl

            // if the user's google profile picture exists then display it on the user profile activity
            if(googleProfilePicture == null)
            {
                binding.userProfilePicture.setImageResource(R.drawable.user_pfp_example)
            }
            else // If the user's google profile picture does not exist, then display just the placeholder image on the profile
            {
                // Here we use the Glide library to import the pfp from the google account of the logged in user
                Glide.with(this)
                    .load(acct.photoUrl)
                    .circleCrop()
                    .into(binding.userProfilePicture)
            }

            // Also display the full name and email from the user's Google account on the profile
            binding.userProfileName.setText(acct.displayName)
            binding.userProfileEmail.setText("Email: "+acct.email)
        }
        else // If the google account does not exist, then it means the user logged in with their firebase account
        {
            // Get the firebase user from FireBaseAuth
            val user = FirebaseAuth.getInstance().currentUser
            user?.let{

                // Set the full name, email on the profile from the firebase account, and show the placeholder pfp as the pfp
                binding.userProfileName.setText(user.displayName)
                binding.userProfileEmail.setText("Email: " + user.email)
                binding.userProfilePicture.setImageResource(R.drawable.user_pfp_example)
            }

        }

        // Create Action Bar var so we can 1) display it with a proper title and 2) put a working back button on it
        var actionBar = supportActionBar
        super.onCreate(savedInstanceState)

        // if Action Bar is not null, then put a back button on it as well as put the "User Profile" title on it
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "User Profile"
        }
    }

    // This is the function that's called when the back button on the action bar is pressed
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