package com.viveret.pocketn2.view.fragments.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.databinding.FragmentSandboxMessagesBinding


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProjectController] interface
 * to handle interaction events.
 * Use the [MessagesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessagesFragment : ProjectBottomSheetFragment<FragmentSandboxMessagesBinding>() {
    override val initialExpansionState: Int? = null

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentSandboxMessagesBinding? {
        val binding = FragmentSandboxMessagesBinding.inflate(inflater, container, false)
        val scroll = binding.scrollList
        for (msg in projectProvider?.project?.savedMessages!!) {
            val tv = TextView(this.context)
            tv.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            tv.text = msg.toString()
            scroll.addView(tv)
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
        fun newInstance(): MessagesFragment = MessagesFragment()
    }
}// Required empty public constructor
