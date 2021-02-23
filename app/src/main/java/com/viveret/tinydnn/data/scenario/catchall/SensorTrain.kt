package com.viveret.tinydnn.data.scenario.catchall

import android.content.Context
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.scenario.basis.InteractiveScenario
import java.util.*

class SensorTrain : InteractiveScenario {
    override val fragmentType: Class<*>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val hinting = InteractivityHint.Always
    override val id = UUID.fromString("8e1cb444-4153-11e9-b210-d663bd873d93")
    override val dataMethod = DataMethod.Sensors

    private lateinit var project: NeuralNetProject

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
    }

    override fun compatibleWithNetwork(project: NeuralNetProject) = false

    override var name: String = "Sensor"//R.string.train_method_sensor
    override val summary: String = "Learn from sensors, the computer's basic senses."
    override val source: String = "Your phone"
}