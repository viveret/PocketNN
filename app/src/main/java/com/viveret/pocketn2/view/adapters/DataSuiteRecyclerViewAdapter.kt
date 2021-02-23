package com.viveret.pocketn2.view.adapters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentDataSuiteListItemBinding
import com.viveret.pocketn2.view.activities.DataSuiteDetailActivity
import com.viveret.pocketn2.view.activities.DataSuiteListActivity
import com.viveret.pocketn2.view.fragments.data.DataSuiteDetailFragment
import com.viveret.tinydnn.basis.DataSource
import com.viveret.tinydnn.basis.HostedStreamPackage
import com.viveret.tinydnn.basis.StreamPackage
import java.text.NumberFormat
import java.util.*

class DataSuiteRecyclerViewAdapter(private val parentActivity: DataSuiteListActivity,
                                   private val twoPane: Boolean, val dataManager: DataManager) :
        androidx.recyclerview.widget.RecyclerView.Adapter<DataSuiteRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    private val values
        get() = HostedStreamPackage.filter { x -> x.isAvailable(DataSource.LocalFile) }.toTypedArray()

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as StreamPackage
            if (twoPane) {
                val fragment = DataSuiteDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(DataSuiteDetailFragment.ARG_ITEM_ID, item.id.toString())
                        putString(DataSuiteDetailFragment.ARG_PROJECT_ID, parentActivity.intent.getStringExtra(DataSuiteDetailFragment.ARG_PROJECT_ID))
                    }
                }
                parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit()
            } else {
                val intent = Intent(v.context, DataSuiteDetailActivity::class.java).apply {
                    putExtra(DataSuiteDetailFragment.ARG_ITEM_ID, item.id.toString())
                    putExtra(DataSuiteDetailFragment.ARG_PROJECT_ID, parentActivity.intent.getStringExtra(DataSuiteDetailFragment.ARG_PROJECT_ID))
                }
                v.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentDataSuiteListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.title
        holder.size.text = NumberFormat.getNumberInstance(Locale.getDefault())
                .format(item.sizeOfStreams(DataSource.LocalFile) / 1000) + "KB"

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: FragmentDataSuiteListItemBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view.root) {
        val idView = view.text
        val size = view.size
    }
}