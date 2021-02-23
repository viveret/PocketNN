package com.viveret.pocketn2.scenario.basis


import com.viveret.tinydnn.data.Scenario
import com.viveret.tinydnn.data.train.DataSliceReader

interface PredefinedScenario : Scenario {
    val data: DataSliceReader
}