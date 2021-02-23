package com.viveret.pocketn2.view.fragments.data

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentDataSuiteDetailBinding
import com.viveret.pocketn2.view.ListFragmentHelper
import com.viveret.pocketn2.view.adapters.DataAdapter
import com.viveret.pocketn2.view.fragments.data.fmt.DataFormatView
import com.viveret.pocketn2.view.holders.DataFormatViewHolder
import com.viveret.tinydnn.basis.*
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.util.async.Dismissible
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult
import java.util.*

/**
 * A fragment representing a single ObjectItemKindReplaceMe detail screen.
 * This fragment is either contained in a [DataBrowseListActivity]
 * in two-pane mode (on tablets) or a [DataBrowseActivity]
 * on handsets.
 */
class DataSuiteDetailFragment : androidx.fragment.app.Fragment(), OnItemSelectedListener {
    override fun onSelected(item: Any): OnSelectedResult {
        return OnSelectedResult(false) // todo: implement
    }

    private lateinit var dm: DataManager
    private lateinit var myHelperBrowseData: DataBrowseHelper

    /**
     * The dummy content this fragment is presenting.
     */
    private lateinit var item: StreamPackage
    private var itemValues: DataSliceReader? = null
    var project: NeuralNetProject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.dm = DataManager.get(this.requireContext())

        arguments?.let {
            if (it.containsKey(ARG_PROJECT_ID)) {
                val projectId = UUID.fromString(it.getString(ARG_PROJECT_ID)!!)
                project = dm.getPoject(projectId)
            } else {
                throw Exception("Project id required")
            }
            if (it.containsKey(ARG_ITEM_ID) && project != null) {
                val suiteId = UUID.fromString(it.getString(ARG_ITEM_ID))
                item = HostedStreamPackage.fromId(suiteId)

                if (item.isAvailable(DataSource.LocalFile)) {
                    itemValues = item.open(DataSource.LocalFile, true, this.project!!)
                    // toolbar_layout()?.title = item.title
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentDataSuiteDetailBinding.inflate(inflater, container, false)

        if (this.itemValues != null) {
            binding.btnUncache.setOnClickListener {
                dm.deleteSuite(this.item.id)
            }

            item.let {
                binding.detailText.text = it.dataName
                val fmtView = dm.getViewForMime(it.streams.getValue(DataRole.Input).mime)

                val helper = DataBrowseHelper(this.itemValues!!, fmtView, this, null)
                this.myHelperBrowseData = helper
                helper.onAttach(context)
                helper.onCreateView(binding.list)
            }
        } else {
//            rootView.btnUncache.setText(R.string.action_cache)
//            rootView.btnUncache.setOnClickListener {
//                dm.cache(this.item.id)
//            }
        }

        return binding.root
    }

    private class DataBrowseHelper(val stream: DataSliceReader, val dataFormatView: DataFormatView, val exceptionListener: OnItemSelectedListener, val dismissible: Dismissible?) : ListFragmentHelper<Vect, DataFormatViewHolder, DataAdapter, OnItemSelectedListener>() {
        override fun newAdapter(listener: OnItemSelectedListener?): DataAdapter =
                DataAdapter(stream, dataFormatView, listener, exceptionListener, dismissible)

        override fun asValidListener(context: Context): OnItemSelectedListener? =
                context as OnItemSelectedListener

        override fun getListenerClassName(): String = "OnItemSelectedListener"
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"

        const val ARG_PROJECT_ID = "project_id"
    }
}
