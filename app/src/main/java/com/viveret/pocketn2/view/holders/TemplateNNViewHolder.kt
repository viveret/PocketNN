package com.viveret.pocketn2.view.holders

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.viveret.pocketn2.databinding.FragmentTemplatennItemBinding
import com.viveret.tinydnn.project.TemplateNetwork

class TemplateNNViewHolder(binding: FragmentTemplatennItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val mView = binding.root
    val mTitleView: TextView = binding.title
    val mContentView: TextView = binding.content
    var mItem: TemplateNetwork? = null

    override fun toString(): String = "${super.toString()} '${mContentView.text}'"
}