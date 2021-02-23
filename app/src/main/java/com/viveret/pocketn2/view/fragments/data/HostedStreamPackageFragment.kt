package com.viveret.pocketn2.view.fragments.data

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.R
import com.viveret.pocketn2.async.view.ProgressionListView
import com.viveret.pocketn2.async.view.TrainDataProgressViewHolder
import com.viveret.pocketn2.asyncFile.StreamDownloadQueue
import com.viveret.pocketn2.databinding.FragmentScenarioOnlineBinding
import com.viveret.pocketn2.view.fragments.basis.BottomSheetFormFragment
import com.viveret.pocketn2.view.widget.OnlineAcknowledgementView
import com.viveret.tinydnn.basis.*
import com.viveret.tinydnn.data.io.InputSelection
import com.viveret.tinydnn.data.io.SmartFileOutputStream
import com.viveret.tinydnn.util.async.OnSelectedResult
import com.viveret.tinydnn.util.async.PermissionsProvider
import java.io.File
import java.io.OutputStream
import java.util.*

class HostedStreamPackageFragment : BottomSheetFormFragment<FragmentScenarioOnlineBinding>(), OnlineAcknowledgementView.OnlineAcknowledgementListener {
    override fun onDownloaded(files: Map<Stream, File>) {
        DataManager.get(this.requireContext()).validateStreamPackageCached(this.streamPackage)
        binding!!.btnContinue.visibility = View.VISIBLE

        downloadedFiles = files
    }

    fun onDownloadedStream(files: Map<BetterInputStream, OutputStream>) {
        onDownloaded(files.map { it.key.source to (it.value as SmartFileOutputStream).file }.toMap())
    }

    override fun onlineDenied() = dismiss()

    override fun onlineAccepted() = this.accepted(false)

    override fun onlineAcceptedAndSave() = this.accepted(true)

    private var saveForNextTime: Boolean = false
    override val initialExpansionState: Int? = BottomSheetBehavior.STATE_EXPANDED
    lateinit var progressionsView: ProgressionListView
    private lateinit var streamPackage: StreamPackage
    private lateinit var progressView: TrainDataProgressViewHolder
    private val onlineAcknowledgementView: OnlineAcknowledgementView = OnlineAcknowledgementView(this)
    private lateinit var downloadedFiles: Map<Stream, File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dm = DataManager.get(this.requireContext())
        if (arguments != null) {
            val packageId = requireArguments().getString("package-id")
            this.streamPackage = HostedStreamPackage.fromId(UUID.fromString(packageId))
        } else {
            throw Exception("Missing arguments")
        }
    }

    private fun accepted(saveForNextTime: Boolean) {
        this.saveForNextTime = saveForNextTime

        if (context is PermissionsProvider) {
            (context as PermissionsProvider).requestInternetPermissions {
                try {
                    val downloadQueue = StreamDownloadQueue(this.streamPackage, saveForNextTime, this.requireContext()) { onDownloadedStream(it) }
                    downloadQueue.addListener(this.progressView)
                    this.progressView.onInit(downloadQueue.params.size)
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

    override fun submit(): OnSelectedResult {
        return try {
            val trainingData = InputDataValueStream(requireContext(), projectProvider!!.project!!, InputSelection(downloadedFiles))
            this.projectProvider!!.onSelected(trainingData)
        } catch (e: java.lang.Exception) {
            errorContainer.setError(e, binding!!.progressTotal)
            OnSelectedResult(false)
        }
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentScenarioOnlineBinding? {
        val binding = FragmentScenarioOnlineBinding.inflate(inflater, container, false)
        this.onlineAcknowledgementView.onCreateView(binding.root)

        binding.fragmentTitle.text = this.streamPackage.title
        binding.fragmentMessage.text = binding.fragmentMessage.text.toString().replace("%data_name%", this.streamPackage.dataName).replace("%data_url%", this.streamPackage.host.siteName)

        this.progressionsView = ProgressionListView(binding.progressions)
        this.progressView = TrainDataProgressViewHolder(progressionsView, binding.progressTotal)

        return binding
    }
}