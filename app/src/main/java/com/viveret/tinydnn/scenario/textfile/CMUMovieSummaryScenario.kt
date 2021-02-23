package com.viveret.tinydnn.data.scenario.textfile

import android.content.Context
import com.viveret.pocketn2.R
import com.viveret.tinydnn.basis.DataSource
import com.viveret.tinydnn.basis.HostedStreamPackage
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.formats.CMUMovieSummaryCorpusFormat
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.scenario.basis.OnlineScenarioBase
import java.util.*

class CMUMovieSummaryScenario : OnlineScenarioBase(R.string.cmu_movie_summary_corpus) {
    override val id = UUID.fromString("a3043090-4da5-11e9-8646-d663bd873d93")
    override val dataMethod = DataMethod.TextFile
    override val dataFormat = UUID.fromString("a30433b0-4da5-11e9-8646-d663bd873d93")!!
    override val dataSuite = UUID.fromString("a304354a-4da5-11e9-8646-d663bd873d93")!!
    override val hinting: InteractivityHint = InteractivityHint.Always

    override val hasData: Boolean
        get() = HostedStreamPackage.fromId(this.dataSuite).isAvailable(DataSource.LocalFile)

    override fun openData(fitTo: Boolean): DataSliceReader =
            HostedStreamPackage.fromId(this.dataSuite).open(DataSource.LocalFile, fitTo, project)

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
        this.context = context
    }

    override fun compatibleWithNetwork(project: NeuralNetProject) = project.get().in_data_size() >= 300L && project.get().out_data_size() == CMUMovieSummaryCorpusFormat.outputLabels.size.toLong()

    override val summary: String = "Train to give movie summaries a category"
    override val source: String = "David Bamman, Brendan O'Connor, and Noah Smith at the Language Technologies Institute and Machine Learning Department at Carnegie Mello"
    private lateinit var project: NeuralNetProject
    private lateinit var context: Context
}