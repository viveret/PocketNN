package com.viveret.tinydnn.scenario.binaryfile.mnist

import com.viveret.tinydnn.R
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.model.CommonNetworkFilters
import com.viveret.tinydnn.scenario.basis.OnlineScenarioBase
import com.viveret.tinydnn.project.NeuralNetProject
import java.util.*

class MNISTOnlineScenario : OnlineScenarioBase(R.string.mnist) {
    override val summary: String = "Train to detect a number in an image"
    override val source: String = "Yann LeCun, Courant Institute, NYU"

    override val id = UUID.fromString("539dcfb2-4151-11e9-b210-d663bd873d93")
    override val dataMethod = DataMethod.BinaryFile
    override val dataFormat = UUID.fromString("44f43102-3054-4602-a10c-7f3c0d8cd3f7")!!
    override val dataSuite = UUID.fromString("45b98b47-69a0-4405-b3ca-6f91887a9d61")!!

    override fun compatibleWithNetwork(project: NeuralNetProject) = CommonNetworkFilters.MNIST.isMatch(project.get())
}