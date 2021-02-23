package com.viveret.pocketn2.view.holders

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.viveret.pocketn2.databinding.FragmentDataViewBinding
import com.viveret.pocketn2.view.fragments.data.fmt.DataFormatView

class DataFormatViewHolder(binding: FragmentDataViewBinding, val parent: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
    val mView = binding.root
    val mTitleView: TextView = binding.title
    val mContentView: FrameLayout = binding.content
    var mItem: DataFormatView? = null
}