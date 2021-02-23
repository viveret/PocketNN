package com.viveret.pocketn2.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentDataViewBinding
import com.viveret.pocketn2.view.fragments.data.fmt.DataFormatView
import com.viveret.pocketn2.view.holders.DataFormatViewHolder
import com.viveret.tinydnn.basis.AnchorPoint
import com.viveret.tinydnn.basis.DataAttr
import com.viveret.tinydnn.data.DataValues
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.project.ProjectController
import com.viveret.tinydnn.util.async.Dismissible
import com.viveret.tinydnn.util.async.OnItemSelectedListener

/**
 * [RecyclerView.Adapter] that can display a [TemplateNetwork] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class DataAdapter(val stream: DataSliceReader, private val dataFormatView: DataFormatView, private val mListener: OnItemSelectedListener?, val exceptionListener: OnItemSelectedListener, val dismissible: Dismissible?) : RecyclerView.Adapter<DataFormatViewHolder>() {
    override fun getItemCount() = stream.getInt(DataAttr.ElementCount) ?: error("Unknown element count")

    val itemBuffer = DataValues(stream.openRoles, 1)

    override fun onBindViewHolder(holder: DataFormatViewHolder, position: Int) {
        if (stream.seek(AnchorPoint.Start, position)) {
            val item = if (stream.read(itemBuffer, 0, 1) == 1) itemBuffer.get(0) else error("Could not get at $position")
            holder.mItem = dataFormatView
            holder.mContentView.removeAllViews()

            if (dataFormatView.supportsData(item)) {
                holder.mContentView.addView(dataFormatView.getViewForData(item, holder.parent, if (mListener is ProjectController) mListener.project!! else null))
                holder.mTitleView.setText(dataFormatView.nameResId)
            } else {
                holder.mContentView.setBackgroundResource(android.R.drawable.stat_notify_error)
                holder.mTitleView.setText(R.string.title_not_supported)
            }

            holder.mView.setOnClickListener {
                try {
                    val dismissCallback = this.mListener!!.onSelected(item)
                    if (dismissCallback.dismiss && dismissible != null) {
                        dismissible.dismiss()
                    }
                    dismissCallback.callback()
                } catch (e: UserException) {
                    Log.e("PocketN2", e.localizedMessage, e)
                    exceptionListener.onSelected(e).callback()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataFormatViewHolder {
        val _binding = FragmentDataViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataFormatViewHolder(_binding, parent)
    }
}
