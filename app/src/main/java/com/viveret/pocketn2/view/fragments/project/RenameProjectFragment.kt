package com.viveret.pocketn2.view.fragments.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.databinding.FragmentProjectRenameBinding
import com.viveret.tinydnn.error.NNException
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.network.SequentialNetworkModelWithWeights
import com.viveret.tinydnn.project.actions.ProjectRenameEvent
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RenameProjectFragment.OnLayerAddedListener] interface
 * to handle interaction events.
 * Use the [RenameProjectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RenameProjectFragment : ProjectBottomSheetFragment<FragmentProjectRenameBinding>() {
    override val initialExpansionState: Int? = null
    private var tableView: LinearLayout? = null

    private fun onSave() {
        try {
            if (projectProvider != null) {
                val nn = projectProvider?.project?.get()
                if (nn != null && nn is SequentialNetworkModelWithWeights) {
                    this.onSave(nn)
                } else {
                    throw NNException("Network must be sequential")
                }
            } else {
                throw UserException("Cannot add new layer without existing network")
            }
        } catch (e: Exception) {
            errorContainer.setError(e)
        }
    }

    private fun onSave(nn: SequentialNetworkModelWithWeights) {
        doAsync {
            try {
                try {
                    val newName = binding!!.etName.text.toString()
                    nn.name = newName
                    projectProvider?.onSelected(ProjectRenameEvent(newName))
                }  catch (e: UserException){
                    errorContainer.setError(e)
                    return@doAsync
                }

                uiThread {
                    dismiss()
                }
            } catch (e: NNException) {
                errorContainer.setError(e, tableView!!)
            }
        }
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentProjectRenameBinding? {
        val binding = FragmentProjectRenameBinding.inflate(inflater, container, false)

        binding.etName.setText(projectProvider!!.project!!.name)
        binding.btnSave.setOnClickListener { onSave() }
        binding.btnCancel.setOnClickListener { dismiss() }

        return binding
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment RenameProjectFragment.
         */
        fun newInstance(): RenameProjectFragment = RenameProjectFragment()
    }
}// Required empty public constructor
