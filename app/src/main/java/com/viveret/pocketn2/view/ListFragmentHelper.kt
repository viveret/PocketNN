package com.viveret.pocketn2.view

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class ListFragmentHelper<T, TViewHolder, TViewAdapter, TListener> where TViewHolder : RecyclerView.ViewHolder, TViewAdapter : RecyclerView.Adapter<TViewHolder> {
    private var orientation: Int
    lateinit var listView: RecyclerView
    private var mColumnCount: Int
    private var mListener: TListener? = null
    protected var adapter: TViewAdapter? = null

    abstract fun newAdapter(listener: TListener?): TViewAdapter

    abstract fun asValidListener(context: Context): TListener?

    abstract fun getListenerClassName(): String

    constructor(defaultColumnCount: Int = 2, orientation: Int = LinearLayoutManager.VERTICAL) {
        this.mColumnCount = defaultColumnCount
        this.orientation = orientation
    }

    fun applyArguments(arguments: Bundle) {
        mColumnCount = arguments.getInt(ARG_COLUMN_COUNT)
    }

    fun onCreate(savedInstanceState: Bundle?) {
    }

    fun onCreateView(view: RecyclerView) {
        // Set the adapter
        this.listView = view
        val context = listView.context
        listView.layoutManager = if (mColumnCount <= 1) {
            LinearLayoutManager(context, orientation, false)
        } else {
            androidx.recyclerview.widget.GridLayoutManager(context, mColumnCount, orientation, false)
        }
        listView.adapter = adapter
    }

    fun onAttach(context: Context?) {
        mListener = this.asValidListener(context!!)
        if (this.mListener == null) {
            throw RuntimeException("$context must implement ${this.getListenerClassName()}")
        }
        adapter = newAdapter(mListener)
    }

    fun onDetach() {
        mListener = null
        adapter = null
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
    }
}