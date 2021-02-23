package com.viveret.pocketn2.view.fragments.data.fmt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentDataViewTostringBinding
import com.viveret.tinydnn.basis.DataRole
import com.viveret.tinydnn.basis.DataSlice
import com.viveret.tinydnn.data.formats.CMUMovieSummaryCorpusFormat
import com.viveret.tinydnn.project.NeuralNetProject
import kotlin.math.roundToLong

class MovieCategoryView : DataFormatView {
    override val nameResId: Int = R.string.title_fragment_data_view_movie_category

    override fun supportsData(data: DataSlice): Boolean = (data[DataRole.Input]?.first?.vals?.size ?: 0) >= 300// CMUMovieSummaryCorpusFormat.outputLabels.size

    override fun getViewForData(data: DataSlice, parent: ViewGroup, project: NeuralNetProject?): View {
        val binding = FragmentDataViewTostringBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val vect = data[DataRole.Input]!!.first
        val valsSaved = vect.vals.mapIndexed { i, x -> ProbabilityPoint(x, i) }
        val valsOrdered = valsSaved.sortedBy { x -> x.prob }.reversed()

        var msg = ""
        for (prob in valsOrdered.take(10)) {
            if (prob.idVal < CMUMovieSummaryCorpusFormat.outputLabels.size) {
                msg += "${CMUMovieSummaryCorpusFormat.outputLabels[prob.idVal]} at ${(prob.prob * 100).roundToLong()}%\n"
            }
        }

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