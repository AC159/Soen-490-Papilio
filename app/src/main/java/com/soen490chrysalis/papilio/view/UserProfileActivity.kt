package com.soen490chrysalis.papilio.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.soen490chrysalis.papilio.databinding.ActivityUserProfileBinding

class UserProfileActivity : AppCompatActivity()
{
    private lateinit var binding : ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}