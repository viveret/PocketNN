package com.viveret.pocketn2.view.fragments.sandbox

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentTrainMethodBinding
import com.viveret.pocketn2.view.adapters.DataMethodAdapter
import com.viveret.pocketn2.view.fragments.basis.BottomSheetFormFragment
import com.viveret.tinydnn.data.challenge.ChallengeMetaInfo
import com.viveret.tinydnn.data.challenge.ChallengeProvider
import com.viveret.tinydnn.data.train.BasicTrainingConfig
import com.viveret.tinydnn.error.NNException
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.optimizer.Optimizer
import com.viveret.tinydnn.util.OptimizerId2Type
import com.viveret.tinydnn.util.async.OnSelectedResult


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProjectController] interface
 * to handle interaction events.
 * Use the [TrainingMethodFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrainingMethodFragment : BottomSheetFormFragment<FragmentTrainMethodBinding>() {
    private var challenge: ChallengeMetaInfo? = null
    override val initialExpansionState: Int? = null

    private fun selectMenu(id: Int): Boolean {
        when (id) {
            R.id.action_train_method_short -> this.setBatchSizeAndEpochs(1, 10)
            R.id.action_train_method_regular -> this.setBatchSizeAndEpochs(1, 100)
            R.id.action_train_method_long -> this.setBatchSizeAndEpochs(1, 1000)
            else -> return false
        }
        return true
    }

    private fun setBatchSizeAndEpochs(batchSize: Int, epochs: Int) {
        binding!!.etBatchSize.setText(batchSize.toString())
        binding!!.etEpochs.setText(epochs.toString())
    }

    override fun submit(): OnSelectedResult {
        val optimizationMethod = try {
            OptimizerId2Type.OPTIMIZER_IDS[binding!!.spinOptimizer.selectedItemId.toInt()]
        } catch (e: Exception) {
            errorContainer.setError(e, binding!!.spinOptimizer)
            return OnSelectedResult(false)
        }

        val realdataMethod = try {
            (binding!!.spinDataMethod.adapter as DataMethodAdapter).getItem(binding!!.spinDataMethod.selectedItemPosition)!!
        } catch (e: Exception) {
            errorContainer.setError(e, binding!!.spinDataMethod)
            return OnSelectedResult(false)
        }

        val batchSize = try {
            binding!!.etBatchSize.text.toString().toLong()
        } catch (e: NumberFormatException) {
            errorContainer.setError(Exception("Invalid batch size", e), binding!!.etBatchSize)
            return OnSelectedResult(false)
        }

        val epochs = try {
            binding!!.etEpochs.text.toString().toInt()
        } catch (e: NumberFormatException) {
            errorContainer.setError(Exception("Invalid epoch count", e), binding!!.etEpochs)
            return OnSelectedResult(false)
        }

        val percentToInclude = try {
            binding!!.sbPercentInclude.progress * 1.0 / binding!!.sbPercentInclude.max
        } catch (e: NumberFormatException) {
            errorContainer.setError(Exception("Invalid percent to include", e), binding!!.sbPercentInclude)
            return OnSelectedResult(false)
        }

        return try {
            if (projectProvider!!.project!!.supportsDataMethod(realdataMethod.dataMethod)) {
                projectProvider?.trainUsingMethod(BasicTrainingConfig(batchSize, epochs,
                        createOptimizer(optimizationMethod), realdataMethod.dataMethod, binding!!.cbFit.isChecked, percentToInclude))
                OnSelectedResult(true)
            } else {
                errorContainer.setError(Exception("Data Method Not Supported By Project"), binding!!.spinDataMethod)
                OnSelectedResult(false)
            }
        } catch (e: NNException) {
            errorContainer.setError(e, binding!!.spinDataMethod)
            return OnSelectedResult(false)
        } catch (e: UserException) {
            errorContainer.setError(e, binding!!.spinDataMethod)
            return OnSelectedResult(false)
        }
    }

    private fun createOptimizer(optimizationMethod: Int): Optimizer {
        val type = OptimizerId2Type.OPTIMIZER_TYPES[optimizationMethod]
        return type?.newInstance()
                ?: throw IllegalArgumentException("Optimization method \"$optimizationMethod\" not supported")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ChallengeProvider) {
            this.challenge = context.challenge
        }
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentTrainMethodBinding? {
        val binding = FragmentTrainMethodBinding.inflate(inflater, container, false)

        val spinOptimizer = binding.spinOptimizer
        // Create an ArrayAdapter using the string array and a default spinner layout
        val optimizerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.optimizers, android.R.layout.simple_spinner_item)

        // Specify the layout to use when the list of choices appears
        optimizerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinOptimizer.adapter = optimizerAdapter

        val spinDataMethod = binding.spinDataMethod
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapterDataMethod = if (challenge != null) DataMethodAdapter(requireContext(), this.projectProvider!!.project!!, challenge!!.dataMethods.toList()) else DataMethodAdapter(requireContext(), this.projectProvider!!.project!!)
        // Apply the adapter to the spinner
        spinDataMethod.adapter = adapterDataMethod

        val btnSelectTrainingDefaults = binding.btnSelectTrainingDefaults
        btnSelectTrainingDefaults.setOnClickListener {
            val popup = PopupMenu(btnSelectTrainingDefaults.context, btnSelectTrainingDefaults)
            popup.menuInflater.inflate(R.menu.train_method_advanced, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener { item -> this.selectMenu(item.itemId) }
        }

        return binding
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SandboxFragment.
         */
        fun newInstance(): TrainingMethodFragment = TrainingMethodFragment()
    }
}// Required empty public constructor
