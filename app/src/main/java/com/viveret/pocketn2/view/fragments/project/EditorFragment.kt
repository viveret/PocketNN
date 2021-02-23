package com.viveret.pocketn2.view.fragments.project

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.view.adapters.LayerListAdapter
import com.viveret.pocketn2.view.dialog.InspectPhotoDialog
import com.viveret.tinydnn.data.graphics.LayerVizualization
import com.viveret.tinydnn.project.INeuralNetworkObserver
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.project.ProjectViewController
import com.viveret.tinydnn.project.actions.*
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult
import org.jetbrains.anko.runOnUiThread

abstract class EditorFragment: androidx.fragment.app.Fragment(), INeuralNetworkObserver, OnItemSelectedListener {
    protected var projectViewController: ProjectViewController? = null
    private val allowInsert = false
    lateinit var inspectPhotoDialog: InspectPhotoDialog
    lateinit var editCarouselAdapter: LayerListAdapter

    enum class NodeMode {
        Single, Line
    }

    enum class ViewOptions {
        Weights, Labels, Size, NumIn, NumOut, Color
    }

    enum class EditMode {
        None, Node, Edge, Eraser
    }

    override fun onNeuralNetworkChange(project: NeuralNetProject, event: ProjectAction) {
        this.context?.runOnUiThread {
            when (event) {
                is ChangeWeightsAction -> editCarouselAdapter.refreshVisualizations()
                is AddLayerAction, is InsertLayerAction, is RemoveLayerAction -> {
                    try {
                        editCarouselAdapter.notifyDataSetChanged()
                    } catch (e: Exception) {
                        Log.e("com.viveret.pocketn2", "a", e)
                    }
                }
                is ProjectInitializedEvent -> {
                    editCarouselAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        if (context is ProjectViewController) {
            projectViewController = context
            projectViewController?.project?.addObserver(this)
            inspectPhotoDialog = InspectPhotoDialog(context)
            editCarouselAdapter = LayerListAdapter(context.project!!, this, this.allowInsert)
        } else {
            throw RuntimeException("$context must implement ProjectController")
        }

        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        projectViewController?.project?.removeObserver(this)
        projectViewController = null
    }

    override fun onSelected(item: Any): OnSelectedResult = when (item) {
        is LayerVizualization -> {
            val v = this@EditorFragment.requireView()
            inspectPhotoDialog.show(v.width, v.height, item)
            OnSelectedResult(true)
        }
        is Fragment -> projectViewController!!.onSelected(item)
        else -> OnSelectedResult(false)
    }
}