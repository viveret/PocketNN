package com.viveret.pocketn2.view.fragments.challenge

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.widget.TextView
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentDialogJudgeBinding
import com.viveret.pocketn2.view.activities.ProjectActivity
import com.viveret.pocketn2.view.dialog.AbstractProgressDialog
import com.viveret.pocketn2.view.model.DismissReason
import com.viveret.tinydnn.basis.DataAttr
import com.viveret.tinydnn.basis.DataRole
import com.viveret.tinydnn.basis.DataSource
import com.viveret.tinydnn.basis.HostedStreamPackage
import com.viveret.tinydnn.data.DataValues
import com.viveret.tinydnn.data.challenge.ChallengeMetaInfo
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.error.NNException
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.project.ProjectViewController
import com.viveret.tinydnn.project.actions.ChangeWeightsAction
import com.viveret.tinydnn.project.actions.JudgeResults
import com.viveret.tinydnn.util.AppLifecycleContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import kotlin.math.roundToLong

class JudgeFragment : AbstractProgressDialog<FragmentDialogJudgeBinding> {
    override val minimizable: Boolean = true

    override val title: String
        get() = context.getString(titleStringResId)

    override val icon: Int
        get() = R.drawable.ic_check_black_24dp

    override val messageWasUpdated: Boolean = false
    override val messageStringResId: Int = R.string.online_train_data_message
    override val titleStringResId: Int = R.string.title_fragment_judge

    private val challenge: ChallengeMetaInfo?
    private val context: Context
    private val projectView: ProjectViewController

    constructor(context: AppLifecycleContext) : super(context) {
        this.context = context.context
        if (this.context is ProjectViewController) {
            this.projectView = this.context
        } else {
            throw Exception("Context must implement ProjectProvider")
        }

        this.challenge = null
    }

    constructor(context: AppLifecycleContext, challenge: ChallengeMetaInfo) : super(context) {
        this.context = context.context
        if (this.context is ProjectViewController) {
            this.projectView = this.context
        } else {
            throw Exception("Context must implement ProjectProvider")
        }

        this.challenge = challenge
        val judged = challenge.judgeSolution(this.projectView.project!!)
    }

    fun judgeAndShowProgress(inflater: LayoutInflater, trainingData: DataSliceReader) {
        // val _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        //val d = this.show(inflater, R.layout.fragment_dialog_judge)
        val tallyGood = this.view.findViewById<TextView>(R.id.tallyGood)
        var countGood = 0

        val totalCount = trainingData.getInt(DataAttr.ElementCount)
        if (totalCount != null) {
            this.max = totalCount.toInt()
        }

        this.context.doAsync {
            val neuralNetwork = projectView.project!!.get()
            try {
                val vectBuffer = DataValues(1, DataRole.Input, DataRole.FitTo)
                var vects = trainingData.read(vectBuffer, 0, 1)
                while (vects > 0) {
                    val testVal = vectBuffer.get(0)
                    val trainResult = neuralNetwork.predict(testVal[DataRole.Input]!!.first)
                    val percDiff = testVal[DataRole.FitTo]!!.first.correctPredictionOf(trainResult)
                    countGood += if (percDiff) 1 else 0

                    context.runOnUiThread {
                        progress++
                        tallyGood.text = "Accuracy: ${(countGood * 100.0 / progress).roundToLong()}% (${countGood}/${progress})"
                        if (canceled) neuralNetwork.stop_ongoing_training()
                    }
                    vects = trainingData.read(vectBuffer, 0, 1)
                }

                context.runOnUiThread {
                    progress++
                    tallyGood.text = "Accuracy: ${(countGood * 100.0 / progress).roundToLong()}% (${countGood}/${progress})"
                }
                val selectCallback = projectView.onSelected(JudgeResults(countGood, vectBuffer))

                dismiss(DismissReason.Success)
                // , onEpochUpdate, { }
                // , trainingData.fitTo.inputValues, config.batchSize, config.epochs,
                projectView.project!!.notifyObservers(ChangeWeightsAction())
                //projectView.message(ConsoleMessage.MessageLevel.LOG, "Train result: $trainResult")
                selectCallback.callback()
            } catch (e: UserException) {
                projectView.message(e, "Could not train")
            } catch (e: NNException) {
                projectView.message(e, "Could not train")
            } finally {
                // dismiss()
            }
        }
    }

    companion object {
        fun newInstance(trainingData: DataSliceReader, activity: ProjectActivity): JudgeFragment {
            val judgeFragment = JudgeFragment(activity.appLifecycleContext)
            judgeFragment.judgeAndShowProgress(activity.layoutInflater, trainingData)
            return judgeFragment
        }

        fun newInstance(challenge: ChallengeMetaInfo, activity: ProjectActivity): ChallengeFragment {
            val fragment = ChallengeFragment()
            val args = Bundle()
            args.putString("challenge_id", challenge.id.toString())
            fragment.arguments = args

            val pkg = HostedStreamPackage.fromId(challenge.dataSuiteId)
            if (pkg.isAvailable(DataSource.LocalFile)) {
                val trainingData = pkg.open(DataSource.LocalFile, true, activity.project!!)
                val judgeFragment = JudgeFragment(activity.appLifecycleContext, challenge)
                judgeFragment.judgeAndShowProgress(activity.layoutInflater, trainingData)
            } else {
                activity.message(ConsoleMessage.MessageLevel.ERROR, "You must train with MNIST data before submitting solution")
            }

            return fragment
        }

        fun newInstance(challenge: HostedStreamPackage): ChallengeFragment {
            val fragment = ChallengeFragment()
            val args = Bundle()
            args.putString("challenge_id", challenge.id.toString())
            fragment.arguments = args
            return fragment
        }
    }

    override fun createBinding(layoutInflater: LayoutInflater, container: ViewGroup): FragmentDialogJudgeBinding {
        return FragmentDialogJudgeBinding.inflate(layoutInflater, container, false)
    }
}