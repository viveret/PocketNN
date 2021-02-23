package com.viveret.pocketn2.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viveret.pocketn2.databinding.FragmentTrainScenarioItemBinding
import com.viveret.pocketn2.view.holders.ScenarioViewHolder
import com.viveret.tinydnn.data.Scenario
import com.viveret.tinydnn.util.async.Dismissible
import com.viveret.tinydnn.util.async.Observable
import com.viveret.tinydnn.util.async.OnItemSelectedListener

/**
 * [RecyclerView.Adapter] that can display a [TemplateNetwork]
 */
class ScenarioAdapter(private val mValues: List<Scenario>, val dismissible: Dismissible?, initialListener: OnItemSelectedListener) : androidx.recyclerview.widget.RecyclerView.Adapter<ScenarioViewHolder>(), Observable {
    override fun addListener(listener: OnItemSelectedListener) {
        this.listeners.add(listener)
    }

    override fun removeListener(listener: OnItemSelectedListener) {
        this.listeners.remove(listener)
    }

    override val listeners = ArrayList<OnItemSelectedListener>()

    init {
        addListener(initialListener)
    }

    override fun getItemCount() = mValues.size

    override fun onBindViewHolder(holder: ScenarioViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mTitleView.text = mValues[position].name
        holder.mContentView.text = mValues[position].summary

        holder.mView.setOnClickListener {
            try {
                if (this.onSelected(holder.mItem!!) && dismissible != null) {
                    dismissible.dismiss()
                }
            } catch (e: Exception) {
                Log.e("PocketN2", e.localizedMessage, e)
                this.onSelected(e)
            }
        }
    }

    private fun onSelected(item: Any): Boolean {
        if (listeners.size > 0) {
            var ret = false
            for (li in this.listeners) {
                val selectCallback = li.onSelected(item)
                if (selectCallback.dismiss) {
                    ret = true
                }
                selectCallback.callback()
            }
            return ret
        } else {
            throw java.lang.Exception("No listeners to intercept onSelected")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioViewHolder {
        val binding = FragmentTrainScenarioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScenarioViewHolder(binding)
    }
}
