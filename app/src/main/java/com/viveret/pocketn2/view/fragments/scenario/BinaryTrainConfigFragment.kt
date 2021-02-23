package com.viveret.pocketn2.view.fragments.scenario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.viveret.pocketn2.databinding.FragmentTrainMethodBinding
import com.viveret.pocketn2.view.fragments.project.ProjectBottomSheetFragment

class BinaryTrainConfigFragment : ProjectBottomSheetFragment<FragmentTrainMethodBinding>() {
    // TrainingScenarioDataListener
    override val initialExpansionState: Int? = null

    companion object {
        fun newInstance(gateName: String, outputs: List<Int>): BinaryTrainConfigFragment {
            return BinaryTrainConfigFragment()
        }
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentTrainMethodBinding? {
        return FragmentTrainMethodBinding.inflate(inflater, container, false)
    }
}