package com.soen490chrysalis.papilio.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soen490chrysalis.papilio.databinding.FragmentActivitiesBinding
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
class ActivitiesFragment : Fragment()
{
    // TODO: Rename and change types of parameters
    private var param1 : String? = null
    private var param2 : String? = null

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding : FragmentActivitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View?
    {
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        val view = binding.root
        val createActivityButton = view.activities_create_activity_button

        createActivityButton?.setOnClickListener {
            val intent = Intent(this.activity, CreateActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    companion object
    {
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
        fun newInstance(param1 : String, param2 : String) =
            ActivitiesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}