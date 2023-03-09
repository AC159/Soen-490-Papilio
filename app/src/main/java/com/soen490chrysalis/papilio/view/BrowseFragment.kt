package com.soen490chrysalis.papilio.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.soen490chrysalis.papilio.R
import com.soen490chrysalis.papilio.databinding.ActivityActivitiesBoxBinding
import com.soen490chrysalis.papilio.databinding.FragmentBrowseBinding
import com.soen490chrysalis.papilio.viewModel.BrowseFragmentViewModel
import com.soen490chrysalis.papilio.viewModel.factories.BrowseFragmentViewModelFactory

class BrowseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private lateinit var browseFragmentViewModel: BrowseFragmentViewModel

    private lateinit var progressCircleContainer: LinearLayout
    private lateinit var progressCircle: ProgressBar

    private lateinit var binding: FragmentBrowseBinding
    private lateinit var searchBar: EditText
    private lateinit var backButton: ImageButton
    private lateinit var layoutActivityList: ScrollView
    private lateinit var activityContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBrowseBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val browseFragmentVMFactory = BrowseFragmentViewModelFactory()
        browseFragmentViewModel =
            ViewModelProvider(this, browseFragmentVMFactory)[BrowseFragmentViewModel::class.java]

        layoutActivityList = binding.activityContainer
        activityContainer = binding.activityList

        progressCircle = binding.progressBar1
        progressCircleContainer = binding.progressBarContainer
        searchBar = binding.searchBar
        backButton = binding.backButton

        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                displayProgressCircle(true)
                context?.let { it1 -> hideKeyboardFrom(it1, view) }
                browseFragmentViewModel.searchActivities(searchBar.text.toString());
            }
            true
        }

        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        browseFragmentViewModel.activitiesResponse.observe(viewLifecycleOwner, Observer {

            val noResultsBox = view.findViewById<MaterialCardView>(R.id.no_activity_found_box)
            noResultsBox.visibility = View.GONE
            progressCircle.visibility = View.VISIBLE

            displayProgressCircle(false)

            val data = browseFragmentViewModel.activitiesResponse.value

            activityContainer.removeAllViews()

            val layoutInflater: LayoutInflater? = activity?.layoutInflater

            if (data?.count == "0") {
                displayProgressCircle(true)
                noResultsBox.visibility = View.VISIBLE
                progressCircle.visibility = View.GONE
            } else {
                for (activity in browseFragmentViewModel.activitiesResponse.value?.rows!!) {

                    val activityBoxBinding = layoutInflater?.let { it1 ->
                        ActivityActivitiesBoxBinding.inflate(
                            it1
                        )
                    }

                    if (activityBoxBinding != null) {
                        val activityBoxImage = activityBoxBinding.activityBoxImage
                        val activityBoxTitle = activityBoxBinding.activityBoxTitle
                        val activityBoxDesc = activityBoxBinding.activityBoxAddress
                        activityBoxBinding.activityBoxStartTime.visibility = View.GONE

                        if (activity.images != null) {
                            Glide.with(this).load(activity.images).into(activityBoxImage)
                        }

                        activityBoxTitle.text = activity.title
                        activityBoxDesc.text = activity.description

                        activityBoxBinding.activityBox.setOnClickListener {
                            activity.id?.toInt()
                                ?.let { it1 -> browseFragmentViewModel.getActivity(it1) }
                        }

                        activityContainer.addView(activityBoxBinding.activityBox)
                    }
                }
            }
        })

        browseFragmentViewModel.activityResponse.observe(viewLifecycleOwner, Observer {

            val activity = browseFragmentViewModel.activityResponse.value?.activity;

            val intent = Intent(this.activity, DisplayActivityInfoActivity::class.java)
            intent.putExtra("id", activity?.id)
            intent.putExtra("title", activity?.title)
            intent.putExtra("description", activity?.description)
            intent.putExtra(
                "individualCost",
                if (activity?.costPerIndividual == "0") "FREE" else ("$" + activity?.costPerIndividual + "/person")
            )
            intent.putExtra(
                "groupCost",
                if (activity?.costPerGroup == "0") "FREE" else ("$" + activity?.costPerGroup + "/group")
            )
            intent.putExtra("location", activity?.address)

            if (activity?.images != null && activity.images.isNotEmpty()) {
                intent.putExtra("images", true)
                var x = 0
                Log.d("Size", activity.images.size.toString())
                for (i in activity.images) {
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

        })

        searchBar.requestFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)

    }

    fun displayProgressCircle(shouldDisplay: Boolean) {
        progressCircleContainer.visibility = if (shouldDisplay) View.VISIBLE else View.GONE
        layoutActivityList.visibility = if (shouldDisplay) View.GONE else View.VISIBLE
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
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
         * @return A new instance of fragment BrowseFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): BrowseFragment {
            val fragment = BrowseFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}