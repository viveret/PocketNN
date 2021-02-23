package com.viveret.tinydnn.data.scenario.textfile

import android.content.Context
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.scenario.basis.InteractiveScenario
import java.util.*

class TextFileScenario : InteractiveScenario {
    override val fragmentType: Class<*>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val hinting = InteractivityHint.Always
    override val id = UUID.fromString("8e1cbb4c-4153-11e9-b210-d663bd873d93")
    override val dataMethod = DataMethod.TextFile

    private lateinit var project: NeuralNetProject

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
    }

    override fun compatibleWithNetwork(project: NeuralNetProject) = false

    override var name: String = "Text File"//R.string.train_method_text_file
    override val summary: String = "Learn from a text file, similar to a human reading."
    override val source: String = "The text in the file at <<>>"
}