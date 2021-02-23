package com.viveret.pocketn2.view.holders

import android.widget.TextView
import com.viveret.pocketn2.databinding.FragmentTrainScenarioItemBinding
import com.viveret.tinydnn.data.Scenario

class ScenarioViewHolder(binding: FragmentTrainScenarioItemBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
    val mView = binding.root
    val mTitleView: TextView = binding.title
    val mContentView: TextView = binding.content
    var mItem: Scenario? = null

    override fun toString(): String = "${super.toString()} '${mItem?.summary}'"
}