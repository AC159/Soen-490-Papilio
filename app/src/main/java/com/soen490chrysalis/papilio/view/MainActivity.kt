package com.soen490chrysalis.papilio.view

import android.accounts.Account
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.*
import com.soen490chrysalis.papilio.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_account_menu.view.*
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener{
    var bottomNavigationView: NavigationBarView? = null

    private lateinit var binding : ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser : FirebaseUser
    private var currentFragmentID : String = ""

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
            R.id.home -> {
                // Getting the current active fragment
                var fm = supportFragmentManager.findFragmentByTag(currentFragmentID)

                //Removing the current active fragment from view when pressing the home button on the nav bar
                if (fm != null) {
                    supportFragmentManager.beginTransaction().remove(fm).commit()
                }
            }
            R.id.browse -> {
                supportFragmentManager.beginTransaction().replace(R.id.relativelayout, BrowseFragment(), "BROWSE").commit()
                currentFragmentID = "BROWSE"
            }
            R.id.activities -> {
                supportFragmentManager.beginTransaction().replace(R.id.relativelayout, ActivitiesFragment(), "ACTIVITIES").commit()
                currentFragmentID = "ACTIVITIES"
            }
            R.id.account -> {
                supportFragmentManager.beginTransaction().replace(R.id.relativelayout, AccountMenuFragment(), "ACCOUNT MENU").commit()
                currentFragmentID = "ACCOUNT MENU"
            }

        }

        return true
    }
}