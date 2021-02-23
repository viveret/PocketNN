package com.viveret.pocketn2.view.dialog

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.viveret.pocketn2.R
import com.viveret.pocketn2.view.model.DismissReason
import com.viveret.pocketn2.view.model.MinimizableDialog
import com.viveret.pocketn2.view.widget.DynamicDrawable
import com.viveret.tinydnn.util.AppLifecycleContext
import com.viveret.tinydnn.util.async.AppLifecycleListener
import com.viveret.tinydnn.util.async.Observable
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import org.jetbrains.anko.runOnUiThread
import kotlin.math.roundToLong


abstract class AbstractProgressDialog<T>(context: AppLifecycleContext) : Observable, AppLifecycleListener, MinimizableDialog, DynamicDrawable where T: ViewBinding {
    override val progression: Double
        get() = progressRatio

    // fragment_dialog_progress
    protected abstract fun createBinding(layoutInflater: LayoutInflater, container: ViewGroup): T

    override fun addListener(listener: OnItemSelectedListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: OnItemSelectedListener) {
        listeners.remove(listener)
    }

    override val visible: Boolean
        get() = myIsShowing

    override fun hideDialog() {
        if (isShowing) {
            dismiss(DismissReason.Success)
        }
    }

    override fun showDialog() {
        if (!isShowing) {
            inflateDialog()
        }
    }

    fun onDismiss() {
        myIsShowing = false
        listeners.forEach { it.onSelected(this@AbstractProgressDialog).callback() }
    }

    override fun onCreate() {
    }

    override fun onStart() {
    }

    override fun onResume() {
    }

    override fun onPause() = hideDialog()

    override fun onStop() = this.dialog.dismiss()

    override fun onDestroy() = this.dialog.dismiss()

    protected lateinit var binding: T
    private lateinit var layoutInflater: LayoutInflater

    lateinit var view: View
    private val activity: AppLifecycleContext = context
    // TODO: ads or helpful hints

    abstract val messageWasUpdated: Boolean
    abstract val messageStringResId: Int
    abstract val titleStringResId: Int
    open val updatedMessage = ""

    private lateinit var percentTV: TextView
    private lateinit var timeLeftTV: TextView
    private lateinit var messageView: TextView
    private lateinit var totalProgressBar: ProgressBar
    private lateinit var dialog: AlertDialog

    var progressRatio: Double = 0.0
    override var dismissReason = DismissReason.Minimize
    override val listeners = ArrayList<OnItemSelectedListener>()
    private var myIsShowing = false
    private val timeStarted = java.util.Date()
    private var timeLastUpdated = timeStarted
    private var progressLeftSum = 0.0
    private var progressLeftSampleCount = 0
    private var timeForSum = 0L
    private var timeLeftDots = 0
    private val timeLeftDotFrames = arrayOf("", ".", "..", "...", " ..", "  .")

    var max: Int
        get() = totalProgressBar.max
        set(value) {
            totalProgressBar.max = value
        }

    var progress: Int
        get() = totalProgressBar.progress
        set(value) {
            totalProgressBar.progress = value
            updateText()
        }

    var indeterminate: Boolean
        get() = totalProgressBar.isIndeterminate
        set(value) {
            totalProgressBar.isIndeterminate = value
        }

    fun show(layoutInflater: LayoutInflater): Dialog {
        this.layoutInflater = layoutInflater
        return inflateDialog()
    }

    private fun inflateDialog(): AlertDialog {
        binding = createBinding(layoutInflater, FrameLayout(activity.context))
        // val binding = ActivitySandboxBinding.inflate(layoutInflater, null, false)
        this.view = binding.root
        this.messageView = this.view.findViewById(R.id.message)
        this.percentTV = this.view.findViewById(R.id.percent)
        this.timeLeftTV = this.view.findViewById(R.id.timeLeft)

        this.totalProgressBar = this.view.findViewById(R.id.progressTotal)

        val builder = AlertDialog.Builder(activity.context)
        builder.apply {
            setCancelable(minimizable)
            setView(view)
            setNeutralButton(R.string.action_minimize) { _, _ -> dismiss() }
            setNegativeButton(R.string.action_stop) { _, _ -> dismiss(DismissReason.Cancel) }
            setOnDismissListener { onDismiss() }
        }

        this.dialog = builder.create()
        this.dialog.setCanceledOnTouchOutside(minimizable)

        this.dialog.setTitle(titleStringResId)
        this.dialog.setMessage(activity.context.getString(messageStringResId))
        // ret.setTheme(R.style.AppTheme_CitrusDialog)
        this.dialog.show()
        myIsShowing = true
        return this.dialog
    }

    fun setProgressStyle(style: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        // isIndeterminate = false
    }

    fun dismiss(reason: DismissReason = DismissReason.Minimize) {
        this.dismissReason = reason
        if (isShowing) {
            this.activity.context.runOnUiThread {
                dialog.dismiss()
            }
        } else {
            onDismiss()
        }
    }

    val isShowing: Boolean
        get() = this.myIsShowing

    val canceled: Boolean
        get() = this.dismissReason == DismissReason.Cancel

    init {
        context.addListener(this)
    }

    private fun updateText() {
        if (messageWasUpdated) {
            val updatedMessage = this.updatedMessage
            this.messageView.text = updatedMessage
            this.dialog.setMessage(updatedMessage)
        }

        val currentTime = java.util.Date()
        val elapsedTime = currentTime.time - this.timeStarted.time
        val dt = currentTime.time - this.timeLastUpdated.time // 55 - 65
        timeForSum += dt

        this.progressRatio = totalProgressBar.progress.toDouble() / totalProgressBar.max
        val progressLeft = (totalProgressBar.max - totalProgressBar.progress)
        progressLeftSum += progressLeft
        progressLeftSampleCount++

        if (timeForSum > 5000 && totalProgressBar.progress > 0) {
            val progressLeftAverage = progressLeftSum / progressLeftSampleCount
            val averageTimeToComplete = java.util.Date((progressLeftAverage * elapsedTime / (totalProgressBar.progress)).roundToLong())
            averageTimeToComplete.hours = averageTimeToComplete.hours - 16

            var timeLeftText = if (averageTimeToComplete.hours > 0) {
                "${averageTimeToComplete.hours} hrs and ${averageTimeToComplete.minutes} min left"
            } else if (averageTimeToComplete.minutes > 0) {
                "${averageTimeToComplete.minutes} min and ${averageTimeToComplete.seconds} sec left"
            } else {
                "${averageTimeToComplete.seconds} sec left"
            }
            timeLeftText = "$timeLeftText${timeLeftDotFrames[timeLeftDots]}"
            this.timeLeftTV.text = timeLeftText

            progressLeftSum = 0.0
            progressLeftSampleCount = 0
            timeForSum -= 5000

            timeLeftDots++
            if (timeLeftDots >= timeLeftDotFrames.size) {
                timeLeftDots = 0
            }
        }

        this.percentTV.text = "${totalProgressBar.progress} / ${totalProgressBar.max} (${(progressRatio * 100.0f).roundToLong()}%)"
        this.timeLastUpdated = currentTime
    }
}