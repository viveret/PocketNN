package com.viveret.tinydnn.data.scenario.binaryfile

import android.content.Context
import com.viveret.pocketn2.view.fragments.scenario.CanvasScenarioFragment
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.scenario.basis.InteractiveScenario
import java.util.*

class CanvasScenario : InteractiveScenario {
    override val fragmentType: Class<*> = CanvasScenarioFragment::class.java
    override val id = UUID.fromString("539dcb8e-4151-11e9-b210-d663bd873d93")
    override val dataMethod = DataMethod.Canvas
    override val hinting: InteractivityHint = InteractivityHint.Always

    private lateinit var project: NeuralNetProject
    private lateinit var context: Context

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
        this.context = context
    }

    override fun compatibleWithNetwork(project: NeuralNetProject) = project.get().in_data_size() >= 9

    override val source: String = "Your poor fingers"
    override val summary: String = "Draw a what you are thinking of"
    override var name: String = "Canvas"
}