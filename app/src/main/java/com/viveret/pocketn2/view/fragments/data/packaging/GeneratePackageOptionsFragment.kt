package com.viveret.pocketn2.view.fragments.data.packaging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.databinding.FragmentGeneratePackageOptionsBinding
import com.viveret.pocketn2.view.fragments.basis.FileFormFragment
import com.viveret.tinydnn.data.transform.PackageGenerator
import com.viveret.tinydnn.enums.FileFormat
import com.viveret.tinydnn.project.actions.GeneratePackageAction
import com.viveret.tinydnn.util.async.OnSelectedResult

class GeneratePackageOptionsFragment: FileFormFragment<FragmentGeneratePackageOptionsBinding>() {
    private lateinit var pkgGenerator: PackageGenerator
    override val initialExpansionState: Int? = null

    override fun submit(): OnSelectedResult {
        val size = binding!!.etPackageSize.text.toString().toInt()
        val includeFitTo = binding!!.cbFit.isChecked
        val options = GeneratePackageAction(size, includeFitTo, fileNameView().getSavePath().canonicalPath)
        return projectProvider!!.onSelected(options)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            this.init(requireArguments())
        }
    }

    private fun init(arguments: Bundle) {
        val item = arguments.getInt("item")
        this.pkgGenerator = DataManager.get(this.requireContext()).generators.first { it.name == item }
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentGeneratePackageOptionsBinding? {
        val binding = FragmentGeneratePackageOptionsBinding.inflate(inflater, container, false)
        binding.title.setText(this.pkgGenerator.name)

        binding.widgetFileName.etExtension.setText("gz")
        (binding.widgetFileName.etExtension.parent as ViewGroup).visibility = View.GONE

        return binding
    }

    override fun getSaveFormat(): FileFormat? = null

    companion object {
        fun newInstance(packageGenerator: PackageGenerator): GeneratePackageOptionsFragment {
            val frag = GeneratePackageOptionsFragment()
            val args = Bundle()

            args.putInt("item", packageGenerator.name)

            frag.arguments = args
            return frag
        }
    }
}