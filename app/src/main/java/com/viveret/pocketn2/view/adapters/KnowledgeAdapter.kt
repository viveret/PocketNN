package com.viveret.pocketn2.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.viveret.pocketn2.databinding.FragmentListItemBinding
import com.viveret.pocketn2.view.holders.KnowledgeItemViewHolder
import com.viveret.tinydnn.data.knowledge.KnowledgeCatalogItem
import com.viveret.tinydnn.util.async.Dismissible
import com.viveret.tinydnn.util.async.OnItemSelectedListener

class KnowledgeAdapter (private val mValues: List<KnowledgeCatalogItem>, private val mListener: OnItemSelectedListener?, private val dismissible: Dismissible) : androidx.recyclerview.widget.RecyclerView.Adapter<KnowledgeItemViewHolder>() {
    override fun getItemCount() = mValues.size

    override fun onBindViewHolder(holder: KnowledgeItemViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mTitleView.text = mValues[position].stream.name
        holder.mContentView.setText(mValues[position].description)

        holder.mView.setOnClickListener {
            val dismissCallback = mListener!!.onSelected(holder.mItem!!)
            if (dismissCallback.dismiss) {
                dismissible.dismiss()
            }
            dismissCallback.callback()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KnowledgeItemViewHolder {
        val binding = FragmentListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KnowledgeItemViewHolder(binding)
    }
}