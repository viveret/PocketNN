package com.viveret.pocketn2.view.fragments.data.fmt

import android.view.View
import android.view.ViewGroup
import com.viveret.tinydnn.basis.DataSlice
import com.viveret.tinydnn.project.NeuralNetProject

interface DataFormatView {
    val nameResId: Int
    fun supportsData(data: DataSlice): Boolean
    fun getViewForData(data: DataSlice, parent: ViewGroup, project: NeuralNetProject?): View
}