package com.viveret.tinydnn.scenario.basis

import android.content.Context
import com.viveret.pocketn2.view.fragments.scenario.ImportScenarioFragment
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint
import com.viveret.tinydnn.project.NeuralNetProject

abstract class ImportScenarioBase(val nameId: Int): ImportScenario {
    private lateinit var project: NeuralNetProject
    private lateinit var context: Context
    override val hinting: InteractivityHint = InteractivityHint.Always

    override val name: String
        get() = context.getString(nameId)

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
        this.context = context
    }

    override val fragmentType: Class<*> = ImportScenarioFragment::class.java
}