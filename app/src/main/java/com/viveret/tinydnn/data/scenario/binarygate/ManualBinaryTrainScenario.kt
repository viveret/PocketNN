package com.viveret.tinydnn.data.scenario.binarygate

import android.content.Context
import com.viveret.pocketn2.view.fragments.scenario.BinaryGateTrainConfigFragment
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.scenario.basis.InteractiveScenario
import java.util.*

class ManualBinaryTrainScenario : InteractiveScenario {
    override val hinting = InteractivityHint.Always
    override val id = UUID.fromString("58f7a64e-4152-11e9-b210-d663bd873d93")
    override val dataMethod = DataMethod.BinaryGate
    lateinit var project: NeuralNetProject
    private lateinit var context: Context

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
        this.context = context
    }

    override fun compatibleWithNetwork(project: NeuralNetProject) = project.get().in_data_size() == 2L && project.get().out_data_size() == 1L

    override val summary: String = "Train a Binary gate manually"
    override val source: String = "You are the input source"
    override var name: String = "Manual"//R.string.train_binary_manual
    override val fragmentType: Class<*> = BinaryGateTrainConfigFragment::class.java
}