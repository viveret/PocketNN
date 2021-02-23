package com.viveret.pocketn2.view.fragments.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.databinding.FragmentChallengeBinding
import com.viveret.pocketn2.view.activities.BasisActivity
import com.viveret.pocketn2.view.activities.ChallengeActivity
import com.viveret.pocketn2.view.activities.ProjectActivity
import com.viveret.pocketn2.view.fragments.project.EditorFragment
import com.viveret.tinydnn.data.challenge.ChallengeMetaInfo
import java.util.*

class ChallengeFragment: EditorFragment() {
    private fun challenge() = (requireActivity() as ChallengeActivity).challenge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectViewController?.switchToMode(ProjectActivity.MODE_ADD_LAYER) { it.putString("challenge_id", this.challenge().id.toString()) }
    }

    private fun submitSolution() =
            (projectViewController as BasisActivity).switchToFragment(JudgeFragment.newInstance(this.challenge(), this.activity as ProjectActivity))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentChallengeBinding.inflate(inflater, container, false)

        val fabPredict = binding.fabPredict
        fabPredict.setOnClickListener { submitSolution() }

        val listView = binding.list
        listView.adapter = this.editCarouselAdapter

        //onNeuralNetworkChange(projectViewController?.project!!, ProjectViewLoad)
        return binding.root
    }

    companion object {
        fun newInstance(): ChallengeFragment {
            val fragment = ChallengeFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}