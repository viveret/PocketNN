package com.viveret.pocketn2.view.holders

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.viveret.pocketn2.databinding.FragmentNavigationViewBinding
import com.viveret.tinydnn.util.nav.NavigationItem

class NavigationViewHolder(binding: FragmentNavigationViewBinding, val parent: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
    val view: View = binding.root
    val icon: ImageView = binding.icon
    val name: TextView = binding.name
    val modified: TextView = binding.modified

    lateinit var item: NavigationItem
}