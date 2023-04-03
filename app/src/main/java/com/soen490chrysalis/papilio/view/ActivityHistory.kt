package com.soen490chrysalis.papilio.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.soen490chrysalis.papilio.databinding.ActivityActivitiesBoxBinding
import com.soen490chrysalis.papilio.databinding.ActivityHistoryBinding
import com.soen490chrysalis.papilio.viewModel.ActivityHistoryViewModel
import com.soen490chrysalis.papilio.viewModel.factories.ActivityHistoryViewModelFactory
import java.time.LocalDate

class ActivityHistory : AppCompatActivity()
{
    private lateinit var binding : ActivityHistoryBinding
    private lateinit var activityHistoryViewModel : ActivityHistoryViewModel
    private var dateToday : LocalDate = LocalDate.now()
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val actionBar = supportActionBar

        // if Action Bar is not null, then put a back button on it as well as put the "User Profile" title on it
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Activity History"
        }

        val activityHistoryFactory = ActivityHistoryViewModelFactory()
        activityHistoryViewModel =
            ViewModelProvider(this, activityHistoryFactory)[ActivityHistoryViewModel::class.java]

        activityHistoryViewModel.getActivityHistory()

        activityHistoryViewModel.activitiesResponse.observe(this, Observer {

            displayProgressCircle(false)

            val activityList = activityHistoryViewModel.getPastActivities(dateToday)

            if (activityList.isNotEmpty())
            {
                for (activity in activityList)
                {
                    val activityBoxBinding = layoutInflater.let { it1 ->
                        ActivityActivitiesBoxBinding.inflate(
                            it1
                        )
                    }

                    val activityBoxImage = activityBoxBinding.activityBoxImage
                    val activityBoxTitle = activityBoxBinding.activityBoxTitle
                    val activityBoxDesc = activityBoxBinding.activityBoxAddress
                    activityBoxBinding.activityBoxStartTime.visibility = View.GONE

                    if (activity.images != null && activity.images.isNotEmpty())
                    {
                        Glide.with(this).load(activity.images[0]).into(activityBoxImage)
                    }

                    activityBoxTitle.text = activity.title!!.replace("\"", "")
                    activityBoxDesc.text = activity.description!!.replace("\"", "")

                    activityBoxBinding.activityBox.setOnClickListener {

                        val intent =
                            Intent(this.baseContext, DisplayActivityInfoActivity::class.java)
                        intent.putExtra("id", activity.id)
                        intent.putExtra("title", activity.title)
                        intent.putExtra("description", activity.description)
                        intent.putExtra("contact", activity.business?.email)
                        intent.putExtra("user_id", activity.user?.firebase_id)
                        intent.putExtra("business_id", activity.business?.businessId)
                        intent.putExtra(
                            "individualCost",
                            if (activity.costPerIndividual == "0") "FREE" else ("$" + activity.costPerIndividual + "/person")
                        )
                        intent.putExtra(
                            "groupCost",
                            if (activity.costPerGroup == "0") "FREE" else ("$" + activity.costPerGroup + "/group")
                        )
                        intent.putExtra("location", activity.address)

                        if (activity.images != null && activity.images.isNotEmpty())
                        {
                            intent.putExtra("images", true)
                            var x = 0
                            Log.d("Size", activity.images.size.toString())
                            for (i in activity.images)
                            {
                                intent.putExtra("images$x", i)
                                x++
                            }
                            if (x != 5)
                            {
                                for (e in x until 5)
                                {
                                    intent.putExtra("images$e", "")
                                    x++
                                }
                            }
                        }
                        else intent.putExtra("images", false)

                        startActivity(intent)

                    }

                    binding.activityList.addView(activityBoxBinding.activityBox)
                }
            }
            else
            {
                displayProgressCircle(true)
                binding.progressBar1.visibility = View.GONE
                binding.noActivityFoundBox.visibility = View.VISIBLE

            }
        })

    }

    override fun onCreateView(name : String, context : Context, attrs : AttributeSet) : View?
    {
        return super.onCreateView(name, context, attrs)
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

    fun displayProgressCircle(shouldDisplay : Boolean)
    {
        binding.progressBarContainer.visibility = if (shouldDisplay) View.VISIBLE else View.GONE
        binding.activityContainer.visibility = if (shouldDisplay) View.GONE else View.VISIBLE
    }
}