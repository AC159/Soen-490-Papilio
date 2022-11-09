package com.soen490chrysalis.papilio.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.*
import com.soen490chrysalis.papilio.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener
{
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

        // Setting the listener to detect when we press one of the button on the navigation bar
        bottomNavigationView = binding.bottonnav
        bottomNavigationView?.setOnItemSelectedListener(this)
    }

    // Function that loads in a page/fragment on the navigation bar when you tap the respective button
    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.home ->
            {
                supportFragmentManager.beginTransaction().replace(binding.fragmentContainerView.id, HomeFragment(), "HOME").commit()
                currentFragmentID = "HOME"
            }

            R.id.browse ->
            {
                supportFragmentManager.beginTransaction().replace(binding.fragmentContainerView.id, BrowseFragment(), "BROWSE").commit()
                currentFragmentID = "BROWSE"
            }

            R.id.activities ->
            {
                supportFragmentManager.beginTransaction().replace(binding.fragmentContainerView.id, ActivitiesFragment(), "ACTIVITIES").commit()
                currentFragmentID = "ACTIVITIES"
            }

            R.id.account ->
            {
                supportFragmentManager.beginTransaction().replace(binding.fragmentContainerView.id, AccountMenuFragment(), "ACCOUNT MENU").commit()
                currentFragmentID = "ACCOUNT MENU"
            }
        }

        return true
    }
}