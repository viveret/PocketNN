package com.viveret.pocketn2.view.fragments.sandbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.viveret.pocketn2.databinding.FragmentSandboxBinding
import com.viveret.pocketn2.databinding.FragmentSandboxHorizontalBinding
import com.viveret.pocketn2.databinding.FragmentSandboxVerticalBinding
import com.viveret.pocketn2.view.activities.SandboxActivity
import com.viveret.pocketn2.view.fragments.project.EditorFragment
import com.viveret.tinydnn.project.actions.ProjectInitializedEvent


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProjectController] interface
 * to handle interaction events.
 * Use the [SandboxFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SandboxFragment : EditorFragment() {
    private var visualizationMode = "default"
    private lateinit var listView: androidx.recyclerview.widget.RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) {
            if (args.containsKey(ARG_VISUALIZATION_MODE)) {
                visualizationMode = args.getString(ARG_VISUALIZATION_MODE, visualizationMode)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = when (visualizationMode) {
            "educational" -> educationalSandboxBinding(inflater, container)
            "traditional" -> traditionalSandboxBinding(inflater, container)
            "default-vertical" -> defaultVerticalSandboxBinding(inflater, container)
            else -> defaultSandboxBinding(inflater, container)
        }
        onNeuralNetworkChange(projectViewController?.project!!, ProjectInitializedEvent())
        return binding.root
    }

    private fun defaultSandboxBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
        val binding = FragmentSandboxHorizontalBinding.inflate(inflater, container, false)

        val fabPredict = binding.fabPredict
        fabPredict.setOnClickListener {
            projectViewController?.switchToMode(SandboxActivity.MODE_SWITCH_VISUALIZATION)
        }

        this.listView = binding.list
        listView.adapter = this.editCarouselAdapter
        return binding
    }

    private fun defaultVerticalSandboxBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
        val binding = FragmentSandboxVerticalBinding.inflate(inflater, container, false)

        val fabPredict = binding.fabPredict
        fabPredict.setOnClickListener {
            projectViewController?.switchToMode(SandboxActivity.MODE_SWITCH_VISUALIZATION)
        }

        this.listView = binding.list
        listView.adapter = this.editCarouselAdapter
        return binding
    }

    private fun traditionalSandboxBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
        val binding = FragmentSandboxBinding.inflate(inflater, container, false)

        val fabPredict = binding.fabPredict
        fabPredict.setOnClickListener {
            projectViewController?.switchToMode(SandboxActivity.MODE_SWITCH_VISUALIZATION)
        }

        this.listView = binding.list
        listView.adapter = this.editCarouselAdapter
        return binding
    }

    private fun educationalSandboxBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
        val binding = FragmentSandboxBinding.inflate(inflater, container, false)

        val fabPredict = binding.fabPredict
        fabPredict.setOnClickListener {
            projectViewController?.switchToMode(SandboxActivity.MODE_SWITCH_VISUALIZATION)
        }

        this.listView = binding.list
        listView.adapter = this.editCarouselAdapter
        return binding
    }

    companion object {
        const val ARG_VISUALIZATION_MODE = "visualization-mode"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SandboxFragment.
         */
        fun newInstance(projectVisualizationMode: String): SandboxFragment {
            val frag = SandboxFragment()
            val args = Bundle()
            args.putString(ARG_VISUALIZATION_MODE, projectVisualizationMode)
            frag.arguments = args
            return frag
        }
    }
}// Required empty public constructor
