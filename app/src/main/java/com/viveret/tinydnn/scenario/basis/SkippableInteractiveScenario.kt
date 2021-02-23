package com.viveret.tinydnn.scenario.basis

import com.viveret.tinydnn.data.train.DataSliceReader

interface SkippableInteractiveScenario : InteractiveScenario {
    val hasData: Boolean
    fun openData(fitTo: Boolean): DataSliceReader
    fun deleteData()
}