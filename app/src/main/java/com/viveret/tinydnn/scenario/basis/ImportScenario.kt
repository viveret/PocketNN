package com.viveret.tinydnn.scenario.basis

import java.util.*

interface ImportScenario : InteractiveScenario {
    val permissions: Array<String>
    val dataFormat: UUID
}