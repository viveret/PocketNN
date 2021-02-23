package com.viveret.pocketn2.view.holders

import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.viveret.pocketn2.databinding.ListMinimizedDialogItemBinding
import com.viveret.pocketn2.view.model.MinimizableDialog

class MinimizableDialogViewHolder(binding: ListMinimizedDialogItemBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
    val icon: ImageView = binding.icon
    val title: TextView = binding.title
    val progress: ProgressBar = binding.progress

    lateinit var item: MinimizableDialog
}