package com.soen490chrysalis.papilio.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.navigation.NavigationBarView
import com.soen490chrysalis.papilio.databinding.ActivityMainBinding
import com.soen490chrysalis.papilio.ActivitiesFragment
import com.soen490chrysalis.papilio.BrowseFragment
import com.soen490chrysalis.papilio.HomeFragment
import com.soen490chrysalis.papilio.UserProfileFragment
import com.soen490chrysalis.papilio.R


class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener
{
    var bottomNavigationView: NavigationBarView? = null
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Setting the listener to detect when we press one of the button on the navigation bar
        bottomNavigationView = findViewById(R.id.bottonnav)
        bottomNavigationView?.setOnItemSelectedListener(this)
    }

    // Function that loads in a page/fragment on the navigation bar when you tap the respective button
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.home -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, HomeFragment()).commit()
            R.id.browse -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, BrowseFragment()).commit()
            R.id.activities -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, ActivitiesFragment()).commit()
            R.id.account -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, UserProfileFragment()).commit()
        }
        return true
    }
}