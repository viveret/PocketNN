package com.viveret.pocketn2.view.fragments.scenario

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.async.view.ProgressionListView
import com.viveret.pocketn2.async.view.TrainDataProgressViewHolder
import com.viveret.pocketn2.asyncFile.StreamDownloadQueue
import com.viveret.pocketn2.databinding.FragmentScenarioOnlineBinding
import com.viveret.pocketn2.view.fragments.basis.ScenarioFragment
import com.viveret.pocketn2.view.widget.OnlineAcknowledgementView
import com.viveret.tinydnn.basis.*
import com.viveret.tinydnn.data.io.InputSelection
import com.viveret.tinydnn.data.io.SmartFileOutputStream
import com.viveret.tinydnn.scenario.basis.OnlineScenario
import com.viveret.tinydnn.util.async.OnSelectedResult
import com.viveret.tinydnn.util.async.PermissionsProvider
import org.jetbrains.anko.runOnUiThread
import java.io.File
import java.io.OutputStream

class OnlineScenarioFragment : ScenarioFragment<FragmentScenarioOnlineBinding>(), OnlineAcknowledgementView.OnlineAcknowledgementListener {
    private lateinit var progressionsView: ProgressionListView
    private lateinit var dm: DataManager
    override val dismissAfterSubmit: Boolean = true

    override fun onDownloaded(files: Map<Stream, File>) {
        downloadedFiles = files
        dm.validateStreamPackageCached(this.streamPackage)
        this.requireContext().runOnUiThread {
            binding!!.btnContinue.visibility = View.VISIBLE
        }
    }

    fun onDownloadedStream(files: Map<BetterInputStream, OutputStream>) {
        onDownloaded(files.map { it.key.source to (it.value as SmartFileOutputStream).file }.toMap())
    }

    override fun onlineDenied() = dismiss()

    override fun onlineAccepted() = this.accepted(false)

    override fun onlineAcceptedAndSave() = this.accepted(true)

    private var saveForNextTime: Boolean = false
    override val initialExpansionState: Int? = BottomSheetBehavior.STATE_EXPANDED
    private lateinit var streamPackage: StreamPackage
    private lateinit var progressView: TrainDataProgressViewHolder
    private val onlineAcknowledgementView: OnlineAcknowledgementView
    private lateinit var downloadedFiles: Map<Stream, File>

    init {
        onlineAcknowledgementView = OnlineAcknowledgementView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.streamPackage = HostedStreamPackage.fromId((this.scenario as OnlineScenario).dataSuite)
    }

    private fun accepted(saveForNextTime: Boolean) {
        this@OnlineScenarioFragment.saveForNextTime = saveForNextTime
        this.startDownload()
    }

    private fun startDownload() {
        if (context is PermissionsProvider) {
            (context as PermissionsProvider).requestInternetPermissions {
                try {
                    val downloadQueue = StreamDownloadQueue(streamPackage, this@OnlineScenarioFragment.saveForNextTime, it) { files -> onDownloadedStream(files) }
                    downloadQueue.addListener(progressView)
                    progressView.onInit(downloadQueue.params.size)
                    downloadQueue.start()
                } catch (e: Exception) {
                    Log.e("PocketN2", "Could not download training data", e)
                    activity?.runOnUiThread {
                        Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            throw Exception("context must implement PermissionsProvider")
        }
    }

    override fun submit(): OnSelectedResult = try {
        val inputs = InputSelection(this.downloadedFiles, if (saveForNextTime) DataSource.LocalFile else DataSource.TempFile)
        val stream = InputDataValueStream(requireContext(), projectProvider!!.project!!, inputs)
        this.submitData(stream)
    } catch (e: Exception) {
        errorContainer.setError(e, binding!!.progressTotal)
        OnSelectedResult(false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.dm = DataManager.get(this.requireContext())
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentScenarioOnlineBinding? {
        val binding = FragmentScenarioOnlineBinding.inflate(inflater, container, false)
        this.onlineAcknowledgementView.onCreateView(binding.root)

        binding.fragmentTitle.text = this.streamPackage.title

        binding.fragmentMessage.text = binding.fragmentMessage.text.toString().replace("%data_name%", this.streamPackage.dataName, false).replace("%data_url%", this.streamPackage.host.siteName)

        this.progressionsView = ProgressionListView(binding.progressions)
        this.progressView = TrainDataProgressViewHolder(progressionsView, binding.progressTotal)

        return binding
    }
}