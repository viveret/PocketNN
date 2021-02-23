package com.viveret.tinydnn.data.scenario.textfile

import android.content.Context
import com.viveret.pocketn2.view.fragments.scenario.MovieSummaryCategoryFragment
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.formats.CMUMovieSummaryCorpusFormat
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.scenario.basis.InteractiveScenario
import java.util.*

class MovieSummaryCategoryScenario: InteractiveScenario {
    override val hinting = InteractivityHint.Always
    override val id = UUID.fromString("322b34cf-cdef-4246-acc2-3223781c05b4")
    override val dataMethod = DataMethod.TextFile

    override fun compatibleWithNetwork(project: NeuralNetProject) = project.get().in_data_size() >= 300L && project.get().out_data_size() == CMUMovieSummaryCorpusFormat.outputLabels.size.toLong()

    private lateinit var project: NeuralNetProject

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
    }

    override var name: String = "Movie Summary Categories"
    override val summary: String = "Learn from movie summaries and categories."
    override val source: String = "The text in the file at <<>>"
    override val fragmentType: Class<*> = MovieSummaryCategoryFragment::class.java
}