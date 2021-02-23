package com.viveret.tinydnn.scenario.basis

import com.viveret.tinydnn.data.Scenario
import com.viveret.tinydnn.data.scenario.basis.InteractivityHint

interface InteractiveScenario : Scenario {
    val fragmentType: Class<*>
    val hinting: InteractivityHint
}