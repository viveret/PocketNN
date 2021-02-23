package com.viveret.pocketn2.view.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viveret.pocketn2.databinding.FragmentAboutSectionBinding
import com.viveret.pocketn2.view.fragments.main.LearnMoreFragment

/**
 * [RecyclerView.Adapter] that can display a [TemplateNetwork] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class LearnMoreAdapter(private val mValues: List<LearnMoreFragment.LearnMoreSection>, private val mListener: LearnMoreFragment.OnLearnMoreSectionSelectedListener?) : RecyclerView.Adapter<LearnMoreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val _binding = FragmentAboutSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(_binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.mContentView.text = Html.fromHtml(mValues[position].mySectionText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            holder.mContentView.text = Html.fromHtml(mValues[position].mySectionText)
        }

        holder.mView.root.setOnClickListener {
            mListener?.onLearnMoreSectionSelected(holder.mItem!!.myId)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: FragmentAboutSectionBinding) : RecyclerView.ViewHolder(mView.root) {
        val mContentView = mView.content
        var mItem: LearnMoreFragment.LearnMoreSection? = null

        override fun toString(): String = "${super.toString()} '${mContentView.text}'"
    }
}
