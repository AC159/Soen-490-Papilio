package com.soen490chrysalis.papilio.view

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import android.content.Intent
import android.view.MenuItem
import android.widget.Button
import com.soen490chrysalis.papilio.databinding.ActivityQuizPart1Binding

class QuizPart1 : AppCompatActivity()
{
    private lateinit var binding : ActivityQuizPart1Binding
    private lateinit var next : Button
    private lateinit var choice1 : Button
    private lateinit var choice2 : Button
    private lateinit var question : TextView

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizPart1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        next = binding.next
        choice1 = binding.choice1
        choice2 = binding.choice2
        question = binding.question

        next.setOnClickListener {
            startActivity(
                Intent(
                    this@QuizPart1,
                    QuizPart2Activity::class.java
                )
            )
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