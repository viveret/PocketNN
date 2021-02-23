package com.viveret.pocketn2.view.fragments.sandbox

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentChooseVisualizationBinding
import com.viveret.pocketn2.view.fragments.project.ProjectBottomSheetFragment
import com.viveret.pocketn2.view.widget.ActionListView
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ChooseVisualizationFragment.OnLayerAddedListener] interface
 * to handle interaction events.
 * Use the [ChooseVisualizationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseVisualizationFragment : ProjectBottomSheetFragment<FragmentChooseVisualizationBinding>(), OnItemSelectedListener {
    override val initialExpansionState: Int? = null
    private val actions = listOf(
            R.string.visualization_default_horizontal,
            R.string.visualization_default_vertical,
            R.string.visualization_traditional,
            R.string.visualization_educational
    )
    private var listener: OnItemSelectedListener? = null

    override fun onSelected(item: Any): OnSelectedResult {
        val selectCallback = listener!!.onSelected(item)
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

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentChooseVisualizationBinding? {
        val binding = FragmentChooseVisualizationBinding.inflate(inflater, container, false)
        val scroll = binding.scrollList
        val listView = ActionListView(scroll, actions, this) { dismiss() }
        return binding
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment RenameProjectFragment.
         */
        fun newInstance(): ChooseVisualizationFragment = ChooseVisualizationFragment()
    }
}// Required empty public constructor
