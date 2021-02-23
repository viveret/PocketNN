package com.viveret.pocketn2.view.holders.editor

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.view.widget.editor.LayerVizualizationView
import com.viveret.tinydnn.error.NNException
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.network.Layer
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import kotlin.math.abs

class LayerViewHolder(val parent: ViewGroup, val wrapper: LinearLayout, val textView: TextView, val listener: OnItemSelectedListener): CarouselViewHolder(parent, wrapper) {
    var item: Layer<*>? = null
    private lateinit var visualizations: List<LayerVizualizationView>

    constructor(parent: ViewGroup, listener: OnItemSelectedListener): this(parent, genView(parent.context), genTextView(parent.context), listener) {
        refresh()
    }

    fun refreshVisualzations() {
        for (viz in visualizations) {
            viz.refresh()
        }
    }

    fun refresh() {
        if (item != null) {
            wrapper.removeAllViews()
            wrapper.addView(textView)

            visualizations = item!!.getVisualizations().map { x -> LayerVizualizationView(parent.context, x, listener) }
            try {
                // TODO: More weight views? How to visualize different layers?
                for (viz in visualizations) {
                    viz.attach(wrapper)
                }
            } catch (e: NNException) {
                textView.text = "${textView.text}\n${e.localizedMessage}"
            } catch (e: UserException) {
                textView.text = "${textView.text}\n${e.localizedMessage}"
            }

            textView.text = item!!.toString()
            wrapper.setBackgroundColor(colorForLayer(item!!))
            refreshVisualzations()
        }
    }

    private fun colorForLayer(l: Layer<*>): Int {
        val colors = DataManager.get(parent.context).backgroundColors()
        return colors[abs(l.layer_type().hashCode()) % colors.size]
    }

    companion object {
        fun genView(context: Context): LinearLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL
            setPadding(100, 0, 100, 0)
        }

        fun genTextView(context: Context): TextView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            setTextColor(Color.WHITE)
        }
    }
}