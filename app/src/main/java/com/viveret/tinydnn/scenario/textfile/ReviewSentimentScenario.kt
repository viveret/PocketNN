package com.viveret.tinydnn.data.scenario.textfile

import android.content.Context
import com.viveret.pocketn2.R
import com.viveret.tinydnn.basis.DataSource
import com.viveret.tinydnn.basis.HostedStreamPackage
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.scenario.basis.OnlineScenarioBase
import java.util.*

class ReviewSentimentScenario : OnlineScenarioBase(R.string.online_movie_reviews) {
    override val id = UUID.fromString("bd61663e-503d-11e9-8647-d663bd873d93")
    override val dataMethod = DataMethod.TextFile
    override val dataFormat = UUID.fromString("bd6162ba-503d-11e9-8647-d663bd873d93")!!
    override val dataSuite = UUID.fromString("bd615b76-503d-11e9-8647-d663bd873d93")!!
    override val hinting: InteractivityHint = InteractivityHint.Always

    override val hasData: Boolean
        get() = HostedStreamPackage.fromId(this.dataSuite).isAvailable(DataSource.LocalFile)

    override fun openData(fitTo: Boolean): DataSliceReader =
            HostedStreamPackage.fromId(this.dataSuite).open(DataSource.LocalFile, fitTo, project)

    private lateinit var project: NeuralNetProject
    private lateinit var context: Context

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
        this.context = context
    }

    override fun compatibleWithNetwork(project: NeuralNetProject) = project.get().in_data_size() >= 300L && (project.get().out_data_size() == 1L || project.get().out_data_size() == 12L)

    override val summary: String = "Train to detect sentiment in online user reviews"
    override val source: String = "Online movie reviews"
}