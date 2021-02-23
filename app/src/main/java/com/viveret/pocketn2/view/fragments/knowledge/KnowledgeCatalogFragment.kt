package com.viveret.pocketn2.view.fragments.knowledge

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.ActivityChallengeBinding
import com.viveret.pocketn2.databinding.FragmentKnowledgeSourceBinding
import com.viveret.pocketn2.databinding.FragmentListBinding
import com.viveret.pocketn2.view.ListFragmentHelper
import com.viveret.pocketn2.view.adapters.KnowledgeAdapter
import com.viveret.pocketn2.view.fragments.project.ProjectBottomSheetFragment
import com.viveret.pocketn2.view.holders.KnowledgeItemViewHolder
import com.viveret.tinydnn.data.knowledge.KnowledgeCatalogItem
import com.viveret.tinydnn.model.INetworkModelWithWeights
import com.viveret.tinydnn.util.async.Dismissible
import com.viveret.tinydnn.util.async.OnItemSelectedListener

class KnowledgeCatalogFragment: ProjectBottomSheetFragment<FragmentListBinding>(), Dismissible {
    override val initialExpansionState: Int? = null
    private val myHelper = KnowledgeHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myHelper.onCreate(savedInstanceState)
        if (arguments != null) {
            myHelper.applyArguments(requireArguments())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myHelper.onAttach(context, projectProvider?.project?.get()!!)
    }

    override fun onDetach() {
        myHelper.onDetach()
        super.onDetach()
    }

    private class KnowledgeHelper(val dismissible: Dismissible) : ListFragmentHelper<KnowledgeCatalogItem, KnowledgeItemViewHolder, KnowledgeAdapter, OnItemSelectedListener>(defaultColumnCount = 1, orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
        private lateinit var networkModelWithWeights: INetworkModelWithWeights
        private lateinit var myItems: Collection<KnowledgeCatalogItem>

        override fun newAdapter(listener: OnItemSelectedListener?): KnowledgeAdapter =
                KnowledgeAdapter(myItems.toList(), listener, dismissible)

        override fun asValidListener(context: Context): OnItemSelectedListener? {
            this.myItems = DataManager.get(context).knowledgeCatalog.values.filter { x -> x.isMatch(networkModelWithWeights) }
            return context as OnItemSelectedListener
        }

        fun onAttach(context: Context, networkModelWithWeights: INetworkModelWithWeights) {
            this.networkModelWithWeights = networkModelWithWeights
            super.onAttach(context)
        }

        override fun getListenerClassName(): String = "OnItemSelectedListener"
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SandboxFragment.
         */
        fun newInstance(columnCount: Int): KnowledgeCatalogFragment {
            val f = KnowledgeCatalogFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            //args.putString("start", PATH_SELECT_SOURCE)
            f.arguments = args
            return f
        }
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentListBinding? {
        val binding = FragmentListBinding.inflate(inflater, container, false)

        fragmentRootView = binding.root
        myHelper.onCreateView(binding.list)

        return binding
    }
}