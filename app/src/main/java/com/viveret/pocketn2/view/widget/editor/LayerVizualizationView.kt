package com.viveret.pocketn2.view.widget.editor

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.viveret.pocketn2.R
import com.viveret.tinydnn.data.graphics.LayerVizualization
import com.viveret.tinydnn.data.graphics.VizualizationSubject
import com.viveret.tinydnn.util.async.OnItemSelectedListener

class LayerVizualizationView(val context: Context, val vizualization: LayerVizualization, val listener: OnItemSelectedListener) {
    val tvImgTitle = TextView(context)
    val imgView = ImageView(context)

    init {
        val lptvImgTitle = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tvImgTitle.layoutParams = lptvImgTitle
        tvImgTitle.setTextColor(Color.WHITE)
        tvImgTitle.setText(this.subjectTitle(vizualization.subject))

        val lpImage = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        imgView.layoutParams = lpImage
        this.refresh()

        imgView.setOnClickListener {
            listener.onSelected(vizualization)
        }
    }

    private fun subjectTitle(subject: VizualizationSubject): Int {
        return when (subject) {
            VizualizationSubject.Weights -> R.string.weights_lbl
            VizualizationSubject.Outputs -> R.string.outputs_visualization
        }
    }

    fun refresh() {
        imgView.setImageBitmap(vizualization.asIcon()) // imgBmp.asIcon() -> should this be a callback? Kinda like renderquueue when no data changes just the saved draw queue renders
    }

    fun attach(wrapper: LinearLayout) {
        wrapper.addView(tvImgTitle)
        wrapper.addView(imgView)
    }
}