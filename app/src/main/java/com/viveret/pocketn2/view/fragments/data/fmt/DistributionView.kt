package com.viveret.pocketn2.view.fragments.data.fmt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentDataViewTostringBinding
import com.viveret.tinydnn.basis.DataSlice
import com.viveret.tinydnn.project.NeuralNetProject
import kotlin.math.roundToLong

class DistributionView : DataFormatView {
    override val nameResId: Int = R.string.title_fragment_data_view_distribution

    override fun supportsData(data: DataSlice): Boolean = data.values.first().first.vals.size in 1..30

    override fun getViewForData(data: DataSlice, parent: ViewGroup, project: NeuralNetProject?): View {
        val binding = FragmentDataViewTostringBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val vect = data.values.first().first
        val min = vect.vals.minOrNull()!!
        val max = vect.vals.maxOrNull()!!
        val diff = max - min
        val valsSaved = vect.vals.mapIndexed { i, x -> ProbabilityPoint((x - min) / diff, i) }
        val valsOrdered = valsSaved.sortedBy { x -> x.prob }.reversed()

        var msg = ""
        for (prob in valsOrdered) {
            msg += if (prob.prob.isNaN()) {
                "${prob.idVal} is NaN\n"
            } else {
                "${prob.idVal} at ${(prob.prob * 100).roundToLong()}%\n"
            }
        }

        //(view as TextView).text = msg
        binding.message.text = msg
        val v2 = binding.btnKeep
        if (project != null) {
            v2.setOnClickListener { project.saveMessage(ConsoleMessage.MessageLevel.LOG, msg) }
        } else {
            v2.visibility = View.GONE
        }
        return binding.root
    }

    class ProbabilityPoint(val prob: Float, val idVal: Int)
}