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

class HomeFragment : Fragment()
{
    private lateinit var homeFragmentViewModel : HomeFragmentViewModel
    private lateinit var activityList : List<ActivityObject>
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : FeedAdapter

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

    override fun onViewCreated(view : View, savedInstanceState : Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val homeFragmentVMFactory = HomeFragmentViewModelFactory()
        homeFragmentViewModel =
            ViewModelProvider(this, homeFragmentVMFactory)[HomeFragmentViewModel::class.java]

        homeFragmentViewModel.getAllActivities("1", "20")

        homeFragmentViewModel.activityResponse.observe(viewLifecycleOwner, Observer {

            activityList = homeFragmentViewModel.activityResponse.value!!.rows
            Log.d("getAllActivities", activityList.toString())
            val layoutManager = LinearLayoutManager(context)
            recyclerView = view.findViewById(R.id.activityFeedRV)
            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(false)
            adapter = FeedAdapter(activityList, this)
            recyclerView.adapter = adapter
            adapter.setOnItemClickListener(object : FeedAdapter.OnItemClickListener
            {
                override fun onItemClick(position : Int)
                {
                    val intent = Intent(activity, DisplayActivityInfoActivity::class.java)
                    intent.putExtra("id", activityList[position].id)
                    intent.putExtra("title", activityList[position].title)
                    intent.putExtra("description", activityList[position].description)
                    intent.putExtra("individualCost", activityList[position].costPerIndividual)
                    intent.putExtra("groupCost", activityList[position].costPerGroup)
                    intent.putExtra("location", activityList[position].address)

                    if (activityList[position].images != null && activityList[position].images?.isNotEmpty() == true)
                    {
                        intent.putExtra("images", true)
                        var x = 0
                        Log.d("Size", activityList[position].images!!.size.toString())
                        for(i in activityList[position].images!!){
                            intent.putExtra("images$x", i)
                            x++

                        }
                        if(x != 5){
                            for(e in x until 5){
                                intent.putExtra("images$e", "")
                                x++
                            }
                        }
                    }
                    else
                    {
                        intent.putExtra("images", false)
                    }

                    startActivity(intent)
                }
            })
        })
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
