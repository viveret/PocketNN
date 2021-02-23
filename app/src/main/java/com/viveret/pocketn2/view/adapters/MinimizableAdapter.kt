package com.viveret.pocketn2.view.adapters

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import com.viveret.pocketn2.databinding.ListMinimizedDialogItemBinding
import com.viveret.pocketn2.view.holders.MinimizableDialogViewHolder
import com.viveret.pocketn2.view.model.MinimizableDialog
import com.viveret.pocketn2.view.widget.DynamicDrawable

class MinimizableAdapter(private val items: MutableList<MinimizableDialog>) : androidx.recyclerview.widget.RecyclerView.Adapter<MinimizableDialogViewHolder>() {
    val progressDialogs = HashSet<MinimizableDialogViewHolder>()
    private val customHandler = Handler()
    private val updateTimerThread = object : Runnable {
        override fun run() {
            for (d in progressDialogs) {
                refreshViewHolder(d)
            }
            customHandler.postDelayed(this, 1000)
        }
    }

    init {
        customHandler.postDelayed(updateTimerThread, 1000)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MinimizableDialogViewHolder, position: Int) {
        holder.item = items[position]

        progressDialogs.add(holder)

        holder.title.setOnClickListener {
            holder.item.showDialog()
            items.remove(holder.item)
            notifyDataSetChanged()
        }
        refreshViewHolder(holder)
    }

    fun refreshViewHolder(holder: MinimizableDialogViewHolder) {
        holder.title.text = holder.item.title
        if (holder.item is DynamicDrawable) {
            holder.progress.progress = ((holder.item as DynamicDrawable).progression * holder.progress.max).toInt()
        }
        holder.icon.setImageResource(holder.item.icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MinimizableDialogViewHolder {
        val binding = ListMinimizedDialogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MinimizableDialogViewHolder(binding)
    }
}
