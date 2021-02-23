package com.viveret.pocketn2.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentDataViewBinding
import com.viveret.pocketn2.view.fragments.data.fmt.DataFormatView
import com.viveret.pocketn2.view.holders.DataFormatViewHolder
import com.viveret.tinydnn.basis.DataSlice
import com.viveret.tinydnn.project.ProjectController
import com.viveret.tinydnn.util.async.Dismissible
import com.viveret.tinydnn.util.async.OnItemSelectedListener

/**
 * [RecyclerView.Adapter] that can display a [TemplateNetwork] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class DataViewAdapter(val dataSlice: DataSlice, private val mValues: List<DataFormatView>, private val mListener: OnItemSelectedListener?, val dismissible: Dismissible?) : androidx.recyclerview.widget.RecyclerView.Adapter<DataFormatViewHolder>() {
    override fun getItemCount() = mValues.size

    override fun onBindViewHolder(holder: DataFormatViewHolder, position: Int) {
        val item = mValues[position]
        holder.mItem = item
        holder.mContentView.removeAllViews()
        if (item.supportsData(dataSlice)) {
            holder.mContentView.addView(item.getViewForData(dataSlice, holder.parent, if (mListener is ProjectController) mListener.project!! else null))
            holder.mTitleView.setText(item.nameResId)
        } else {
            holder.mContentView.setBackgroundResource(android.R.drawable.stat_notify_error)
            holder.mTitleView.setText(R.string.title_not_supported)
        }

        holder.mView.setOnClickListener {
            val dismissCallback = mListener!!.onSelected(item)
            if (dismissCallback.dismiss && dismissible != null) {
                dismissible.dismiss()
            }
            dismissCallback.callback()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataFormatViewHolder {
        val _binding = FragmentDataViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataFormatViewHolder(_binding, parent)
    }
}
