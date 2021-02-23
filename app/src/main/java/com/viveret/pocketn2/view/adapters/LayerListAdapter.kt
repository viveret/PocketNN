package com.viveret.pocketn2.view.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.viveret.pocketn2.view.holders.editor.CarouselViewHolder
import com.viveret.pocketn2.view.holders.editor.InsertViewHolder
import com.viveret.pocketn2.view.holders.editor.LayerViewHolder
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.util.async.OnItemSelectedListener

/**
 * [RecyclerView.Adapter] that can display a [LayerViewHolder] and makes a call to the
 * specified [OnItemSelectedListener].
 */
class LayerListAdapter(private val project: NeuralNetProject, private val listener: OnItemSelectedListener, val allowInsert: Boolean) : RecyclerView.Adapter<CarouselViewHolder>() {
    private val layerViews = ArrayList<LayerViewHolder>()

    fun refreshVisualizations() {
        for (layerView in layerViews) {
            layerView.refresh()
        }
    }

    override fun getItemCount() = project.get().layer_size().toInt() * 2 + 1

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        when (holder) {
            is InsertViewHolder -> {
                holder.insertPosition = position.toLong() / 2
                holder.refresh()
            }
            is LayerViewHolder -> {
                holder.item = project.get().layerAt((position.toLong() - 1) / 2)
                holder.refresh()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            if (allowInsert || position == itemCount - 1) {
                1
            } else {
                2
            }
        } else if (position == itemCount) {
            1
        } else {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return when (viewType) {
            0 -> LayerViewHolder(parent, listener)
            1 -> InsertViewHolder(parent, listener)
            else -> BlankViewHolder(parent)
        }
    }

    class BlankViewHolder(parent: ViewGroup) : CarouselViewHolder(parent, genView(parent)) {
        companion object {
            fun genView(parent: ViewGroup): View = LinearLayout(parent.context).apply {
                layoutParams = LinearLayout.LayoutParams(100, 100)
            }
        }
    }
}