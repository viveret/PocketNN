package com.viveret.pocketn2.view.fragments.data

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viveret.pocketn2.view.ListFragmentHelper
import com.viveret.pocketn2.view.adapters.DataAdapter
import com.viveret.pocketn2.view.fragments.data.fmt.DataFormatView
import com.viveret.pocketn2.view.holders.DataFormatViewHolder
import com.viveret.tinydnn.basis.Vect
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.util.async.Dismissible
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import java.util.*

class ListDataViewerFragment: DataViewerFragment() {
    private var helper: DataBrowseHelper? = null
    private lateinit var dataValueStream: DataSliceReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper?.onCreate(savedInstanceState)
    }

    override fun onCreateArguments(arguments: Bundle) {
        super.onCreateArguments(arguments)
        if (arguments.containsKey("streamId")) {
            val streamId = UUID.fromString(arguments.getString("streamId"))
            this.dataValueStream = dm.getDataValueStream(streamId)
        }

        this.helper = DataBrowseHelper(this.dataValueStream, viewForData, this, this)
        helper?.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)!!
        helper?.onCreateView(_binding!!.list)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        helper?.onAttach(context)
    }

    override fun onDetach() {
        helper?.onDetach()
        helper = null
        super.onDetach()
    }

    private class DataBrowseHelper(val valuesFile: DataSliceReader, val dataFormatView: DataFormatView, val exceptionListener: OnItemSelectedListener, val dismissible: Dismissible?) : ListFragmentHelper<Vect, DataFormatViewHolder, DataAdapter, OnItemSelectedListener>() {
        override fun newAdapter(listener: OnItemSelectedListener?): DataAdapter =
                DataAdapter(valuesFile, dataFormatView, listener, exceptionListener, dismissible)

        override fun asValidListener(context: Context): OnItemSelectedListener? =
                context as OnItemSelectedListener

        override fun getListenerClassName(): String = "OnItemSelectedListener"
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SandboxFragment.
         */
        fun newInstance(streamId: UUID, viewToUse: DataFormatView): ListDataViewerFragment {
            val f = ListDataViewerFragment()
            val args = Bundle()
            args.putString("streamId", streamId.toString())
            args.putInt("viewId", viewToUse.nameResId)
            f.arguments = args
            return f
        }
    }
}