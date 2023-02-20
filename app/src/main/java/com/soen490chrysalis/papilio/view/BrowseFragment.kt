package com.soen490chrysalis.papilio.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.soen490chrysalis.papilio.R
import com.soen490chrysalis.papilio.viewModel.BrowseFragmentViewModel
import com.soen490chrysalis.papilio.viewModel.HomeFragmentViewModel
import com.soen490chrysalis.papilio.viewModel.factories.BrowseFragmentViewModelFactory
import com.soen490chrysalis.papilio.viewModel.factories.HomeFragmentViewModelFactory


/**
 * A simple [Fragment] subclass.
 * Use the [BrowseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BrowseFragment : Fragment()
{
    // TODO: Rename and change types of parameters
    private var mParam1 : String? = null
    private var mParam2 : String? = null

    private lateinit var browseFragmentViewModel : BrowseFragmentViewModel

    private lateinit var progressCircleContainer : LinearLayout
    private lateinit var progressCircle : ProgressBar

    private lateinit var searchBar : EditText
    private lateinit var searchButton : ImageButton

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (arguments != null)
        {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val browseFragmentVMFactory = BrowseFragmentViewModelFactory()
        browseFragmentViewModel = ViewModelProvider(this, browseFragmentVMFactory)[BrowseFragmentViewModel::class.java]

        progressCircle = view.findViewById(R.id.progressBar1)
        progressCircleContainer = view.findViewById(R.id.progressBarContainer)
        searchBar = view.findViewById(R.id.search_bar)
        searchButton = view.findViewById(R.id.search_button)

        searchButton.setOnClickListener {
            displayProgressCircle(true)
            context?.let { it1 -> hideKeyboardFrom(it1, view) }
            browseFragmentViewModel.searchActivities(searchBar.text.toString());
        }

    }


    fun displayProgressCircle(shouldDisplay : Boolean)
    {
        progressCircleContainer.visibility = if(shouldDisplay) View.VISIBLE else View.GONE
    }


    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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
         * @return A new instance of fragment BrowseFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1 : String?, param2 : String?) : BrowseFragment
        {
            val fragment = BrowseFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}