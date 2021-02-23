package com.viveret.pocketn2.view.holders

import android.widget.TextView
import com.viveret.pocketn2.databinding.FragmentListItemBinding
import com.viveret.tinydnn.data.knowledge.KnowledgeCatalogItem

class KnowledgeItemViewHolder(binding: FragmentListItemBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
    val mView = binding.root
    val mTitleView: TextView = binding.title
    val mContentView: TextView = binding.content
    var mItem: KnowledgeCatalogItem? = null

    override fun toString(): String = "${super.toString()} '${mContentView.text}'"
}