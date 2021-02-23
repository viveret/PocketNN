package com.viveret.pocketn2.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.R
import com.viveret.tinydnn.project.INeuralNetworkObserver
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.project.ProjectProvider
import com.viveret.tinydnn.project.actions.ProjectAction


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OnProjectConfigInteractionListener] interface
 * to handle interaction events.
 * Use the [ProjectConfigFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProjectConfigFragment : androidx.fragment.app.Fragment(), INeuralNetworkObserver {
    override fun onNeuralNetworkChange(project: NeuralNetProject, event: ProjectAction) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var projectProvider: ProjectProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =// Inflate the layout for this fragment
            inflater.inflate(R.layout.fragment_project_config, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ProjectProvider) {
            this.projectProvider = context
            context.project?.addObserver(this)
        } else {
            throw RuntimeException("$context must implement OnProjectConfigInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.projectProvider.project?.removeObserver(this)
    }

    companion object {
        fun newInstance(): ProjectConfigFragment = ProjectConfigFragment()
    }
}// Required empty public constructor
