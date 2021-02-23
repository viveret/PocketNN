package com.viveret.pocketn2.view.adapters

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viveret.pocketn2.databinding.FragmentNavigationViewBinding
import com.viveret.pocketn2.view.holders.NavigationViewHolder
import com.viveret.tinydnn.data.nav.NavigationListener
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.nav.NavigationItem
import java.io.File
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [TemplateNetwork] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class NavigationAdapter(private val mListener: OnItemSelectedListener?, val frag: androidx.fragment.app.Fragment?, val exceptionListener: OnItemSelectedListener) : androidx.recyclerview.widget.RecyclerView.Adapter<NavigationViewHolder>(), NavigationListener {
    override fun onItemsChange(items: Array<NavigationItem>) {
        this.currentEntries = items
        this.notifyDataSetChanged()
    }

    override fun onHistoryChange(history: Array<String>) {
        this.notifyDataSetChanged()
    }

    override fun onLocationChange(location: File) {
    }

    var currentEntries = emptyArray<NavigationItem>()

    override fun getItemCount() = currentEntries.size

    override fun onBindViewHolder(holder: NavigationViewHolder, position: Int) {
        val item = currentEntries[position]
        holder.item = item
        holder.name.text = item.name
        holder.modified.text = DateUtils.getRelativeTimeSpanString(item.lastModified, Date().time, 0L, DateUtils.FORMAT_ABBREV_RELATIVE)
        holder.icon.setImageResource(item.iconResId)
        if (item.canRead) {
            holder.view.setOnClickListener {
                try {
                    val dismissCallback = this.mListener!!.onSelected(item)
                    if (dismissCallback.dismiss && frag is androidx.fragment.app.DialogFragment) {
                        frag.dismiss()
                    }
                    dismissCallback.callback()
                } catch (e: Exception) {
                    Log.e("PocketN2", e.localizedMessage, e)
                    exceptionListener.onSelected(e).callback()
                }
            }
        } else {
            holder.view.setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationViewHolder {
        val binding = FragmentNavigationViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NavigationViewHolder(binding, parent)
    }
}
