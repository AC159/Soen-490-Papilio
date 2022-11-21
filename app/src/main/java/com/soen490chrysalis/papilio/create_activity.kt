package com.soen490chrysalis.papilio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.soen490chrysalis.papilio.databinding.ActivityCreateActivityBinding

class create_activity : AppCompatActivity()
{
    private lateinit var binding : ActivityCreateActivityBinding

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Create Action Bar var so we can 1) display it with a proper title and 2) put a working back button on it
        var actionBar = supportActionBar

        // if Action Bar is not null, then put a back button on it as well as put the "User Profile" title on it
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Create Activity"
        }
    }

    // This is the function that's called when the back button on the action bar is pressed
    override fun onOptionsItemSelected(item : MenuItem) : Boolean
    {
        when (item.itemId)
        {
            android.R.id.home ->
            {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}