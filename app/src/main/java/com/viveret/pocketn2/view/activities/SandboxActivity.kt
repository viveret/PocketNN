package com.viveret.pocketn2.view.activities

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.R
import com.viveret.pocketn2.async.tasks.ProjectSaveAsyncTask
import com.viveret.pocketn2.databinding.ActivitySandboxBinding
import com.viveret.pocketn2.view.dialog.SaveProjectProgressDialog
import com.viveret.pocketn2.view.fragments.data.packaging.ChoosePackageGeneratorFragment
import com.viveret.pocketn2.view.fragments.data.packaging.GeneratePackageOptionsFragment
import com.viveret.pocketn2.view.fragments.knowledge.KnowledgeCatalogFragment
import com.viveret.pocketn2.view.fragments.knowledge.KnowledgeFileNavigationFragment
import com.viveret.pocketn2.view.fragments.knowledge.KnowledgeSourceFragment
import com.viveret.pocketn2.view.fragments.knowledge.OnlineKnowledgeFragment
import com.viveret.pocketn2.view.fragments.predict.SelectDataMethodFragment
import com.viveret.pocketn2.view.fragments.project.RenameProjectFragment
import com.viveret.pocketn2.view.fragments.sandbox.ChooseScenarioFragment
import com.viveret.pocketn2.view.fragments.sandbox.ChooseVisualizationFragment
import com.viveret.pocketn2.view.fragments.sandbox.SandboxFragment
import com.viveret.pocketn2.view.fragments.sandbox.SaveConfigFragment
import com.viveret.tinydnn.basis.DataRole
import com.viveret.tinydnn.basis.DataSource
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.DataValues
import com.viveret.tinydnn.data.SaveConfig
import com.viveret.tinydnn.data.Scenario
import com.viveret.tinydnn.data.io.OutputSelection
import com.viveret.tinydnn.data.io.SmartFileOutputStream
import com.viveret.tinydnn.data.knowledge.KnowledgeCatalogItem
import com.viveret.tinydnn.data.knowledge.basis.OnlineKnowledgeCatalogItem
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.data.transform.PackageGenerator
import com.viveret.tinydnn.enums.FileFormat
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.network.SequentialNetworkModelWithWeights
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.project.actions.*
import com.viveret.tinydnn.util.async.OnSelectedResult


class SandboxActivity : ProjectActivity() {
    private lateinit var titleFormat: String
    private lateinit var binding: ActivitySandboxBinding
    private var exitOnSave = false
    private var packageGenerator: PackageGenerator? = null
    private var packageGeneratorOptions: GeneratePackageAction? = null
    private var packageGeneratorOutput: OutputSelection? = null
    private var projectVisualizationMode = "default-horizontal"

    override fun getToolbar(): androidx.appcompat.widget.Toolbar = binding.toolbar

    override fun getDrawerLayout(): DrawerLayout = binding.drawerLayout

    override fun onActionSelected(id: Int): Boolean {
        return when (id) {
            R.id.nav_gen_package -> {
                switchToMode(MODE_GEN_PKG)
                true
            }
            R.id.nav_nn_learn -> {
                switchToMode(MODE_LEARN)
                true
            }
            R.id.action_knowledge_catalog -> {
                switchToFragment(KnowledgeCatalogFragment.newInstance(1))
                true
            }
            R.id.action_knowledge_file -> {
                switchToFragment(KnowledgeFileNavigationFragment.newInstance())
                true
            }
            else -> super.onActionSelected(id)
        }
    }

    override fun predictUsingScenario(scenario: Scenario) {
        this.currentMode = MODE_PREDICT
        this.resolveDataFromScenario(scenario, false)
    }

    override fun predictUsingDataMethod(dataMethod: DataMethod) {
        val scenarios = DataManager.get(this).getScenariosForDataMethod(dataMethod)
        if (scenarios.isNotEmpty()) {
            this.currentMode = MODE_PREDICT
            this.switchToFragment(ChooseScenarioFragment.newInstance(dataMethod))
        } else {
            throw UserException("No scenarios for data method")
        }
    }

    override fun onSelected(item: Any): OnSelectedResult {
        return when (item) {
            is Scenario -> {
                when (this.currentMode) {
                    MODE_GEN_PKG -> {
                        OnSelectedResult(true) { this.resolveDataFromScenario(item, packageGeneratorOptions!!.includeFitTo, packageGeneratorOptions!!.size) }
                    }
                    else -> super.onSelected(item)
                }
            }
            is SaveConfig -> {
                OnSelectedResult(true) {
                    if (item.isSaved) {
                        this.showSaveFileActions(item)
                    } else {
                        this.saveAndShowProgress(item)
                    }
                }
            }
            is GeneratePackageAction -> {
                packageGeneratorOptions = item
                packageGeneratorOutput = packageGenerator!!.begin(item)
                this.switchToFragment(SelectDataMethodFragment.newInstance())
                OnSelectedResult(true)
            }
            is DataMethod -> {
                if (currentMode == MODE_GEN_PKG) {
                    switchToChooseScenarioFromDataMethod(item)
                    OnSelectedResult(true)
                } else {
                    super.onSelected(item)
                }
            }
            is PackageGenerator -> {
                packageGenerator = item
                this.switchToFragment(GeneratePackageOptionsFragment.newInstance(item))
                OnSelectedResult(true)
            }
            is KnowledgeCatalogItem -> {
                val fmt = if (item.stream.extension == ".json") FileFormat.Json else FileFormat.PortableBinary
                when {
                    item.stream.isAvailable(DataSource.LocalFile) -> {
                        project!!.get().loadWeights(item.stream.sourcePath(DataSource.LocalFile), fmt)
                        onNeuralNetworkChange(project!!, ChangeWeightsAction())
                    }
                    item.stream.isAvailable(DataSource.TempFile) -> {
                        project!!.get().loadWeights(item.stream.sourcePath(DataSource.TempFile), fmt)
                        onNeuralNetworkChange(project!!, ChangeWeightsAction())
                    }
                    else -> switchToFragment(OnlineKnowledgeFragment.newInstance(item as OnlineKnowledgeCatalogItem))

                    //project!!.get().loadWeights(item.stream.sourcePath(DataSource.RemoteFile, this), fmt)
                }

                runOnUiThread {
                    askJudge(R.string.title_judge_new_knowledge, R.string.msg_judge_new_knowledge)
                }

                OnSelectedResult(true)
            }
            is ProjectRenameEvent -> {
                onProjectRename(item.projectName)
                OnSelectedResult(true)
            }
            is Int -> when (item) {
                R.string.save -> {
                    OnSelectedResult(true) { switchToMode(MODE_SAVE) }
                }
                R.string.rename -> {
                    //onProjectRename(item.name)
                    OnSelectedResult(true) { switchToMode(MODE_RENAME) }
                }
                R.string.clear_weights -> {
                    project?.get()?.initWeight()
                    OnSelectedResult(true)
                }
                R.string.visualization_default_horizontal -> onSelectVisualizationMode("default-horizontal")
                R.string.visualization_default_vertical -> onSelectVisualizationMode("default-vertical")
                R.string.visualization_traditional -> onSelectVisualizationMode("traditional")
                R.string.visualization_educational -> onSelectVisualizationMode("educational")
                else -> super.onSelected(item)
            }
            else -> super.onSelected(item)
        }
    }

    private fun onSelectVisualizationMode(id: String): OnSelectedResult {
        projectVisualizationMode = id
        lastFragment = null
        supportFragmentManager.popBackStackImmediate()
        switchToMode(INITIAL_MODE_VIEW)
        return OnSelectedResult(true)
    }

    private fun switchToChooseScenarioFromDataMethod(method: DataMethod) {
        val scenarios = DataManager.get(this).getScenariosForDataMethod(method)
        if (scenarios.isNotEmpty()) {
            this.switchToFragment(ChooseScenarioFragment.newInstance(method))
        } else {
            throw UserException("No scenarios for data method")
        }
    }

    override fun selectDataValueFromSet(data: DataSliceReader) {
        when (currentMode) {
            MODE_GEN_PKG -> {
                val buf = DataValues(0, DataRole.Input)
                for (i in 0 until packageGeneratorOptions!!.size) {
                    val vect = data.read(buf, 0, 1)
                    if (vect == 1) {
                        this.packageGenerator!!.append(buf.get(0),
                                packageGeneratorOutput!!,
                                packageGeneratorOptions!!)
                    } else {
                        break
                    }
                }
                if (packageGeneratorOutput!!.elementCount >= packageGeneratorOptions!!.size) {
                    showGeneratedPackageActions(packageGeneratorOutput!!)
                }
            }
            else -> super.selectDataValueFromSet(data)
        }
    }

    private fun showSaveFileActions(item: SaveConfig) {
        runOnUiThread {
            AlertDialog.Builder(this)
                    .setTitle(R.string.title_dialog_save_result)
                    .setMessage("Would you like to locate the saved file?")
                    .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int ->
                        val myIntent = Intent(Intent.ACTION_VIEW)
                        myIntent.data = Uri.fromFile(item.outputFile)
                        val j = Intent.createChooser(myIntent, "Choose an application to openData with:")
                        startActivity(j)
                    }
                    .setNegativeButton(R.string.no) { d: DialogInterface, _: Int -> d.dismiss() }
                    .create().show()
        }
    }

    private fun showGeneratedPackageActions(outputSelection: OutputSelection) {
        runOnUiThread {
            AlertDialog.Builder(this)
                    .setTitle(R.string.title_dialog_save_result)
                    .setMessage("Would you like to locate the saved file?")
                    .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.fromFile((outputSelection.values.first().stream as SmartFileOutputStream).file.parentFile)
                        val j = Intent.createChooser(intent, "Choose an application to openData with:")
                        startActivity(j)
                    }
                    .setNegativeButton(R.string.no) { d: DialogInterface, _: Int -> d.dismiss() }
                    .create().show()
        }
    }

    private fun saveAndShowProgress(item: SaveConfig) {
        val dialog = SaveProjectProgressDialog(this.appLifecycleContext, this)
        val task = ProjectSaveAsyncTask(this.project!!)
        task.addListener(dialog)
        dialog.show(this.layoutInflater)
        task.execute(item)
    }

    override var project: NeuralNetProject? = null
        private set

    private fun onProjectRename(newName: String) {
        runOnUiThread {
            supportActionBar!!.title = String.format(titleFormat, newName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        project = if (intent.hasExtra(NETWORK_HANDLE)) {
            NeuralNetProject(SequentialNetworkModelWithWeights.attach(intent.getLongExtra(NETWORK_HANDLE, -0x21524111)))
        } else {
            NeuralNetProject(SequentialNetworkModelWithWeights(getString(R.string.from_scratch)))
        }

        DataManager.get(this).putProject(project!!)
        project!!.addObserver(this)

        switchToMode(INITIAL_MODE_VIEW)
        this.lastFragment = null
        if (intent.hasExtra(INITIAL_MODE)) {
            switchToMode(intent.getStringExtra(INITIAL_MODE)!!)
        }

        project!!.notifyObservers(ProjectInitializedEvent())
        titleFormat = getToolbar().title!!.toString()
        onProjectRename(project!!.name)
    }

    override fun getFragmentForMode(mode: String): androidx.fragment.app.Fragment {
        return when (mode) {
            MODE_PREDICT -> SelectDataMethodFragment.newInstance()
            INITIAL_MODE_VIEW -> SandboxFragment.newInstance(projectVisualizationMode)
            MODE_LEARN -> KnowledgeSourceFragment()
            MODE_SAVE -> SaveConfigFragment.newInstance()
            MODE_GEN_PKG -> ChoosePackageGeneratorFragment.newInstance()
            MODE_RENAME -> RenameProjectFragment.newInstance()
            MODE_SWITCH_VISUALIZATION -> ChooseVisualizationFragment.newInstance()
            else -> super.getFragmentForMode(mode)
        }
    }

    override fun finish() {
        if (project!!.hasUnsavedChanges()) {
            exitOnSave = false
            this.promptSaveOnExit { r -> when (r) {
                SavePromptResult.Discard -> { project!!.discardChanges(); finish() }
                SavePromptResult.Save -> { exitOnSave = true; switchToMode(MODE_SAVE) }
            } }
        } else {
            super.finish()
        }
    }

    override fun onNeuralNetworkChange(project: NeuralNetProject, event: ProjectAction) {
        super.onNeuralNetworkChange(project, event)
        if (event is ProjectRenameEvent) {
            onProjectRename(event.name)
        }
    }

    private fun promptSaveOnExit(onResult: (SavePromptResult) -> Unit ) {
        AlertDialog.Builder(this)
                .setTitle(R.string.title_leaving)
                .setMessage(R.string.msg_leaving)
                .setPositiveButton(R.string.save) { _: DialogInterface, _: Int -> onResult(SavePromptResult.Save) }
                .setNegativeButton(R.string.action_cancel) { _: DialogInterface, _: Int -> onResult(SavePromptResult.Cancel) }
                .setNeutralButton(R.string.discard) { _: DialogInterface, _: Int -> onResult(SavePromptResult.Discard) }
                .create().show()
    }

    override fun CreateBinder(): View {
        binding = ActivitySandboxBinding.inflate(layoutInflater, null, false)
        return binding.root
    }

    enum class SavePromptResult {
        Save, Discard, Cancel
    }

    companion object {
        const val NETWORK_HANDLE = "NETWORK_HANDLE"
        const val INITIAL_MODE = "INITIAL_MODE"
        const val MODE_ADD_LAYER = "ADD_LAYER"
        const val MODE_PREDICT = "PREDICT"
        const val INITIAL_MODE_VIEW = "VIEW"
        const val MODE_SAVE = "SAVE"
        const val MODE_LEARN = "LEARN"
        const val MODE_RENAME = "RENAME"
        const val MODE_GEN_PKG = "GEN_PKG"
        const val MODE_SWITCH_VISUALIZATION = "SWITCH_VIS"
    }
}
