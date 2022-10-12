package com.soen490chrysalis.papilio.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.view.*

import com.soen490chrysalis.papilio.ActivitiesFragment
import com.soen490chrysalis.papilio.BrowseFragment
import com.soen490chrysalis.papilio.HomeFragment
import com.soen490chrysalis.papilio.UserProfileFragment

import com.soen490chrysalis.papilio.R


class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener{
    var bottomNavigationView: NavigationBarView? = null

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
            binding.tvMessage.text = "You are now logged out!"
            firebaseAuth.signOut()
        }
        binding.tvMessage.text = "Hello ${firebaseUser.displayName}!"

        //Setting the listener to detect when we press one of the button on the navigation bar
        bottomNavigationView = findViewById(R.id.bottonnav)
        bottomNavigationView?.setOnItemSelectedListener(this)


    }


    //Function that loads in a page/fragment on the navigation bar when you tap the respective button
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.home -> supportFragmentManager.beginTransaction().replace(R.id.relativelayout, HomeFragment()).commit()
            R.id.browse -> supportFragmentManager.beginTransaction().replace(R.id.relativelayout, BrowseFragment()).commit()
            R.id.activities -> supportFragmentManager.beginTransaction().replace(R.id.relativelayout, ActivitiesFragment()).commit()
            R.id.account -> supportFragmentManager.beginTransaction().replace(R.id.relativelayout, UserProfileFragment()).commit()
        }
        return true
    }
}