package com.viveret.pocketn2.view.fragments.knowledge

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.viveret.tinydnn.basis.BetterInputStream
import com.viveret.tinydnn.basis.Stream
import com.viveret.tinydnn.data.io.SmartFileOutputStream
import com.viveret.tinydnn.data.knowledge.basis.OnlineKnowledgeCatalogItem
import com.viveret.tinydnn.util.async.OnSelectedResult
import com.viveret.tinydnn.util.async.PermissionsProvider
import org.jetbrains.anko.runOnUiThread
import java.io.File
import java.io.OutputStream
import java.util.*

class OnlineKnowledgeFragment: BottomSheetFormFragment<FragmentScenarioOnlineBinding>(), OnlineAcknowledgementView.OnlineAcknowledgementListener {
    override fun onDownloaded(files: Map<Stream, File>) {
        this.context?.runOnUiThread {
            binding!!.btnContinue.visibility = View.VISIBLE
        }
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
    private lateinit var item: OnlineKnowledgeCatalogItem
    private lateinit var progressionsView: ProgressionListView
    private lateinit var progressView: TrainDataProgressViewHolder
    private val onlineAcknowledgementView: OnlineAcknowledgementView = OnlineAcknowledgementView(this)
    private lateinit var downloadedFiles: Map<Stream, File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dm = DataManager.get(this.requireContext())
        this.item = dm.knowledgeCatalog.getValue(UUID.fromString(arguments?.getString("itemId")!!)!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)!!
        this.onlineAcknowledgementView.onCreateView(v)

        return v
    }

    private fun accepted(saveForNextTime: Boolean) {
        this.saveForNextTime = saveForNextTime

        if (context is PermissionsProvider) {
            (context as PermissionsProvider).requestInternetPermissions {
                try {
                    this.startDownload(it)
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

    private fun startDownload(context: Context) {
        val downloadQueue = StreamDownloadQueue(arrayOf(this.item.stream), saveForNextTime, context) { onDownloadedStream(it) }
        downloadQueue.addListener(this.progressView)
        this.progressView.onInit(downloadQueue.params.size)
        downloadQueue.start()
    }

    override fun submit(): OnSelectedResult {
        return try {
            projectProvider!!.onSelected(this.item)
        } catch (e: java.lang.Exception) {
            errorContainer.setError(e, binding!!.progressTotal)
            OnSelectedResult(false)
        }
    }

    companion object {
        fun newInstance(item: OnlineKnowledgeCatalogItem): OnlineKnowledgeFragment {
            val f = OnlineKnowledgeFragment()
            val args = Bundle()
            args.putString("itemId", item.stream.id.toString())
            f.arguments = args
            return f
        }
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentScenarioOnlineBinding? {
        val binding = FragmentScenarioOnlineBinding.inflate(inflater, container, false)

        binding.fragmentTitle.text = this.item.title
        binding.fragmentMessage.text = binding.fragmentMessage.text.toString().replace("%data_name%", this.item.title, false).replace("%data_url%", this.item.stream.host!!.siteName)

        this.progressionsView = ProgressionListView(binding.progressions)
        this.progressView = TrainDataProgressViewHolder(progressionsView, binding.progressTotal)

        return binding
    }
}