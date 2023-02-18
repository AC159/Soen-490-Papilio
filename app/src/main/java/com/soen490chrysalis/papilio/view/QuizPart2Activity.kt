package com.soen490chrysalis.papilio.view

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import android.os.Bundle
import com.soen490chrysalis.papilio.R
import com.soen490chrysalis.papilio.view.ButtonObject
import android.widget.LinearLayout
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import android.util.TypedValue
import android.view.ViewGroup
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.soen490chrysalis.papilio.services.network.responses.GenreObject
import com.soen490chrysalis.papilio.viewModel.GenreViewModel
import com.soen490chrysalis.papilio.viewModel.factories.GenreViewModelFactory


class QuizPart2Activity : AppCompatActivity()
{
    var next : Button? = null
    var other : Button? = null
    var choice1 : Button? = null
    var choice2 : Button? = null
    var choice3 : Button? = null
    var choice4 : Button? = null
    var question : TextView? = null
    var skip : TextView? = null
    var input : TextInputEditText? = null
    private lateinit var genreViewModel : GenreViewModel
    private lateinit var genreList : List<GenreObject>
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_part2)
        val viewModelFactory = GenreViewModelFactory()
        genreViewModel = ViewModelProvider(this, viewModelFactory)[GenreViewModel::class.java]
        genreViewModel.getAllGenres(category = "sport")
        genreViewModel.genreObject.observe(this, Observer {
            genreList = genreViewModel.genreObject.value!!
            Log.d("getAllGenres", genreList.toString())
        })

        //For testing purposes. Must repurpose this button array to actual activities found in backend
        val buttonArray = arrayOf(
            ButtonObject(
                1,
                "Is this working",
                null,
                "sport",
                "2023-01-18T03:50:10.550Z",
                "2023-01-18T03:50:10.550Z"
            ),
            ButtonObject(
                2,
                "Basketball",
                null,
                "sport",
                "2023-01-18T03:50:10.550Z",
                "2023-01-18T03:50:10.550Z"
            ),
            ButtonObject(
                3,
                "Test to see",
                null,
                "sport",
                "2023-01-18T03:50:10.550Z",
                "2023-01-18T03:50:10.550Z"
            ),
            ButtonObject(
                4,
                "Test4",
                null,
                "sport",
                "2023-01-18T03:50:10.550Z",
                "2023-01-18T03:50:10.550Z"
            ),
            ButtonObject(
                5,
                "Test5",
                null,
                "sport",
                "2023-01-18T03:50:10.550Z",
                "2023-01-18T03:50:10.550Z"
            ),
            ButtonObject(
                6,
                "Test6",
                null,
                "sport",
                "2023-01-18T03:50:10.550Z",
                "2023-01-18T03:50:10.550Z"
            ),
            ButtonObject(
                7,
                "Test7",
                null,
                "sport",
                "2023-01-18T03:50:10.550Z",
                "2023-01-18T03:50:10.550Z"
            ),
            ButtonObject(
                8,
                "Test8",
                null,
                "sport",
                "2023-01-18T03:50:10.550Z",
                "2023-01-18T03:50:10.550Z"
            )
        )
        val container = findViewById<LinearLayout>(R.id.container)
        for (buttonObject in buttonArray)
        {
            val button = Button(this)
            button.id = buttonObject.id
            button.text = buttonObject.name
            button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_blue))
            val widthInDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                338f,
                resources.displayMetrics
            ).toInt() //setting the dimensions of the button in DP unit
            val heightInDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                76f,
                resources.displayMetrics
            ).toInt()
            button.layoutParams = ViewGroup.LayoutParams(widthInDp, heightInDp)
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
            val isSelected = booleanArrayOf(false)
            button.setOnClickListener {
                if (isSelected[0])
                {
                    button.setCompoundDrawables(null, null, null, null)
                    isSelected[0] = false
                }
                else
                {
                    val img = resources.getDrawable(R.drawable.ic_checkmark)
                    img.setBounds(0, 0, 60, 60)
                    button.setCompoundDrawables(null, null, img, null)
                    isSelected[0] = true
                }
            }
            container.addView(button)
        }
        next = findViewById<View>(R.id.next) as Button
        question = findViewById<View>(R.id.question) as TextView
        skip = findViewById<View>(R.id.skip) as TextView
        next!!.setOnClickListener {
            question!!.text = "Which of the following arts do you enjoy more?"
            choice1!!.text = "ARTS AND CRAFTS"
            choice2!!.text = "DANCE"
            choice3!!.text = "MUSIC"
            choice4!!.text = "LITERATURE"
            other!!.text = "Other Arts"
            input!!.visibility = View.INVISIBLE
        }
        skip!!.setOnClickListener {
            startActivity(
                Intent(
                    this@QuizPart2Activity,
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