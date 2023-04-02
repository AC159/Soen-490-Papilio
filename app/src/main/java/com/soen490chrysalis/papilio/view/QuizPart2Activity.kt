package com.soen490chrysalis.papilio.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.soen490chrysalis.papilio.R
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.util.TypedValue
import android.view.ViewGroup
import android.view.MenuItem
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.soen490chrysalis.papilio.databinding.ActivityQuizPart2Binding
import com.soen490chrysalis.papilio.services.network.responses.GenreObject
import com.soen490chrysalis.papilio.viewModel.GenreViewModel
import com.soen490chrysalis.papilio.viewModel.factories.GenreViewModelFactory
import org.json.JSONArray
import org.json.JSONObject


class QuizPart2Activity : AppCompatActivity()
{
    private lateinit var binding : ActivityQuizPart2Binding
    private lateinit var genreViewModel : GenreViewModel
    private lateinit var genreList : List<GenreObject>

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizPart2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = GenreViewModelFactory()
        genreViewModel = ViewModelProvider(this, viewModelFactory)[GenreViewModel::class.java]
        genreViewModel.getAllGenres()

        val selectedGenres = mutableListOf<Int>() //List to store selected genre IDs

        genreViewModel.genreObject.observe(this, Observer {
            genreList = genreViewModel.genreObject.value!!

            // Now that we have received all activity categories from the backend, let's display them
            val container = binding.container
            for (genre in genreList)
            {
                val button = Button(this)
                button.id = genre.id.toInt()
                button.text = genre.name
                button.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_blue))
                button.setTextColor(Color.parseColor("#FFFFFF"))

                val widthInDp = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    338f,
                    resources.displayMetrics
                ).toInt() // setting the dimensions of the button in DP unit

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
                        selectedGenres.remove(button.id)
                    }
                    else
                    {
                        val img = resources.getDrawable(R.drawable.ic_checkmark)
                        img.setBounds(0, 0, 60, 60)
                        button.setCompoundDrawables(null, null, img, null)
                        isSelected[0] = true
                        selectedGenres.add(button.id)
                    }
                }

                container.addView(button)
            }
        })

        binding.doneBtn.setOnClickListener {
            val selectedGenreIds = selectedGenres.toIntArray()
            val indoorButtonSelected = intent.getBooleanExtra("indoorSelected", false)
            val outdoorButtonSelected = intent.getBooleanExtra("outdoorSelected", false)

            val jsonObject = JSONObject()
            jsonObject.put("indoor", indoorButtonSelected)
            jsonObject.put("outdoor", outdoorButtonSelected)
            jsonObject.put("genres", JSONArray(selectedGenreIds))
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