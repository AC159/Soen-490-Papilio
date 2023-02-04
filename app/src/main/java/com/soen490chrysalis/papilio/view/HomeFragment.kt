package com.soen490chrysalis.papilio.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soen490chrysalis.papilio.R
import com.soen490chrysalis.papilio.services.network.responses.ActivityObject
import com.soen490chrysalis.papilio.view.dialogs.FeedAdapter
import com.soen490chrysalis.papilio.viewModel.HomeFragmentViewModel
import com.soen490chrysalis.papilio.viewModel.factories.HomeFragmentViewModelFactory

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class ActivityObject2(var title: String, var address: String, var startTime: String)

class HomeFragment : Fragment()
{
    private lateinit var homeFragmentViewModel : HomeFragmentViewModel
    private lateinit var activityList: List<ActivityObject>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FeedAdapter

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View?
    {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        activityList = listOf<ActivityObject>()
//        (
//            ActivityObject2("something","Montreal", "2032-01-07"),
//            ActivityObject2("Soccer","Laval", "2022-01-07"),
//            ActivityObject2("hello","Laval", "2032-06-23"),
//            ActivityObject2("please","Montreal", "1999-01-05"),
//
//        )

        val homeFragmentVMFactory = HomeFragmentViewModelFactory()
        homeFragmentViewModel = ViewModelProvider(this, homeFragmentVMFactory)[HomeFragmentViewModel::class.java]

        homeFragmentViewModel.getAllActivities("1", "20")

        homeFragmentViewModel.activityResponse.observe(viewLifecycleOwner, Observer{

            activityList = homeFragmentViewModel.activityResponse.value!!.rows
            Log.d("getAllActivities", activityList.toString())
            val layoutManager = LinearLayoutManager(context)
            recyclerView = view.findViewById(R.id.activityFeedRV)
            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(false)
            adapter = FeedAdapter(activityList)
            recyclerView.adapter = adapter
            adapter.setOnItemClickListener(object : FeedAdapter.onItemClickListener{
                override fun onItemClick(position: Int) {

                    val intent = Intent(getActivity(), DisplayAcitivityInfoActivity::class.java)
                    intent.putExtra("title", activityList[position].title)
                    intent.putExtra("description", activityList[position].description)
                    intent.putExtra("individualCost", activityList[position].costPerIndividual)
                    intent.putExtra("groupCost", activityList[position].costPerGroup)
                    intent.putExtra("location", activityList[position].address)
                    if(activityList[position].images != null || activityList[position].images?.size != 0){
                        intent.putExtra("images", activityList[position].images?.get(0))
                        Log.d("hello ",activityList[position].images?.get(0).toString())
                    }else{
                        intent.putExtra("images", "")
                    }

                    startActivity(intent)
                }
            })

        })







//        for (iCardView in 0..2) {
//            val idView = resources.getIdentifier("activity_box$iCardView", "id", requireContext().packageName)
//            val eventView: View = view.findViewById(idView)
//            eventView.setOnClickListener { view -> SwitchToDisplayActivityInfo(view)}
//        }
    }


    fun SwitchToDisplayActivityInfo(view:View?){
        val intent = Intent (getActivity(), DisplayAcitivityInfoActivity::class.java)
        getActivity()?.startActivity(intent)
    }



    companion object
    {
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
        fun newInstance(param1 : String?, param2 : String?) : HomeFragment
        {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }


}

