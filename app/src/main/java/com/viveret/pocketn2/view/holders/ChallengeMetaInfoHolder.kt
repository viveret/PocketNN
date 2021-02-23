package com.viveret.pocketn2.view.holders

import android.widget.TextView
import com.viveret.pocketn2.databinding.FragmentListItemBinding
import com.viveret.tinydnn.data.challenge.ChallengeMetaInfo

class ChallengeMetaInfoHolder(binding: FragmentListItemBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
    val mView = binding.root
    val mTitleView: TextView = binding.title
    val mContentView: TextView = binding.content
    var mItem: ChallengeMetaInfo? = null

    override fun toString(): String = "${super.toString()} '${mContentView.text}'"
}