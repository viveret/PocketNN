package com.viveret.tinydnn.data.scenario.binaryfile

import com.viveret.tinydnn.R
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.scenario.basis.OnlineScenarioBase
import com.viveret.tinydnn.project.NeuralNetProject
import java.util.*

class Cifar10Scenario: OnlineScenarioBase(R.string.cifar10_classification) {
    override val summary: String = "Cifar-10 is a common dataset for object classification"
    override val source: String = "Online machine learning examples (the internet)"
    override val id: UUID = UUID.fromString("1761faf2-5879-11e9-8647-d663bd873d93")
    override val dataSuite: UUID = UUID.fromString("17620240-5879-11e9-8647-d663bd873d93")
    override val dataFormat: UUID = UUID.fromString("176200b0-5879-11e9-8647-d663bd873d93")
    override val dataMethod: DataMethod = DataMethod.BinaryFile

    override fun compatibleWithNetwork(project: NeuralNetProject): Boolean =
            project.get().in_data_size() == 1024L * 3 && project.get().out_data_size() == 10L // make sure in channels = 3 for rgb
}