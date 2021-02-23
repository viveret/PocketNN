package com.viveret.pocketn2.view.fragments.predict

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.databinding.FragmentSelectDataMethodBinding
import com.viveret.pocketn2.view.adapters.DataMethodAdapter
import com.viveret.pocketn2.view.fragments.basis.BottomSheetFormFragment
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.util.async.OnSelectedResult


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProjectController] interface
 * to handle interaction events.
 * Use the [SelectDataMethodFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectDataMethodFragment : BottomSheetFormFragment<FragmentSelectDataMethodBinding>() {
    override val initialExpansionState: Int? = null

    override fun submit(): OnSelectedResult {
        val realDataMethod = try {
            (binding?.spinDataMethod?.adapter as DataMethodAdapter).getItem(binding?.spinDataMethod?.selectedItemPosition!!)!!
        } catch (e: Exception) {
            throw UserException("Invalid data method", e, binding?.spinDataMethod)
        }

        if (projectProvider!!.project!!.supportsDataMethod(realDataMethod.dataMethod)) {
            return projectProvider!!.onSelected(realDataMethod.dataMethod)
        } else {
            throw UserException("Data method not supported by project", null, binding?.spinDataMethod)
        }
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentSelectDataMethodBinding? {
        val binding = FragmentSelectDataMethodBinding.inflate(inflater, container, false)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapterDataMethod = DataMethodAdapter(requireContext(), this.projectProvider!!.project!!)
        // Apply the adapter to the spinner
        binding.spinDataMethod.adapter = adapterDataMethod

        return binding
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SandboxFragment.
         */
        fun newInstance(): SelectDataMethodFragment = SelectDataMethodFragment()
    }
}// Required empty public constructor
