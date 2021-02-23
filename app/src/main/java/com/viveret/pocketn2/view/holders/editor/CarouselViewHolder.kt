package com.viveret.pocketn2.view.holders.editor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

open class CarouselViewHolder(parent: ViewGroup, val view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    constructor(parent: ViewGroup, layoutId: Int): this(parent, LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
}