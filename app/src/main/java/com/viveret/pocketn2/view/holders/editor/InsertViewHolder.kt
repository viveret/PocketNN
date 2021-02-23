package com.viveret.pocketn2.view.holders.editor

import android.view.ViewGroup
import android.widget.Button
import com.viveret.pocketn2.view.fragments.project.AddLayerFragment
import com.viveret.tinydnn.util.async.OnItemSelectedListener

class InsertViewHolder(parent: ViewGroup, val listener: OnItemSelectedListener, val btn: Button): CarouselViewHolder(parent, btn) {
    var insertPosition: Long = Long.MIN_VALUE

    constructor(parent: ViewGroup, listener: OnItemSelectedListener): this(parent, listener, genView(parent))

    constructor(parent: ViewGroup, listener: OnItemSelectedListener, position: Long) : this(parent, listener) {
        this.insertPosition = position
        this.refresh()
    }

    fun refresh() {
        btn.setOnClickListener {
            listener.onSelected(AddLayerFragment.newInstance(insertPosition))
        }
    }

    companion object{
        fun genView(parent: ViewGroup): Button {
            val btn = Button(parent.context)
            btn.text = "+"
            btn.textSize *= 2
            btn.setBackgroundResource(0)
            return btn
        }
    }
}