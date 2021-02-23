package com.viveret.pocketn2.view.fragments.scenario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentTrainManualBinding
import com.viveret.pocketn2.view.fragments.basis.ScenarioFragment
import com.viveret.tinydnn.basis.Vect
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.util.async.OnSelectedResult

class ManualConfigFragment : ScenarioFragment<FragmentTrainManualBinding>() {
    override val dismissAfterSubmit: Boolean = true
    private val inputString = "0,0\n0,1\n1,0\n1,1"
    private val outputStringOr = "0\n1\n1\n1"
    private val outputStringAnd = "0\n0\n0\n1"
    private val outputStringXor = "0\n1\n1\n0"

    // TrainingScenarioDataListener
    override val initialExpansionState: Int? = null

    private fun selectMenu(id: Int): Boolean {
        when (id) {
            R.id.action_train_binary_or -> this.setInputsAndOutputs(this.inputString, this.outputStringOr)
            R.id.action_train_binary_xor -> this.setInputsAndOutputs(this.inputString, this.outputStringXor)
            R.id.action_train_binary_and -> this.setInputsAndOutputs(this.inputString, this.outputStringAnd)
        }
        return true
    }

    private fun setInputsAndOutputs(inputString: String, outputString: String) {
        binding!!.etInputs.setText(inputString)
        binding!!.etOutputs.setText(outputString)
    }

    override fun submit(): OnSelectedResult {
        return try {
//            val inLabels = ArrayList<Long>()
//            val inValues = extractValues(this.etInputs, 2)
//            val
//
//            val trainingData = if (this.fitToOutput) {
//                val outLabels = ArrayList<Long>()
//                val outVals = extractValues(this.etOutputs, 1)
//                TrainingDataValues(inValues, inLabels, outVals, outLabels)
//            } else {
//                TrainingDataValues(inValues, inLabels)
//            }
//            this.submitData(trainingData)
            OnSelectedResult(true)
        } catch (e: Exception) {
            OnSelectedResult(false)
        }
    }

    private fun extractValues(v: EditText, requiredSize: Int): ArrayList<Vect> {
        val out = ArrayList<Vect>()
        val inputsStr = v.text.toString()
        for (row in inputsStr.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val valStrings = row.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (valStrings.size != requiredSize && requiredSize > 0) {
                val msg = "Input length ${if (valStrings.size < requiredSize) "smaller" else "greater"} than amount required ($requiredSize)"
                throw UserException(msg, IllegalArgumentException(msg), v)
            }

            val vals = FloatArray(valStrings.size)
            for (i in vals.indices) {
                vals[i] = java.lang.Float.parseFloat(valStrings[i].trim { it <= ' ' })
            }
            out.add(Vect(vals, vals.size))
        }
        return out
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentTrainManualBinding? {
        val binding = FragmentTrainManualBinding.inflate(inflater, container, false)
        //binding.etOutputs = v!!.findViewById(R.id.etOutputs)
        //binding.etInputs = v.findViewById(R.id.etInputs)

        binding.etOutputs.visibility = if (this.fitToOutput) View.VISIBLE else View.INVISIBLE

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