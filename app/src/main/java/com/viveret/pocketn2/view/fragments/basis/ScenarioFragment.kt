package com.viveret.pocketn2.view.fragments.basis

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.viveret.pocketn2.DataManager
import com.viveret.tinydnn.data.Scenario
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.util.async.OnSelectedResult
import java.util.*

abstract class ScenarioFragment<T : ViewBinding> : BottomSheetFormFragment<T>() {
    var fitToOutput = false
    lateinit var scenario: Scenario
    abstract val dismissAfterSubmit: Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            this.initFromArgs(requireArguments())
        }
    }

    private fun initFromArgs(arguments: Bundle) {
        if (arguments.containsKey("fitToOutput")) {
            this.fitToOutput = arguments.getBoolean("fitToOutput")
        }
        if (arguments.containsKey("scenarioId")) {
            this.scenario = DataManager.get(this.requireContext()).getScenario(UUID.fromString(arguments.getString("scenarioId")))
        }
    }

    fun submitData(data: DataSliceReader): OnSelectedResult = OnSelectedResult(dismissAfterSubmit) {
        projectProvider!!.selectDataValueFromSet(data)
    }
}