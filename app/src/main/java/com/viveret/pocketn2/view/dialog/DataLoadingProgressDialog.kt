package com.viveret.pocketn2.view.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentDialogProgressBinding
import com.viveret.tinydnn.util.AppLifecycleContext
import kotlin.math.roundToInt

class DataLoadingProgressDialog(context: AppLifecycleContext): GenericProgressDialog(context) {
    override val minimizable: Boolean = true
    override val title: String
        get() = "Loading ${(progressRatio * 100.0f).roundToInt()}%"

    override val icon: Int = R.drawable.ic_sync_white_24dp
    override val messageWasUpdated: Boolean = false
    override val messageStringResId: Int = R.string.loading_msg
    override val titleStringResId: Int = R.string.loading_msg
}