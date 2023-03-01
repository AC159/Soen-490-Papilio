package com.soen490chrysalis.papilio.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import com.soen490chrysalis.papilio.R
import com.soen490chrysalis.papilio.services.network.responses.ActivityObject
import com.soen490chrysalis.papilio.view.dialogs.EventDate
import com.soen490chrysalis.papilio.view.dialogs.FeedAdapter
import com.soen490chrysalis.papilio.viewModel.HomeFragmentViewModel
import com.soen490chrysalis.papilio.viewModel.factories.HomeFragmentViewModelFactory

class HomeFragment : Fragment() {
    private lateinit var homeFragmentViewModel: HomeFragmentViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FeedAdapter
    private var isRecyclerViewInitialized = false
    private var isLoading = false
    private lateinit var progressBar: ProgressBar
    private lateinit var filterButton: ImageButton

    // todo: these variables should be in the view model not in the fragment or activity
    private lateinit var activityList: MutableList<ActivityObject>
    private lateinit var addActivityList: List<ActivityObject>
    private var totalPage: Int = 1
    private var currentPage: Int = 1
    private var pageSize: Int = 5


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.feed_progress_Bar)
        recyclerView = view.findViewById(R.id.activityFeedRV)
        filterButton = view.findViewById(R.id.filter_button)

        val homeFragmentVMFactory = HomeFragmentViewModelFactory()
        homeFragmentViewModel =
            ViewModelProvider(this, homeFragmentVMFactory)[HomeFragmentViewModel::class.java]

        // Fetch the first set of activities to display
        homeFragmentViewModel.getAllActivities(currentPage.toString(), pageSize.toString())

        homeFragmentViewModel.activityResponse.observe(viewLifecycleOwner, Observer {
            Log.d("RECEIVED NEW DATA", it.rows.size.toString())

            if (isRecyclerViewInitialized) {
                val unfilteredList = it.rows
                val finalList = mutableListOf<ActivityObject>()

                for (activity in unfilteredList) {
                    if (homeFragmentViewModel.filterActivity(activity)) {
                        finalList.add(activity)
                    }
                }

                addActivityList = finalList

                val index = activityList.size
                addActivityList.forEach { item ->
                    activityList.add(item)
                }

                Log.d(
                    "NOTIFYING RECYCLER VIEW OF NEWLY INSERTED DATA", activityList.size.toString()
                )
                adapter.notifyItemRangeInserted(index - 1, addActivityList.size)
                isLoading = false
                progressBar.visibility = View.GONE
            }

            if (!isRecyclerViewInitialized) {
                // Extract the received data

                val unfilteredList = it.rows
                val finalList = mutableListOf<ActivityObject>()

                for (activity in unfilteredList) {
                    if (homeFragmentViewModel.filterActivity(activity)) {
                        finalList.add(activity)
                    }
                }

                activityList = finalList // initialize the activityList variable

                // Initialize the recycler view only once
                val layoutManager = LinearLayoutManager(context)
                recyclerView.layoutManager = layoutManager
                recyclerView.itemAnimator = null
                recyclerView.setHasFixedSize(false)
                adapter = FeedAdapter(activityList, this)
                recyclerView.adapter = adapter

                itemClickListener()

                totalPage = it.totalPages.toInt()
                Log.d("TOTAL NBR OF PAGES", totalPage.toString())

                // important to set this variable because we only want to initialize the recycler view once
                isRecyclerViewInitialized = true
            }
        })

        scrollListener()

        filterButton.setOnClickListener {

            val c: Calendar = Calendar.getInstance()
            var startDate = homeFragmentViewModel.oldestDate
            var endDate = homeFragmentViewModel.furthestDate


            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Set Filter Options")

            val inflater = this.layoutInflater
            val dialogLayout = inflater.inflate(R.layout.filters, null)
            val individualCostSlider =
                dialogLayout.findViewById<RangeSlider>(R.id.slider_individual_cost)
            val groupCostSlider = dialogLayout.findViewById<RangeSlider>(R.id.slider_group_cost)
            val startDateButton = dialogLayout.findViewById<TextView>(R.id.select_start_date_btn)
            val endDateButton = dialogLayout.findViewById<TextView>(R.id.select_end_date_btn)

            var individualCostSliderValues = mutableListOf<Float>(0f, 1000f)
            var groupCostSliderValues = mutableListOf<Float>(0f, 1000f)

            individualCostSlider.addOnChangeListener { _, _, _ ->
                individualCostSliderValues = individualCostSlider.values
            }

            groupCostSlider.addOnChangeListener { _, _, _ ->
                groupCostSliderValues = groupCostSlider.values
            }

            startDateButton.setOnClickListener {
                val dateDialog = DatePickerDialog(requireContext())
                dateDialog.datePicker.minDate = c.timeInMillis
                dateDialog.show()

                dateDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(View.OnClickListener {
                        startDate = EventDate(
                            dateDialog.datePicker.year,
                            dateDialog.datePicker.month,
                            dateDialog.datePicker.dayOfMonth
                        )
                        startDateButton.text =
                            "${startDate.month + 1}/${startDate.day}/${startDate.year}"
                        dateDialog.dismiss()
                    })
            }

            endDateButton.setOnClickListener {
                val dateDialog = DatePickerDialog(requireContext())
                dateDialog.datePicker.minDate = c.timeInMillis
                dateDialog.show()

                dateDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(View.OnClickListener {
                        endDate = EventDate(
                            dateDialog.datePicker.year,
                            dateDialog.datePicker.month,
                            dateDialog.datePicker.dayOfMonth
                        )
                        endDateButton.text = "${endDate.month + 1}/${endDate.day}/${endDate.year}"
                        dateDialog.dismiss()
                    })
            }

            builder.setView(dialogLayout)

            builder.setPositiveButton(
                "Save",
                DialogInterface.OnClickListener { _: DialogInterface, _: Int ->
                    homeFragmentViewModel.SetFilter(
                        HomeFragmentViewModel.FilterOptions(
                            individualCostSliderValues, groupCostSliderValues, startDate, endDate
                        )
                    )
                    clearFeed()

                })

            builder.setNeutralButton("Remove All Filters",
                DialogInterface.OnClickListener { _: DialogInterface, _: Int ->
                    homeFragmentViewModel.ResetFilter()
                    clearFeed()
                })

            builder.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                    run {
                        // When the "Cancel" button is pressed, simply dismiss the dialog
                        dialog.dismiss()
                    }
                })


            val dialog = builder.create()

            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.RED)

        }

    }

    companion object {
        // Not sure if this should be removed
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    private fun itemClickListener() {
        adapter.setOnItemClickListener(object : FeedAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(activity, DisplayActivityInfoActivity::class.java)
                intent.putExtra("id", activityList[position].id)
                intent.putExtra("title", activityList[position].title)
                intent.putExtra("description", activityList[position].description)
                intent.putExtra(
                    "individualCost",
                    if (activityList[position].costPerIndividual == "0") "FREE" else ("$" + activityList[position].costPerIndividual + "/person")
                )
                intent.putExtra(
                    "groupCost",
                    if (activityList[position].costPerGroup == "0") "FREE" else ("$" + activityList[position].costPerGroup + "/group")
                )
                intent.putExtra("location", activityList[position].address)

                if (activityList[position].images != null && activityList[position].images?.isNotEmpty() == true) {
                    intent.putExtra("images", true)
                    var x = 0
                    Log.d("Size", activityList[position].images!!.size.toString())
                    for (i in activityList[position].images!!) {
                        intent.putExtra("images$x", i)
                        x++
                    }
                    if (x != 5) {
                        for (e in x until 5) {
                            intent.putExtra("images$e", "")
                            x++
                        }
                    }
                } else intent.putExtra("images", false)

                startActivity(intent)
            }
        })
    }

    private fun scrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == (activityList.size - 1)) {
                        // bottom of list!
                        if (currentPage <= totalPage) {
                            ++currentPage
                            // fetch more activities upon button click
                            homeFragmentViewModel.getAllActivities(
                                currentPage.toString(), pageSize.toString()
                            )
                        }
                        isLoading = true
                        progressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun clearFeed() {
        isRecyclerViewInitialized = false
        val size = activityList.size
        activityList.clear()
        adapter.notifyItemRangeRemoved(0, size)
        currentPage = 1
        homeFragmentViewModel.getAllActivities(currentPage.toString(), pageSize.toString())
    }
}

