package com.viveret.tinydnn.data.scenario.catchall

import android.content.Context
import com.viveret.pocketn2.view.fragments.scenario.CameraScenarioFragment
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.scenario.basis.InteractiveScenario
import java.util.*

class CameraScenario : InteractiveScenario {
    override val hinting = InteractivityHint.Always
    override val id = UUID.fromString("58f7aa2c-4152-11e9-b210-d663bd873d93")
    override val dataMethod = DataMethod.Camera

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
    }

    override fun compatibleWithNetwork(project: NeuralNetProject) = true

    private lateinit var project: NeuralNetProject
    override val summary: String = "Gather input from a camera"
    override val source: String = "Your phone"
    override var name = "Camera"//R.string.train_method_camera
    override val fragmentType: Class<*> = CameraScenarioFragment::class.java
}