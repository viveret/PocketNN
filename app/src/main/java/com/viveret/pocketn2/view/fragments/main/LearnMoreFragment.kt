package com.viveret.pocketn2.view.fragments.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentAboutBinding
import com.viveret.pocketn2.view.HasTitle
import com.viveret.pocketn2.view.adapters.LearnMoreAdapter
import java.util.*
import java.util.regex.Pattern

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnLearnMoreSectionSelectedListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class LearnMoreFragment : androidx.fragment.app.Fragment(), HasTitle {
    private var myText: String = ""
    private var mListener: OnLearnMoreSectionSelectedListener? = null
    private val myItems = ArrayList<LearnMoreSection>()
    private var recyclerView: androidx.recyclerview.widget.RecyclerView? = null
    private var myAdapter: LearnMoreAdapter? = null
    private var titleResId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {
            titleResId = args.getInt(ARG_TITLE)
            myText = args.getString(ARG_TEXT)!!
            val sections = Pattern.compile("\\s+---+\\s+").split(myText)
            for (section in sections) {
                val idEnd = section.indexOf(':')
                val id = section.substring(0, idEnd)
                val content = section.substring(idEnd + 1)
                myItems.add(LearnMoreSection(id, content))
            }
            //myAdapter.notifyDataSetChanged();
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentAboutBinding.inflate(inflater, container, false)

        // Set the adapter
        recyclerView = binding.list
        binding.list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(binding.list.context)
        myAdapter = LearnMoreAdapter(myItems, mListener)
        binding.list.adapter = myAdapter

        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLearnMoreSectionSelectedListener) {
            mListener = context
        } else {
            throw RuntimeException(requireContext().toString() + " must implement OnLearnMoreSectionSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override val title: Int
        get() = titleResId!!

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnLearnMoreSectionSelectedListener {
        fun onLearnMoreSectionSelected(itemId: String)
    }

    inner class LearnMoreSection(var myId: String, var mySectionText: String)

    companion object {
        private val ARG_TEXT = "text"
        private val ARG_TITLE = "title"

        fun newInstance(text: String, title: Int): LearnMoreFragment {
            val fragment = LearnMoreFragment()
            val args = Bundle()
            args.putString(ARG_TEXT, text)
            args.putInt(ARG_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }
}
