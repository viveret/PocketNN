package com.viveret.pocketn2.view.fragments.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentTemplatennListBinding
import com.viveret.pocketn2.view.HasTitle
import com.viveret.pocketn2.view.ListFragmentHelper
import com.viveret.pocketn2.view.adapters.TemplateNNAdapter
import com.viveret.pocketn2.view.holders.TemplateNNViewHolder
import com.viveret.tinydnn.project.TemplateNetwork
import com.viveret.tinydnn.util.async.Dismissible
import com.viveret.tinydnn.util.async.OnItemSelectedListener

/**
 * A fragment representing a list of Items.
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
class TemplateNNFragment : Fragment(), HasTitle {
    private val templates = listOf(TemplateNetwork.MnistRnn(),
            TemplateNetwork.MnistRnn2(),
            TemplateNetwork.MultiLayerPerceptron(),
            TemplateNetwork.BinaryGate(),
            TemplateNetwork.Cifar10Classification())
    //myHelper.myItems.add(TemplateNetwork.MovieCategorizer())
    //myHelper.myItems.add(TemplateNetwork.ReviewSentimentAnalysis())

    private val myHelper = TemplateNNHelper(templates, null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTemplatennListBinding.inflate(inflater, container, false)
        myHelper.onCreateView(binding.list)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myHelper.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myHelper.onAttach(context)
        // requireActivity().setTitle()
    }

    override fun onDetach() {
        myHelper.onDetach()
        super.onDetach()
    }

    private class TemplateNNHelper(val myItems: List<TemplateNetwork>, val dismissible: Dismissible?) :
            ListFragmentHelper<TemplateNetwork, TemplateNNViewHolder, TemplateNNAdapter, OnItemSelectedListener>() {
        override fun newAdapter(listener: OnItemSelectedListener?): TemplateNNAdapter =
                TemplateNNAdapter(myItems, listener)

        override fun asValidListener(context: Context): OnItemSelectedListener? =
                context as OnItemSelectedListener

        override fun getListenerClassName(): String = "OnItemSelectedListener<TemplateNetwork>"
    }

    companion object {
        fun newInstance(columnCount: Int): TemplateNNFragment {
            val fragment = TemplateNNFragment()
            val args = Bundle()
            args.putInt(ListFragmentHelper.ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }

    override val title: Int = R.string.title_template_nn
}
