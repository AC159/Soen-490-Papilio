package com.soen490chrysalis.papilio.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide() //Hiding the action title bar for UI debugging purposes
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // the user should be already signed in
        firebaseUser = firebaseAuth.currentUser!!

        binding.button2.setOnClickListener(){
            firebaseAuth.signOut()
            val initialActivity = Intent(this, InitialActivity::class.java)
            startActivity(initialActivity)
            finish()
        }
        binding.tvMessage.text = "Hello ${firebaseUser.email}!"

    }
}