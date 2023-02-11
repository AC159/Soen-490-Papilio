package com.soen490chrysalis.papilio.view

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import com.soen490chrysalis.papilio.R
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.Button

class QuizPart1 : AppCompatActivity()
{
    var next : Button? = null
    var choice1 : Button? = null
    var choice2 : Button? = null
    var question : TextView? = null
    var title : TextView? = null
    var skip : TextView? = null
    var firstClick = false
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_part1)
        next = findViewById<View>(R.id.next) as Button
        choice1 = findViewById<View>(R.id.choise1) as Button
        choice2 = findViewById<View>(R.id.choise2) as Button
        question = findViewById<View>(R.id.question) as TextView
        title = findViewById<View>(R.id.textView4) as TextView
        skip = findViewById<View>(R.id.skip) as TextView
        next!!.setOnClickListener {
            startActivity(
                Intent(
                    this@QuizPart1,
                    QuizPart2Activity::class.java
                )
            )
        }
        skip!!.setOnClickListener {
            startActivity(
                Intent(
                    this@QuizPart1,
                    MainActivity::class.java
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