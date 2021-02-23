package com.viveret.tinydnn.data.scenario.catchall

import android.content.Context
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.scenario.basis.InteractiveScenario
import java.util.*

class ManualTrain : InteractiveScenario {
    override val fragmentType: Class<*>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val hinting = InteractivityHint.Always
    override val id = UUID.fromString("58f7acca-4152-11e9-b210-d663bd873d93")
    override val dataMethod = DataMethod.BinaryGate

    private lateinit var project: NeuralNetProject

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
    }

    override fun compatibleWithNetwork(project: NeuralNetProject) = false

    override val source: String = "Your poor fingers"
    override val summary: String = "Cram numbers into it!"
    override var name: String = "Manual"//R.string.train_method_manual
}