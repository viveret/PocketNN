package com.viveret.pocketn2.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viveret.pocketn2.databinding.FragmentTemplatennItemBinding
import com.viveret.pocketn2.view.holders.TemplateNNViewHolder
import com.viveret.tinydnn.project.TemplateNetwork
import com.viveret.tinydnn.util.async.OnItemSelectedListener

/**
 * [RecyclerView.Adapter] that can display a [TemplateNetwork] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class TemplateNNAdapter(private val mValues: List<TemplateNetwork>, private val mListener: OnItemSelectedListener?) : RecyclerView.Adapter<TemplateNNViewHolder>() {
    override fun getItemCount() = mValues.size

    override fun onBindViewHolder(holder: TemplateNNViewHolder, position: Int) {
        holder.apply {
            mItem = mValues[position]
            mTitleView.text = mValues[position].name
            mContentView.text = mValues[position].description
        }

        holder.mView.setOnClickListener {
            mListener!!.onSelected(holder.mItem!!).callback()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateNNViewHolder =
            TemplateNNViewHolder(FragmentTemplatennItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}
