package com.viveret.pocketn2.view.fragments.sandbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.viveret.pocketn2.databinding.FragmentSandboxSaveConfigBinding
import com.viveret.pocketn2.view.adapters.ContentTypeAdapter
import com.viveret.pocketn2.view.adapters.FileFormatAdapter
import com.viveret.pocketn2.view.fragments.basis.BottomSheetFormFragment
import com.viveret.pocketn2.view.fragments.basis.FileFormFragment
import com.viveret.pocketn2.view.widget.editor.FileNameView
import com.viveret.tinydnn.data.SaveConfig
import com.viveret.tinydnn.enums.ContentType
import com.viveret.tinydnn.enums.FileFormat
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.util.async.OnSelectedResult
import com.viveret.tinydnn.util.async.PermissionsProvider
import java.io.File

class SaveConfigFragment: FileFormFragment<FragmentSandboxSaveConfigBinding>() {
    override fun submit(): OnSelectedResult {
        val finalDestination = try {
            reportSavePath()
        } catch (e: Exception) {
            errorContainer.setError(e, binding?.widgetSelectDirectory?.etDirectory?.parent as ViewGroup)
            return OnSelectedResult(false)
        }

        val saveFormat = try {
            (binding!!.spinSaveFormat.selectedItem as FileFormatAdapter.Adapter).item
        } catch (e: Exception) {
            errorContainer.setError(e, binding!!.spinSaveFormat)
            return OnSelectedResult(false)
        }

        val saveWhat = try {
            (binding!!.spinSaveWhat.selectedItem as ContentTypeAdapter.Adapter).item
        } catch (e: Exception) {
            errorContainer.setError(e, binding!!.spinSaveWhat)
            return OnSelectedResult(false)
        }

        return projectProvider!!.onSelected(SaveConfig(finalDestination, saveFormat, saveWhat))
    }

    private fun reportSavePath(): File {
        val path = this.fileNameView().getSavePath()
        val directory = File(path.parent!!)
        if (directory.exists() && directory.isDirectory) {
            return path
        } else {
            throw UserException("Directory not valid: $directory", relatedView = binding?.widgetSelectDirectory?.etDirectory!!)
        }
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentSandboxSaveConfigBinding {
        val binding = FragmentSandboxSaveConfigBinding.inflate(inflater, container, false)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = FileFormatAdapter(requireContext(), this.projectProvider!!.project!!)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.spinSaveFormat.adapter = adapter
        binding.spinSaveFormat.setSelection(FileFormat.Json.ordinal)

        val spinSaveWhat = binding.spinSaveWhat
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapterSaveWhat = ContentTypeAdapter(requireContext(), this.projectProvider!!.project!!)
        // Apply the adapter to the spinner
        spinSaveWhat.adapter = adapterSaveWhat
        spinSaveWhat.setSelection(ContentType.WeightsAndModel.ordinal)

        if (context is PermissionsProvider) {
            (context as PermissionsProvider).requestReadAndWritePermissions {
                this.btnContinueTrain?.isEnabled = true
            }
        }

        return binding
    }

    override fun getSaveFormat(): FileFormat =
            (binding?.spinSaveFormat?.selectedItem as FileFormatAdapter.Adapter).item

    override val initialExpansionState: Int? = null

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SandboxFragment.
         */
        fun newInstance(): SaveConfigFragment = SaveConfigFragment()
    }
}