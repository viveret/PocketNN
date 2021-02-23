package com.viveret.pocketn2.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viveret.pocketn2.databinding.FragmentListItemBinding
import com.viveret.pocketn2.view.holders.ChallengeMetaInfoHolder
import com.viveret.tinydnn.data.challenge.ChallengeMetaInfo
import com.viveret.tinydnn.util.async.OnItemSelectedListener

/**
 * [RecyclerView.Adapter] that can display a [ChallengeMetaInfo] and makes a call to the
 * specified [OnItemSelectedListener].
 */
class ChallengeAdapter(private val items: List<ChallengeMetaInfo>, private val mListener: OnItemSelectedListener?) : androidx.recyclerview.widget.RecyclerView.Adapter<ChallengeMetaInfoHolder>() {
    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ChallengeMetaInfoHolder, position: Int) {
        holder.mItem = items[position]
        holder.mTitleView.setText(items[position].name)
        holder.mContentView.setText(items[position].description)

        holder.mView.setOnClickListener {
            mListener!!.onSelected(holder.mItem!!).callback()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeMetaInfoHolder {
        val binding = FragmentListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChallengeMetaInfoHolder(binding)
    }
}
