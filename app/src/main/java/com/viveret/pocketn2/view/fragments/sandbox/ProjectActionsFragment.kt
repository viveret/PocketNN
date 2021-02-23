package com.viveret.pocketn2.view.fragments.sandbox

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentProjectActionsBinding
import com.viveret.pocketn2.view.fragments.project.ProjectBottomSheetFragment
import com.viveret.pocketn2.view.widget.ActionListView
import com.viveret.tinydnn.project.actions.ProjectAction
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProjectController] interface
 * to handle interaction events.
 * Use the [ProjectActionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProjectActionsFragment : ProjectBottomSheetFragment<FragmentProjectActionsBinding>(), OnItemSelectedListener {
    override val initialExpansionState: Int? = null
    private val actions = listOf(
            R.string.save,
            R.string.rename,
            R.string.clear_weights
    )
    private var listener: OnItemSelectedListener? = null

    override fun onSelected(item: Any): OnSelectedResult {
        val selectCallback = listener!!.onSelected(item as ProjectAction)
        if (selectCallback.dismiss) {
            dismiss()
        }
        return selectCallback
    }

    override fun onDetach() {
        this.listener = null
        super.onDetach()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnItemSelectedListener) {
            this.listener = context
        } else {
            throw Exception("context does not implement OnProjectActionSelectedListener")
        }
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentProjectActionsBinding? {
        val binding = FragmentProjectActionsBinding.inflate(inflater, container, false)
        val scroll = binding.scrollList
        val listView = ActionListView(scroll, actions, listener!!) { dismiss() }
        return binding
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SandboxFragment.
         */
        fun newInstance(): ProjectActionsFragment = ProjectActionsFragment()
    }
}// Required empty public constructor
