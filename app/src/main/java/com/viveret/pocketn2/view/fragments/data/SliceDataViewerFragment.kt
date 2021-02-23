package com.viveret.pocketn2.view.fragments.data

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viveret.pocketn2.view.ListFragmentHelper
import com.viveret.pocketn2.view.adapters.DataViewAdapter
import com.viveret.pocketn2.view.fragments.data.fmt.DataFormatView
import com.viveret.pocketn2.view.holders.DataFormatViewHolder
import com.viveret.tinydnn.basis.DataSlice
import com.viveret.tinydnn.util.async.Dismissible
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import java.util.*
import kotlin.collections.ArrayList

class SliceDataViewerFragment: DataViewerFragment() {
    private lateinit var dataSlice: DataSlice
    private var helper: DataViewHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper?.onCreate(savedInstanceState)
    }

    override fun onCreateArguments(arguments: Bundle) {
        super.onCreateArguments(arguments)
        val sliceId = UUID.fromString(arguments.getString("item"))
        this.dataSlice = dm.getData(sliceId)

        this.helper = DataViewHelper(this.dataSlice, this, this)
        helper?.myItems?.addAll(possibleViews.filter { x -> x.supportsData(this.dataSlice) })
        helper?.onAttach(context)
        if (this.inflatedView != null) {
            helper?.onCreateView(_binding!!.list)
        }
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
        super.onDetach()
    }

    private class DataViewHelper(val dataSlice: DataSlice, val exceptionListener: OnItemSelectedListener, val dismissible: Dismissible?) : ListFragmentHelper<DataFormatView, DataFormatViewHolder, DataViewAdapter, OnItemSelectedListener>() {
        val myItems = ArrayList<DataFormatView>()

        override fun newAdapter(listener: OnItemSelectedListener?): DataViewAdapter =
                DataViewAdapter(dataSlice, myItems, listener, dismissible)

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
        fun newInstance(sliceId: UUID, viewToUse: DataFormatView?): SliceDataViewerFragment {
            val f = SliceDataViewerFragment()
            val args = Bundle()
            args.putString("item", sliceId.toString())
            if (viewToUse != null) {
                args.putInt("viewId", viewToUse.nameResId)
            }
            f.arguments = args
            return f
        }
    }
}