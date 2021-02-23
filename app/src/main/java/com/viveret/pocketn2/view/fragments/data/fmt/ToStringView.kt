package com.viveret.pocketn2.view.fragments.data.fmt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentDataViewTostringBinding
import com.viveret.tinydnn.basis.DataSlice
import com.viveret.tinydnn.project.NeuralNetProject

class ToStringView : DataFormatView {
    override val nameResId: Int = R.string.title_fragment_data_view_tostring

    override fun supportsData(file: DataSlice): Boolean = file.toString().length < 200

    override fun getViewForData(dataSlice: DataSlice, parent: ViewGroup, project: NeuralNetProject?): View {
        val binding = FragmentDataViewTostringBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //(view as TextView).text = file.toString()
        binding.message.text = dataSlice.toString()
        val v2 = binding.btnKeep
        if (project != null) {
            v2.setOnClickListener { project.saveMessage(ConsoleMessage.MessageLevel.LOG, dataSlice.toString()) }
        } else {
            v2.visibility = View.GONE
        }

        return binding.root
    }
}