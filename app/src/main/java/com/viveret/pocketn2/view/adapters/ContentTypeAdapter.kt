package com.viveret.pocketn2.view.adapters

import android.content.Context
import android.widget.ArrayAdapter
import com.viveret.tinydnn.enums.ContentType
import com.viveret.tinydnn.project.NeuralNetProject

class ContentTypeAdapter(context: Context, val project: NeuralNetProject):
        ArrayAdapter<ContentTypeAdapter.Adapter>(context,
                android.R.layout.simple_spinner_item,
                ContentType.values().map { x -> Adapter(x, context) }) {
    init {
        this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    class Adapter(val item: ContentType, val context: Context) {
        override fun toString(): String = context.getString(item.stringResId)
    }
}