package com.viveret.pocketn2.view.fragments.scenario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.viveret.pocketn2.ConfigurableIsCancelable
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentScenarioCanvasBinding
import com.viveret.pocketn2.view.fragments.basis.ScenarioFragment
import com.viveret.pocketn2.view.widget.VectView
import com.viveret.tinydnn.basis.ConstDataValueStream
import com.viveret.tinydnn.basis.DataRole
import com.viveret.tinydnn.basis.Vect
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.util.async.OnSelectedResult

class CanvasScenarioFragment : ScenarioFragment<FragmentScenarioCanvasBinding>(), ConfigurableIsCancelable {
    override val dismissAfterSubmit: Boolean = false
    override val initialExpansionState: Int? = BottomSheetBehavior.STATE_EXPANDED
    private lateinit var dataView: VectView

    override fun submit(): OnSelectedResult {
        val img = Vect(this.dataView.valueInfo.pixels.map { x -> x / 255.0f }.toFloatArray(), this.dataView.valueInfo.pixels.size)
        return try {
            val submitStream = ConstDataValueStream(if (this.fitToOutput) arrayOf(DataRole.Input, DataRole.FitTo) else arrayOf(DataRole.Input))
            submitStream.push(img, DataRole.Input)
            if (this.fitToOutput) {
                val fitToValue = this.extractValues(binding!!.fitToValue, this.projectProvider!!.project!!.get().out_data_size().toInt()).single()
                submitStream.push(fitToValue, DataRole.FitTo)
            }
            this.submitData(submitStream)
        } catch (e: Exception) {
            errorContainer.setError(e, binding!!.fragmentTitle)
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

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentScenarioCanvasBinding? {
        val binding = FragmentScenarioCanvasBinding.inflate(inflater, container, false)
        this.dataView = VectView(binding.root.context)
        binding.contentFrame.addView(this.dataView)
        this.dataView.setInputSize(projectProvider!!.project!!.get().in_data_size())
        this.dataView.attachFragment(this)

        binding.btnReset.setOnClickListener {
            this.dataView.reset()
        }

        if (this.fitToOutput) {
            (binding.fitToValue.parent as ViewGroup).visibility = View.VISIBLE
        }

        binding.fragmentTitle.setText(R.string.data_method_canvas)
        return binding
    }

    override fun setIsCancelable(value: Boolean) {
        isCancelable = value
    }
}