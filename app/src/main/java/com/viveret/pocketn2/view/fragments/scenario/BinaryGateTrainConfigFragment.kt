package com.viveret.pocketn2.view.fragments.scenario

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.ToggleButton
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentTrainBinaryGateBinding
import com.viveret.pocketn2.view.fragments.basis.ScenarioFragment
import com.viveret.tinydnn.basis.ConstDataValueStream
import com.viveret.tinydnn.basis.DataRole
import com.viveret.tinydnn.data.DataValues
import com.viveret.tinydnn.util.async.OnSelectedResult

class BinaryGateTrainConfigFragment : ScenarioFragment<FragmentTrainBinaryGateBinding>() {
    override val dismissAfterSubmit: Boolean = false
    private lateinit var rowViews: LinearLayout
    private val rows = ArrayList<List<ToggleButton>>()
    override val initialExpansionState: Int? = null

    private fun addGateToggleButtons(inSize: Long, outSize: Long) {
        val destination = LinearLayout(this.context)
        val items = ArrayList<ToggleButton>()
        destination.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        for (i in 0 until inSize) {
            val b = ToggleButton(this.requireContext())
            destination.addView(b)
            items.add(b)
        }

        if (this.fitToOutput) {
            val inputToOutput = TextView(context)
            inputToOutput.text = SpannableString("\u2192")
            destination.addView(inputToOutput)
            for (i in 0 until outSize) {
                val b = ToggleButton(this.requireContext())
                destination.addView(b)
                items.add(b)
            }
        }
        rows.add(items)
        rowViews.addView(destination)
    }

    private fun selectMenu(id: Int): Boolean {
        when (id) {
            R.id.action_train_binary_or -> this.setDesiredOutputs(arrayOf(false, true, true, true))
            R.id.action_train_binary_xor -> this.setDesiredOutputs(arrayOf(false, true, true, false))
            R.id.action_train_binary_and -> this.setDesiredOutputs(arrayOf(false, false, false, true))
        }
        return true
    }

    private fun setDesiredOutputs(outputs: Array<Boolean>) {
        val inputs = arrayOf(arrayOf(false, false), arrayOf(false, true), arrayOf(true, false), arrayOf(true, true))

        for (i in outputs.indices) {
            this.setValues(this.rows[i], inputs[i], outputs[i])
        }
    }

    override fun submit(): OnSelectedResult {
        return try {
            val destination = DataValues()

            val roles = if (this.fitToOutput)
                arrayOf(DataRole.Input, DataRole.InputLabels, DataRole.FitTo, DataRole.FitToLabels)
            else
                arrayOf(DataRole.Input, DataRole.InputLabels)

            val submitStream = ConstDataValueStream(roles)
            //submitStream.push(gatherValues().map { r -> Vect(r.take(r.size - 1).toFloatArray()) }, emptyList(), DataRole.Input)

            if (this.fitToOutput) {
                val outLabels = emptyList<Long>()
                submitStream.push(emptyList(), outLabels, DataRole.FitTo)
            }

            return this.submitData(submitStream)
        } catch (e: Exception) {
            OnSelectedResult(false)
        }
    }

    private fun setValues(v: List<ToggleButton>, vals: Array<Boolean>, fitTo: Boolean) {
        for (i in vals.indices) {
            v[i].isChecked = vals[i]
        }
        if (this.fitToOutput) {
            v.last().isChecked = fitTo
        }
    }

    private fun gatherValues(): List<FloatArray> =
            rows.map { x -> FloatArray(x.size) { i -> if (x[i].isChecked) 1.0f else 0.0f } }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentTrainBinaryGateBinding? {
        val binding = FragmentTrainBinaryGateBinding.inflate(inflater, container, false)

        this.rowViews = binding.rows

        val network = projectProvider!!.project!!.get()

        this.addGateToggleButtons(network.in_data_size(), network.out_data_size())
        this.addGateToggleButtons(network.in_data_size(), network.out_data_size())
        this.addGateToggleButtons(network.in_data_size(), network.out_data_size())
        this.addGateToggleButtons(network.in_data_size(), network.out_data_size())

        val btnSelectTrainingDefaults = binding.btnSelectTrainingDefaults
        btnSelectTrainingDefaults.setOnClickListener {
            val popup = PopupMenu(btnSelectTrainingDefaults.context, btnSelectTrainingDefaults)
            val minflater = popup.menuInflater
            minflater.inflate(R.menu.train_binary_manual_gates, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener { item -> this.selectMenu(item.itemId) }
        }

        return binding
    }
}