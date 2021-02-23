package com.viveret.pocketn2.view.fragments.challenge

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentListBinding
import com.viveret.pocketn2.view.HasTitle
import com.viveret.pocketn2.view.ListFragmentHelper
import com.viveret.pocketn2.view.adapters.ChallengeAdapter
import com.viveret.pocketn2.view.holders.ChallengeMetaInfoHolder
import com.viveret.tinydnn.data.challenge.ChallengeMetaInfo
import com.viveret.tinydnn.util.async.OnItemSelectedListener

class ChallengeListFragment: androidx.fragment.app.Fragment(), HasTitle {
    private val myHelper = ChallengeHelper()

    override val title: Int = R.string.title_activity_challenges

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentListBinding.inflate(inflater, container, false)
        myHelper.onCreateView(binding.list)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myHelper.onCreate(savedInstanceState)
        if (arguments != null) {
            myHelper.applyArguments(requireArguments())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myHelper.onAttach(context)
    }

    override fun onDetach() {
        myHelper.onDetach()
        super.onDetach()
    }

    private class ChallengeHelper : ListFragmentHelper<ChallengeMetaInfo, ChallengeMetaInfoHolder, ChallengeAdapter, OnItemSelectedListener>(defaultColumnCount = 1, orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
        val myItems = ArrayList<ChallengeMetaInfo>()

        override fun newAdapter(listener: OnItemSelectedListener?): ChallengeAdapter =
                ChallengeAdapter(myItems, listener)

        override fun asValidListener(context: Context): OnItemSelectedListener? {
            this.myItems.clear()
            this.myItems.addAll(DataManager.get(context).challenges.values)
            adapter?.notifyDataSetChanged()
            return context as OnItemSelectedListener
        }

        override fun getListenerClassName(): String = "OnItemSelectedListener"
    }

    companion object {
        fun newInstance(columnCount: Int): ChallengeListFragment {
            val fragment = ChallengeListFragment()
            val args = Bundle()
            args.putInt(ListFragmentHelper.ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}