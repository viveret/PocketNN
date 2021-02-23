package com.viveret.tinydnn.data.scenario.textfile

import android.content.Context
import com.viveret.pocketn2.view.fragments.scenario.TextSentimentFragment
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.scenario.basis.InteractiveScenario
import java.util.*

class TextSentimentScenario: InteractiveScenario {
    override val hinting = InteractivityHint.Always
    override val id = UUID.fromString("0e345114-5293-11e9-8647-d663bd873d93")
    override val dataMethod = DataMethod.TextFile

    override fun compatibleWithNetwork(project: NeuralNetProject) = project.get().in_data_size() >= 300L && project.get().out_data_size() == 1L

    private lateinit var project: NeuralNetProject

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
    }

    override var name: String = "Text Sentiment"
    override val summary: String = "Learn from text input and associated emotion."
    override val source: String = "The text in the file at <<>>"
    override val fragmentType: Class<*> = TextSentimentFragment::class.java
}