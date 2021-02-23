package com.viveret.pocketn2.view.adapters

import android.content.Context
import android.widget.ArrayAdapter
import com.viveret.pocketn2.DataManager
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.project.NeuralNetProject

class DataMethodAdapter: ArrayAdapter<DataMethodAdapter.AdapterDataMethod> {
    val project: NeuralNetProject

    constructor(context: Context, project: NeuralNetProject, items: List<DataMethod>): super(context,
            android.R.layout.simple_spinner_item, items.map { x -> AdapterDataMethod(x, context) }) {
        this.project = project
        this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    constructor(context: Context, project: NeuralNetProject) : this(context, project,
            DataManager.get(context).supportedDataMethods(project).toList())

    fun getItemAt(position: Int): DataMethod? = getItem(position)?.dataMethod

    fun getPosition(method: DataMethod): Int = getPosition(AdapterDataMethod(method, this.context))

    class AdapterDataMethod(val dataMethod: DataMethod, val context: Context) {
        override fun toString(): String = context.getString(dataMethod.stringResId)
        override fun hashCode(): Int = dataMethod.hashCode()
        override fun equals(other: Any?): Boolean = other is AdapterDataMethod && this.dataMethod == other.dataMethod
    }
}