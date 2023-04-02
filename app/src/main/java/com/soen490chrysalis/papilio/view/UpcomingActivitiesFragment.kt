package com.soen490chrysalis.papilio.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.databinding.ActivityActivitiesBoxSmallBinding
import com.soen490chrysalis.papilio.databinding.FragmentActivitiesBinding
import com.soen490chrysalis.papilio.databinding.UpcomingActivitiesMonthContainerBinding
import com.soen490chrysalis.papilio.viewModel.UpcomingActivitiesViewModel
import com.soen490chrysalis.papilio.viewModel.factories.UpcomingActivitiesViewModelFactory
import kotlinx.android.synthetic.main.fragment_activities.view.*
import java.time.LocalDate


class UpcomingActivitiesFragment : Fragment()
{
    private lateinit var upcomingActivitiesViewModel : UpcomingActivitiesViewModel

    private var _binding : FragmentActivitiesBinding? = null
    private val binding get() = _binding!!

    private lateinit var monthContainer : LinearLayout
    private lateinit var progressBar : LinearLayout
    private lateinit var monthScrollContainer : ScrollView
    private var dateToday : LocalDate = LocalDate.now()

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?
    ) : View
    {
        val upcomingActivitiesVMFactory = UpcomingActivitiesViewModelFactory()
        upcomingActivitiesViewModel = ViewModelProvider(
            this, upcomingActivitiesVMFactory
        )[UpcomingActivitiesViewModel::class.java]

        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        val view = binding.root

        monthContainer = binding.monthContainer
        progressBar = binding.progressBarContainer
        monthScrollContainer = binding.monthContainerScroll

        val createActivityButton = binding.createActivityBtn
        createActivityButton.setOnClickListener {
            val intent = Intent(this.activity, CreateActivity::class.java)
            startActivity(intent)
        }

        upcomingActivitiesViewModel.getUpcomingActivities()

        val currentMonth = dateToday.monthValue

        val user = FirebaseAuth.getInstance().currentUser

        upcomingActivitiesViewModel.activitiesResponse.observe(viewLifecycleOwner, Observer {

            displayProgressCircle(false)

            for (i in currentMonth..12)
            {
                val tempDate = LocalDate.of(dateToday.year, i, 1)
                val monthContainerBinding = layoutInflater.let { it1 ->
                    UpcomingActivitiesMonthContainerBinding.inflate(
                        it1
                    )
                }

                monthContainerBinding.monthText.text = tempDate.month.toString()

                val activityList = upcomingActivitiesViewModel.getActivitiesByMonth(i, dateToday)

                if (activityList.isNotEmpty())
                {
                    for (activity in activityList)
                    {
                        val activityBoxSmallBinding = layoutInflater.let { it1 ->
                            ActivityActivitiesBoxSmallBinding.inflate(
                                it1
                            )
                        }

                        val activityStartTime = activity.startTime
                        val dateString =
                            activityStartTime!!.substring(0, activityStartTime.indexOf("T"))
                        val dateParts = dateString.split("-")

                        activityBoxSmallBinding.activityBoxDay.text = dateParts[2]
                        activityBoxSmallBinding.activityBoxTitle.text = activity.title!!.replace("\"", "")

                        if (activity.user?.firebase_id == user?.uid)
                        {
                            activityBoxSmallBinding.activityBoxUserCreated.visibility = View.VISIBLE
                        }

                        activityBoxSmallBinding.activityBox.setOnClickListener {
                            val intent =
                                Intent(this.activity, DisplayActivityInfoActivity::class.java)
                            intent.putExtra("id", activity.id)
                            intent.putExtra("title", activity.title)
                            intent.putExtra("description", activity.description)
                            intent.putExtra("user_id", activity.user?.firebase_id)
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

                        monthContainerBinding.root.addView(activityBoxSmallBinding.root)
                    }

                    monthContainerBinding.card.setOnClickListener {
                        for (c in 1 until monthContainerBinding.root.childCount)
                        {
                            val currentActivityBox = monthContainerBinding.root.getChildAt(c)
                            if (currentActivityBox.visibility == View.GONE) monthContainerBinding.root.getChildAt(
                                c
                            ).visibility = View.VISIBLE
                            else monthContainerBinding.root.getChildAt(c).visibility = View.GONE
                        }
                    }


                }

                monthContainer.addView(monthContainerBinding.root)
            }

        })

        return view
    }

    fun displayProgressCircle(shouldDisplay : Boolean)
    {
        progressBar.visibility = if (shouldDisplay) View.VISIBLE else View.GONE
        monthScrollContainer.visibility = if (shouldDisplay) View.GONE else View.VISIBLE
    }
}