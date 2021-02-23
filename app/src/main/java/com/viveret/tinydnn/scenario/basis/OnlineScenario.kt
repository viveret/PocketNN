package com.viveret.tinydnn.scenario.basis

import java.util.*

interface OnlineScenario : SkippableInteractiveScenario {
    val dataSuite: UUID
    val dataFormat: UUID
}