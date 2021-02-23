package com.viveret.pocketn2.view.activities

import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.ConsoleMessage
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.R
import com.viveret.pocketn2.async.MenuItemSelectedListener
import com.viveret.pocketn2.scenario.basis.PredefinedScenario
import com.viveret.pocketn2.view.dialog.DataLoadingProgressDialog
import com.viveret.pocketn2.view.dialog.TrainingProgressDialog
import com.viveret.pocketn2.view.fragments.challenge.JudgeFragment
import com.viveret.pocketn2.view.fragments.data.DataSuiteDetailFragment
import com.viveret.pocketn2.view.fragments.data.ListDataViewerFragment
import com.viveret.pocketn2.view.fragments.data.SliceDataViewerFragment
import com.viveret.pocketn2.view.fragments.predict.SelectDataMethodFragment
import com.viveret.pocketn2.view.fragments.project.AddLayerFragment
import com.viveret.pocketn2.view.fragments.project.MessagesFragment
import com.viveret.pocketn2.view.fragments.sandbox.ChooseScenarioFragment
import com.viveret.pocketn2.view.fragments.sandbox.ProjectActionsFragment
import com.viveret.pocketn2.view.fragments.sandbox.TrainingMethodFragment
import com.viveret.pocketn2.view.model.DismissReason
import com.viveret.tinydnn.basis.*
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.DataValues
import com.viveret.tinydnn.data.Scenario
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.data.train.TrainingMethod
import com.viveret.tinydnn.enums.TrainResult
import com.viveret.tinydnn.error.NNException
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.error.UserPropagatedException
import com.viveret.tinydnn.project.INeuralNetworkObserver
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.project.ProjectViewController
import com.viveret.tinydnn.project.actions.ChangeWeightsAction
import com.viveret.tinydnn.project.actions.JudgeResults
import com.viveret.tinydnn.project.actions.ProjectAction
import com.viveret.tinydnn.project.actions.TrainingResults
import com.viveret.tinydnn.scenario.basis.InteractiveScenario
import com.viveret.tinydnn.scenario.basis.SkippableInteractiveScenario
import com.viveret.tinydnn.util.async.OnSelectedResult
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.EOFException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.roundToLong

abstract class ProjectActivity : BasisActivity(),
        ProjectViewController, INeuralNetworkObserver, MenuItemSelectedListener {
    protected lateinit var currentMode: String
    private var currentTrainingMethod: TrainingMethod? = null

    override fun switchToMode(mode: String) = switchToMode(mode) { }

    override fun switchToMode(mode: String, args: (Bundle) -> Unit) {
        val frag = getFragmentForMode(mode)
        if (frag.arguments == null) {
            frag.arguments = Bundle()
        }
        args(frag.requireArguments())
        this.currentMode = mode
        switchToFragment(frag)
    }

    open fun getFragmentForMode(mode: String): Fragment {
        return when (mode) {
            MODE_ADD_LAYER -> AddLayerFragment.newInstance()
            MODE_MESSAGES -> MessagesFragment.newInstance()
            MODE_TRAIN -> TrainingMethodFragment.newInstance()
            MODE_JUDGE -> SelectDataMethodFragment.newInstance()
            MODE_PROJECT_ACTIONS -> ProjectActionsFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid mode $mode")
        }
    }

    override fun onSelected(item: Any): OnSelectedResult {
        return when (item) {
            is Scenario -> {
                when (this.currentMode) {
                    MODE_TRAIN -> this.trainUsingScenario(item)
                    MODE_PREDICT -> this.predictUsingScenario(item)
                    MODE_JUDGE -> this.judgeUsingScenario(item)
                    else -> return OnSelectedResult(false)
                }
                OnSelectedResult(true)
            }
            is TrainingMethod -> {
                this.trainUsingMethod(item)
                OnSelectedResult(true)
            }
            is DataMethod -> {
                when (this.currentMode) {
                    MODE_PREDICT -> this.predictUsingDataMethod(item)
                    MODE_JUDGE -> this.judgeUsingDataMethod(item)
                    else -> return OnSelectedResult(false)
                }
                OnSelectedResult(true)
            }
            is Vect -> {
                this.predictOutputFromInput(item)
                OnSelectedResult(true)
            }
            is DataSlice -> {
                this.predictOutputFromInput(item[DataRole.Input]!!.first)
                OnSelectedResult(true)
            }
            is TrainingResults -> {
                runOnUiThread {
                    this.askJudge(R.string.judge_training_title, R.string.judge_training_msg)
                }
                OnSelectedResult(true)
            }
            is JudgeResults -> {
                val msg = when {
                    item.tallyGood > item.trainingData.elementCount * 0.975 -> "Nice job, A+!"
                    item.tallyGood > item.trainingData.elementCount * 0.95 -> "Nice job, A!"
                    item.tallyGood > item.trainingData.elementCount * 0.9 -> "Nice job, A-!"
                    item.tallyGood > item.trainingData.elementCount * 0.875 -> "Okay job, B+"
                    item.tallyGood > item.trainingData.elementCount * 0.85 -> "Okay job, B"
                    item.tallyGood > item.trainingData.elementCount * 0.8 -> "Okay job, B-"
                    item.tallyGood > item.trainingData.elementCount * 0.775 -> "Mediocre job, C+"
                    item.tallyGood > item.trainingData.elementCount * 0.75 -> "Mediocre job, C"
                    item.tallyGood > item.trainingData.elementCount * 0.7 -> "Mediocre job, C-"
                    else -> "F is for Failure"
                }

                runOnUiThread {
                    val accuracy = item.tallyGood * 100.0 / item.trainingData.elementCount
                    when {
                        accuracy.isNaN() -> message(ConsoleMessage.MessageLevel.ERROR, "Error! Accuracy is corrupt!")
                        accuracy > 99.25 -> message(ConsoleMessage.MessageLevel.LOG, "Wow! ${accuracy.roundToLong()}% Accuracy!")
                        else -> message(ConsoleMessage.MessageLevel.LOG, "$msg (${accuracy.roundToLong()}% Accuracy)")
                    }
                }
                OnSelectedResult(true)
            }
            is ProjectAction -> item.doAction(this.project!!)
            is Fragment -> {
                runOnUiThread {
                    switchToFragment(item)
                }
                OnSelectedResult(true)
            }
            else -> super.onSelected(item)
        }
    }

    override fun selectDataValueFromSet(data: DataSliceReader) {
        when (this.currentMode) {
            MODE_JUDGE -> judgeUsingData(data)
            MODE_TRAIN -> this.loadData(data, Int.MAX_VALUE,
                    { trainOnData(it) },
                    { /*if (data is InputDataValueStream) data.inputSelection.values.forEach { it.source.delete(DataSource.LocalFile)}*/ })
            MODE_PREDICT -> {
                val dm = DataManager.get(this)
                val streamId = dm.putDataValueStream(data)
                val view = dm.getViewForMime(data.getString(DataAttr.MIME)
                        ?: error("MIME not available"))
                this.switchToFragment(ListDataViewerFragment.newInstance(streamId, view))
            }
            else -> throw java.lang.IllegalArgumentException("Invalid mode $currentMode")
        }
    }

    private fun loadData(data: DataSliceReader, count: Int, callback: (data: DataValues) -> Unit, callbackError: (exception: Exception) -> Unit) {
        val dialog = DataLoadingProgressDialog(this.appLifecycleContext)
        dialog.addListener(this)
        this.loadDataAndShowProgress(dialog, data, count, callback, callbackError)
    }

    private fun loadDataAndShowProgress(dialog: DataLoadingProgressDialog, data: DataSliceReader, count: Int, callback: (data: DataValues) -> Unit, callbackError: (exception: Exception) -> Unit) {
        dialog.show(this.layoutInflater)
        var amountToRead = min(count, data.getInt(DataAttr.ElementCount) ?: Int.MAX_VALUE)
        if (amountToRead == Int.MAX_VALUE) {
            loadDataAndShowProgressIndeterminant(amountToRead, dialog, data, callback, callbackError)
        } else if (amountToRead <= 0 || data.getInt(DataAttr.ElementByteSize)!! * amountToRead > Int.MAX_VALUE / 2) {
            throw Exception("Invalid amount to read: $amountToRead")
        } else {
            if (currentTrainingMethod != null) {
                amountToRead = max(1, (currentTrainingMethod!!.percentToInclude * amountToRead).roundToInt())
            }
            loadDataAndShowProgressDeterminant(amountToRead, dialog, data, callback, callbackError)
        }
    }

    // Get a MemoryInfo object for the device's current memory status.
    private fun getAvailableMemory(): ActivityManager.MemoryInfo {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return ActivityManager.MemoryInfo().also { memoryInfo ->
            activityManager.getMemoryInfo(memoryInfo)
        }
    }

    private fun loadDataAndShowProgressDeterminant(amountToRead: Int, dialog: DataLoadingProgressDialog, data: DataSliceReader, callback: (data: DataValues) -> Unit, callbackError: (exception: Exception) -> Unit) {
        dialog.max = amountToRead
        val effectiveBytesNeeded = data.getInt(DataAttr.ElementByteSize)!! * amountToRead
        val maxMemory = getAvailableMemory().availMem * 1000 * 1000

        if (effectiveBytesNeeded >= maxMemory) {
            throw UserException("$effectiveBytesNeeded bytes needed to load data, but $maxMemory is max")
        }

        doAsync {
            val slices = DataValues(data.openRoles, amountToRead)
            var position = 0
            try {
                var isNotCanceled = true
                var speed = 1
                var speedAcceleration = 2
                var lastTime = Date()

                while (position < amountToRead && isNotCanceled) {
                    val tmp = data.read(slices, position, min(speed, amountToRead - speed))
                    if (tmp <= 0) {
                        isNotCanceled = false
                    } else {
                        position += tmp
                    }
                    uiThread { dialog.progress = position; if (dialog.canceled) isNotCanceled = false; }

                    val newTime = Date()
                    val span = newTime.time - lastTime.time
                    if (span > 50 && speedAcceleration > 1) {
                        speedAcceleration--
                    } else if (speedAcceleration < 1000) {
                        speedAcceleration++
                    }

                    speed += speedAcceleration
                    lastTime = newTime
                }
            } catch (e: UserException) {
                runOnUiThread {
                    message(e, "Could not train")
                    dialog.dismiss(DismissReason.Error)
                }
            } catch (e: NNException) {
                runOnUiThread {
                    message(e, "Could not train")
                    dialog.dismiss(DismissReason.Error)
                }
            } catch (eof: EOFException) {
                runOnUiThread {
                    dialog.dismiss(DismissReason.Success)
                }
            } finally {
                runOnUiThread {
                    dialog.dismiss(DismissReason.Success)
                }
            }

            if (position == amountToRead || amountToRead == Int.MAX_VALUE) {
                runOnUiThread {
                    try {
                        callback(slices)
                    } catch (e: UserException) {
                        callbackError(e)
                    }
                }
            }
        }
    }

    private fun loadDataAndShowProgressIndeterminant(amountToRead: Int, dialog: DataLoadingProgressDialog, data: DataSliceReader, callback: (data: DataValues) -> Unit, callbackError: (exception: Exception) -> Unit) {
        dialog.max = amountToRead

        doAsync {
            val slices = ArrayList<DataSlice>()
            //val buffer = Array<DataSlice>(10) { }

            try {
                var isNotCanceled = true
                while (slices.size < amountToRead && isNotCanceled) {
//                    val tmp = data.read(buffer, 0, buffer.size)
//                    if (tmp < buffer.size) {
//                        isNotCanceled = false
//                        slices.addAll(buffer.take(tmp))
//                    } else {
//                        slices.addAll(buffer)
//                    }
                    uiThread { dialog.progress = slices.size; if (dialog.canceled) isNotCanceled = false; }
                }
            } catch (e: UserException) {
                runOnUiThread {
                    message(e, "Could not train")
                    dialog.dismiss(DismissReason.Error)
                }
            } catch (e: NNException) {
                runOnUiThread {
                    message(e, "Could not train")
                    dialog.dismiss(DismissReason.Error)
                }
            } catch (eof: EOFException) {

            } finally {
                runOnUiThread {
                    dialog.dismiss(DismissReason.Success)
                }
            }

            if (slices.size > 0) {
                runOnUiThread {
                    try {
                        callback(DataValues())
                    } catch (e: UserException) {
                        callbackError(e)
                    }
                }
            }
        }
    }

    override fun trainOnData(trainingData: DataValues) {
        if (trainingData.containsKey(DataRole.FitTo)) {
            this.currentMode = MODE_TRAIN
            val config = this.currentTrainingMethod!!
            val dialog = TrainingProgressDialog(this.appLifecycleContext)
            dialog.addListener(this)
            this.trainOnDataShowProgress(trainingData, dialog, config)
        } else {
            throw UserException("Invalid data")
        }
    }

    override fun trainUsingMethod(method: TrainingMethod) {
        val scenarios = DataManager.get(this).getScenariosForDataMethod(method.dataMethod)
        if (scenarios.isNotEmpty()) {
            this.currentMode = MODE_TRAIN
            this.currentTrainingMethod = method
            this.switchToFragment(ChooseScenarioFragment.newInstance(method))
        } else {
            throw UserException("No scenarios for data method")
        }
    }

    override fun trainUsingScenario(scenario: Scenario) {
        this.currentMode = MODE_TRAIN
        this.resolveDataFromScenario(scenario, this.currentTrainingMethod!!.fitToOutput)
    }

    private fun trainOnDataShowProgress(trainingData: DataValues, dialog: TrainingProgressDialog, config: TrainingMethod) {
        dialog.show(this.layoutInflater)
        dialog.max = config.epochs

        doAsync {
            val neuralNetwork = project!!.get()
            val onEpochUpdate: () -> Unit = { uiThread { dialog.progress = neuralNetwork.epochAt(); if (dialog.canceled) neuralNetwork.stop_ongoing_training(); } }
            val trainResult = try {
                val trainResult = if (trainingData.containsKey(DataRole.FitTo)) {
                    neuralNetwork.fit(config.optimizer, trainingData[DataRole.Input]!!.vects, trainingData[DataRole.FitTo]!!.vects, config.batchSize, config.epochs, onEpochUpdate, { })
                } else {
                    neuralNetwork.train(config.optimizer, trainingData[DataRole.Input]!!.vects, emptyArray(), config.batchSize, config.epochs, onEpochUpdate, { })
                }
                trainResult
            } catch (e: UserException) {
                message(e, "Could not train")
                dialog.dismiss(DismissReason.Error)
                TrainResult.FAILURE
            } catch (e: NNException) {
                message(e, "Could not train")
                dialog.dismiss(DismissReason.Error)
                TrainResult.FAILURE
            } finally {
                dialog.dismiss(DismissReason.Success)
            }

            project!!.notifyObservers(ChangeWeightsAction())
            if (trainResult == TrainResult.SUCCEEDED) {
                onSelected(TrainingResults(trainResult, trainingData)).callback()
            }
        }
    }

    override fun predictOutputFromInput(data: Vect) {
        this.currentMode = MODE_PREDICT
        try {
            val outputData = project?.get()?.predict(data)!!
            val slice = DataSlice(DataRole.FitTo to (outputData to -1L)) // DataRole.Input to (data to -1L),
            val outputDataId = DataManager.get(this).putData(slice)
            this.switchToFragment(SliceDataViewerFragment.newInstance(outputDataId, null))
        } catch (e: UserException) {
            message(e, "Could not predict")
        } catch (e: NNException) {
            message(e, "Could not predict")
        }
    }

    fun askJudge(title: Int, msg: Int) {
        getDrawerLayout().closeDrawer(GravityCompat.START)

        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int -> switchToMode(MODE_JUDGE) }
                .setNegativeButton(R.string.no) { _: DialogInterface, _: Int -> }
                .create().show()
    }

    override fun judgeUsingScenario(scenario: Scenario) {
        this.currentMode = MODE_JUDGE
        this.resolveDataFromScenario(scenario, true)
    }

    override fun judgeUsingDataMethod(dataMethod: DataMethod) {
        val scenarios = DataManager.get(this).getScenariosForDataMethod(dataMethod)
        if (scenarios.isNotEmpty()) {
            this.currentMode = MODE_JUDGE
            this.switchToFragment(ChooseScenarioFragment.newInstance(dataMethod))
        } else {
            throw UserException("No scenarios for data method")
        }
    }

    override fun judgeUsingData(data: DataSliceReader) = runOnUiThread {
        JudgeFragment.newInstance(data, this)
    }

    override fun message(msgLvL: ConsoleMessage.MessageLevel, message: String) {
        runOnUiThread {
            AlertDialog.Builder(this)
                    .setTitle(msgLvL.name)
                    .setMessage(message)
                    .setPositiveButton(R.string.close) { _: DialogInterface, _: Int -> }
                    .setNegativeButton(R.string.action_keep_message) { _: DialogInterface, _: Int ->
                        project?.saveMessage(msgLvL, message)
                    }
                    .create().show()
        }
    }

    override fun message(exception: Exception, message: String) {
        //var ex = if (exception is UserException) exception.cause as Exception else exception
        this.message(ConsoleMessage.MessageLevel.ERROR, "$message\n${exception.localizedMessage}")
        Log.e("com.viveret.pocketn2", message, exception)
    }

    override fun onNeuralNetworkChange(project: NeuralNetProject, event: ProjectAction) {

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val ret = onActionSelected(id)
        getDrawerLayout().closeDrawer(GravityCompat.START)
        return ret
    }

    override fun onActionSelected(id: Int): Boolean {
        val frag: Fragment? = null
        when (id) {
            R.id.nav_home -> {
                goHome()
                return true
            }
            R.id.nav_project_actions -> switchToMode(MODE_PROJECT_ACTIONS)
            R.id.nav_nn_train -> switchToMode(MODE_TRAIN)
            R.id.nav_nn_predict -> switchToMode(MODE_PREDICT)
            R.id.nav_nn_messages -> switchToMode(MODE_MESSAGES)
            R.id.nav_share -> {
            }
            R.id.nav_emmlipedia -> {
            }
            R.id.nav_browse_data -> {
                val i = Intent(this, DataSuiteListActivity::class.java)
                i.putExtra(DataSuiteDetailFragment.ARG_PROJECT_ID, this.project!!.id.toString())
                startActivity(i)
                return true
            }
        }

        if (frag != null) {
            this.showFragment(frag)
        }

        return true
    }

    protected fun resolveDataFromScenario(scenario: Scenario, fitToValues: Boolean, maxCount: Int = Int.MAX_VALUE) {
        if (scenario.compatibleWithNetwork(project!!)) {
            when (scenario) {
                is InteractiveScenario -> {
                    if (scenario is SkippableInteractiveScenario && scenario.hasData) {
                        try {
                            selectDataValueFromSet(scenario.openData(fitToValues))
                        } catch (e: Exception) {
                            Log.e("com.viveret.pocketn2", "Could not resolve data using scenario $scenario", e)
                            retry(R.string.title_retry_get_scenario, R.string.msg_retry_get_scenario,
                                    otherOptions = arrayOf(R.string.action_cancel, R.string.action_retry),
                                    onItemSelected = { item ->
                                        when (item) {
                                            R.string.action_cancel -> throw UserPropagatedException(e)
                                            R.string.action_retry -> {
                                                scenario.deleteData()
                                                resolveDataFromScenario(scenario, fitToValues, maxCount)
                                            }
                                        }
                                    })
                        }
                    } else {
                        this.switchToInteractiveScenario(scenario, fitToValues)
                    }
                }
                is PredefinedScenario -> {
                    this.selectDataValueFromSet(scenario.data)
                }
                else -> throw UserException("Invalid scenario type")
            }
        } else {
            throw UserException("Scenario not compatible with project")
        }
    }

    private fun switchToInteractiveScenario(scenario: InteractiveScenario, fitTo: Boolean) {
        val f = scenario.fragmentType.newInstance() as Fragment
        val args = Bundle()
        args.putString("scenarioId", scenario.id.toString())
        args.putBoolean("fitToOutput", fitTo)
        f.arguments = args
        this.switchToFragment(f)
    }

    companion object {
        const val MODE_ADD_LAYER = "ADD_LAYER"
        const val MODE_TRAIN = "TRAIN"
        const val MODE_PREDICT = "PREDICT"
        const val MODE_JUDGE = "JUDGE"
        const val MODE_MESSAGES = "MESSAGES"
        const val MODE_PROJECT_ACTIONS = "PROJECT_ACTIONS"
    }
}