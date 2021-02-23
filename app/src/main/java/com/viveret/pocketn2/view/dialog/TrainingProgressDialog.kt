package com.viveret.pocketn2.view.dialog

import com.viveret.pocketn2.R
import com.viveret.tinydnn.util.AppLifecycleContext
import kotlin.math.roundToInt

class TrainingProgressDialog(context: AppLifecycleContext): GenericProgressDialog(context) {
    override val minimizable: Boolean = true
    override val title: String
        get() = "Training ${(progressRatio * 100.0f).roundToInt()}%"

    override val icon: Int = R.drawable.ic_fitness_center_white_24dp
    override val messageWasUpdated: Boolean = false
    override val messageStringResId: Int = R.string.training_waiting_0
    override val titleStringResId: Int = R.string.training_waiting_0
}