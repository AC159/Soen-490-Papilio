package com.soen490chrysalis.papilio.view

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import android.content.Intent
import android.view.MenuItem
import android.widget.Button
import com.soen490chrysalis.papilio.R
import com.soen490chrysalis.papilio.databinding.ActivityQuizPart1Binding

class QuizPart1 : AppCompatActivity()
{
    private lateinit var binding : ActivityQuizPart1Binding
    private lateinit var next : Button
    private lateinit var outdoorButton : Button
    private lateinit var indoorButton : Button
    private lateinit var question : TextView

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizPart1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        next = binding.next
        outdoorButton = binding.choice1
        indoorButton = binding.choice2
        question = binding.question

        var outdoorButtonSelected = false
        var indoorButtonSelected = false

        next.setOnClickListener {
            val intent = Intent(this@QuizPart1, QuizPart2Activity::class.java)
            intent.putExtra("indoorSelected", indoorButtonSelected)
            intent.putExtra("outdoorSelected", outdoorButtonSelected)
            startActivity(intent)
        }

        outdoorButton.setOnClickListener {
            if (outdoorButtonSelected)
            {
                outdoorButton.setCompoundDrawables(null, null, null, null)
            }
            else
            {
                val img = resources.getDrawable(R.drawable.ic_checkmark)
                img.setBounds(0, 0, 60, 60)
                outdoorButton.setCompoundDrawables(null, null, img, null)
            }
            outdoorButtonSelected = !outdoorButtonSelected
        }

        indoorButton.setOnClickListener {
            if (indoorButtonSelected)
            {
                indoorButton.setCompoundDrawables(null, null, null, null)
            }
            else
            {
                val img = resources.getDrawable(R.drawable.ic_checkmark)
                img.setBounds(0, 0, 60, 60)
                indoorButton.setCompoundDrawables(null, null, img, null)
            }
            indoorButtonSelected = !indoorButtonSelected
        }

        val actionBar = supportActionBar
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Activity Quiz"
        }
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean
    {
        val id = item.itemId
        if (id == android.R.id.home)
        {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}