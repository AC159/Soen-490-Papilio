package com.soen490chrysalis.papilio.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.soen490chrysalis.papilio.R
import com.soen490chrysalis.papilio.databinding.ActivityActivitiesBoxBinding
import com.soen490chrysalis.papilio.databinding.FragmentFavoriteActivitiesBinding
import com.soen490chrysalis.papilio.viewModel.FavoriteActivitiesViewModel
import com.soen490chrysalis.papilio.viewModel.factories.FavoriteActivitiesViewModelFactory
import kotlinx.android.synthetic.main.fragment_activities.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PapilioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteActivitiesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var favoriteActivitiesViewModel: FavoriteActivitiesViewModel

    private lateinit var progressCircle: ProgressBar
    private lateinit var progressCircleContainer: LinearLayout
    private lateinit var layoutActivityList: ScrollView
    private lateinit var activityContainer: LinearLayout
    private var shouldRefreshOnResume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentFavoriteActivitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteActivitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val view = binding.root
        progressCircle = binding.progressBar1
        activityContainer = binding.activityList
        layoutActivityList = binding.activityContainer
        progressCircleContainer = binding.progressBarContainer

        val favoriteActivitiesVMFactory = FavoriteActivitiesViewModelFactory()
        favoriteActivitiesViewModel =
            ViewModelProvider(
                this,
                favoriteActivitiesVMFactory
            )[FavoriteActivitiesViewModel::class.java]

        // Fetch all activities favorited by the user
        favoriteActivitiesViewModel.getFavoriteActivities()

        favoriteActivitiesViewModel.activitiesResponse.observe(viewLifecycleOwner, Observer {

            val noResultsBox = view.findViewById<MaterialCardView>(R.id.no_activity_found_box)
            noResultsBox.visibility = View.GONE
            progressCircle.visibility = View.VISIBLE

            displayProgressCircle(false)

            val data = favoriteActivitiesViewModel.activitiesResponse.value

            activityContainer.removeAllViews()

            val layoutInflater: LayoutInflater? = activity?.layoutInflater

            if (data?.count == "0") {
                displayProgressCircle(true)
                noResultsBox.visibility = View.VISIBLE
                progressCircle.visibility = View.GONE
            } else {
                for (activity in favoriteActivitiesViewModel.activitiesResponse.value?.activities!!) {

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

                        if (activity.images != null && activity.images.isNotEmpty()) {
                            Glide.with(this).load(activity.images[0]).into(activityBoxImage)
                        }

                        activityBoxTitle.text = activity.title
                        activityBoxDesc.text = activity.description

                        activityBoxBinding.activityBox.setOnClickListener {

                            val intent =
                                Intent(getActivity(), DisplayActivityInfoActivity::class.java)
                            intent.putExtra("id", activity.id)
                            intent.putExtra("title", activity.title)
                            intent.putExtra("description", activity.description)
                            intent.putExtra(
                                "individualCost",
                                if (activity.costPerIndividual == "0") "FREE" else ("$" + activity.costPerIndividual + "/person")
                            )
                            intent.putExtra(
                                "groupCost",
                                if (activity.costPerGroup == "0") "FREE" else ("$" + activity.costPerGroup + "/group")
                            )
                            intent.putExtra("location", activity.address)
                            intent.putExtra("closed", activity.closed)

                            if (activity.images != null && activity.images.isNotEmpty()) {
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
                            intent.putExtra("isFavorited", true)

                            displayProgressCircle(true)

                            startActivity(intent)

                        }

                        activityContainer.addView(activityBoxBinding.activityBox)
                    }
                }
            }
        })
    }

    fun displayProgressCircle(shouldDisplay: Boolean) {
        progressCircleContainer.visibility = if (shouldDisplay) View.VISIBLE else View.GONE
        layoutActivityList.visibility = if (shouldDisplay) View.GONE else View.VISIBLE
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PapilioFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ActivitiesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}