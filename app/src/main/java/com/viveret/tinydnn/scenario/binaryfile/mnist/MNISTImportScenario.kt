package com.viveret.tinydnn.scenario.binaryfile.mnist

import com.viveret.pocketn2.R
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.model.CommonNetworkFilters
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.scenario.basis.ImportScenarioBase
import java.util.*

class MNISTImportScenario : ImportScenarioBase(R.string.title_mnist_scenario_import) {
    override val permissions: Array<String> = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    override val summary: String = "Import images of numbers from an external app"
    override val source: String = "Data from an external application"

    override val id = UUID.fromString("539dcfb2-4151-11e9-b210-d663b0873d93")
    override val dataMethod = DataMethod.BinaryFile
    override val dataFormat = UUID.fromString("44f43102-3054-4602-a10c-7f3c0d8cd3f7")!!

    override fun compatibleWithNetwork(project: NeuralNetProject) = CommonNetworkFilters.MNIST.isMatch(project.get())
}