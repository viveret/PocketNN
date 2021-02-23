package com.viveret.tinydnn.scenario.basis

import android.content.Context
import com.viveret.pocketn2.view.fragments.scenario.OnlineScenarioFragment
import com.viveret.tinydnn.basis.DataSource
import com.viveret.tinydnn.basis.HostedStreamPackage
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.project.NeuralNetProject

abstract class OnlineScenarioBase(val nameId: Int): OnlineScenario {
    private lateinit var project: NeuralNetProject
    private lateinit var context: Context
    override val hinting: InteractivityHint = InteractivityHint.Always

    override val hasData: Boolean
        get() = HostedStreamPackage.fromId(this.dataSuite).isAvailable(DataSource.LocalFile)

    override val name: String
        get() = context.getString(nameId)

    override fun openData(fitTo: Boolean): DataSliceReader =
            HostedStreamPackage.fromId(this.dataSuite).open(DataSource.LocalFile, fitTo, project)

    override fun deleteData() =
            HostedStreamPackage.fromId(this.dataSuite).delete(DataSource.LocalFile)

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
        this.context = context
    }

    override val fragmentType: Class<*> = OnlineScenarioFragment::class.java
}