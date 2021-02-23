package com.viveret.pocketn2.view.holders

import android.widget.TextView
import com.viveret.pocketn2.databinding.FragmentTrainScenarioItemBinding
import com.viveret.tinydnn.data.transform.PackageGenerator

class PackageGeneratorViewHolder(binding: FragmentTrainScenarioItemBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
    val mView = binding.root
    val mTitleView: TextView = binding.title
    val mContentView: TextView = binding.content
    var mItem: PackageGenerator? = null

    override fun toString(): String = "${super.toString()} '${mItem?.name}'"
}