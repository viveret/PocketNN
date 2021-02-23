package com.viveret.pocketn2.view.fragments.scenario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.viveret.pocketn2.databinding.FragmentScenarioMovieSummaryCategoryBinding
import com.viveret.tinydnn.data.formats.CMUMovieSummaryCorpusFormat
import com.viveret.tinydnn.data.scenario.textfile.InteractiveTextScenario

class MovieSummaryCategoryFragment: InteractiveTextScenario<FragmentScenarioMovieSummaryCategoryBinding>() {
    override val dismissAfterSubmit: Boolean = true
    override val initialExpansionState: Int? = null
    private lateinit var categorySpinner: Spinner

    override fun getFitToValues(): FloatArray {
        // CMUMovieSummaryCorpusFormat.outputLabels
       return arrayOf(categorySpinner.selectedItemPosition.toFloat()).toFloatArray()
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentScenarioMovieSummaryCategoryBinding? {
        val binding = FragmentScenarioMovieSummaryCategoryBinding.inflate(inflater, container, false)
        this.btnUndo = binding.btnRotateCounterClockwise
        this.btnRedo = binding.btnRotateClockwise
        this.input = binding.textInput
        if (fitToOutput) {
            this.categorySpinner = binding.category
            this.categorySpinner.adapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, CMUMovieSummaryCorpusFormat.outputLabels)
            (this.categorySpinner.parent as ViewGroup).visibility = View.VISIBLE
        }
        return binding
    }
}