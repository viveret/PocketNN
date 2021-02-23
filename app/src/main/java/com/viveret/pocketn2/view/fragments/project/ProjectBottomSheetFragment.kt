package com.viveret.pocketn2.view.fragments.project

import android.content.Context
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import com.viveret.pocketn2.view.fragments.basis.BottomSheetFragment
import com.viveret.tinydnn.project.ProjectController


abstract class ProjectBottomSheetFragment<T : ViewBinding> : BottomSheetFragment<T>() {
    protected var projectProvider: ProjectController? = null
    override lateinit var fragmentRootView: LinearLayout

    override fun onDetach() {
        super.onDetach()
        projectProvider = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ProjectController) {
            projectProvider = context
        } else {
            throw RuntimeException("$context must implement ProjectController")
        }
    }
}// Required empty public constructor
