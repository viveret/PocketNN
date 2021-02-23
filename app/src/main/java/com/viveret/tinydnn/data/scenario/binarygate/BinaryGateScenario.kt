package com.viveret.tinydnn.data.scenario.binarygate

import android.content.Context
import com.viveret.pocketn2.scenario.basis.PredefinedScenario
import com.viveret.tinydnn.basis.*
import com.viveret.tinydnn.data.ConstDataStream
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.DataValues
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.project.NeuralNetProject
import java.util.*

class BinaryGateScenario(override var name: String, val outputs: List<Int>, override val id: UUID) : PredefinedScenario, InputStreamProvider {
    override fun open(context: Context): BetterInputStream {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val dataMethod = DataMethod.BinaryGate
    private lateinit var project: NeuralNetProject
    private lateinit var context: Context

    override val data: DataSliceReader
            get() {
                val inputs = arrayOf(Vect(arrayListOf(0, 0), 2), Vect(arrayListOf(0, 1), 2), Vect(arrayListOf(1, 0), 2), Vect(arrayListOf(1, 1), 2))
                val ret = DataValues({
                   when (it) {
                       DataRole.Input -> DataValues.Role(inputs, emptyArray())
                       DataRole.FitTo -> DataValues.Role(outputs.map { x -> Vect(arrayListOf(x), 1) }.toTypedArray(), emptyArray())
                       else -> error("Not supported $it")
                   }
                }, DataRole.Input, DataRole.FitTo)

                ret[DataRole.Input]!!.valueMetaInfo = ConstDataStream(context, this, "$name loadDataValues", "application/octet-stream", "bin", "tinydnn", DataRole.Input)
                ret[DataRole.Input]!!.labelMetaInfo = ConstDataStream(context, this, "$name labels", "application/octet-stream", "bin", "tinydnn", DataRole.InputLabels)
                return ConstDataValueStream(arrayOf(DataRole.Input, DataRole.InputLabels, DataRole.FitTo, DataRole.FitToLabels))
            }

    override fun init(project: NeuralNetProject, context: Context) {
        this.project = project
        this.context = context
    }

    override fun compatibleWithNetwork(project: NeuralNetProject) = project.get().in_data_size() == 2L && project.get().out_data_size() == 1L

    override val summary: String = "Train to mimic a(n) $name gate"
    override val source: String = "You are the input source"
}