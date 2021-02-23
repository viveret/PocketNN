package com.viveret.pocketn2.view.fragments.basis

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.viveret.pocketn2.R
import com.viveret.pocketn2.view.widget.ErrorContainerView

abstract class BottomSheetFragment<T> : BottomSheetDialogFragment() where T: ViewBinding {
    lateinit var errorContainer: ErrorContainerView
    abstract var fragmentRootView: LinearLayout
    abstract val initialExpansionState: Int?
    private lateinit var bottomSheetDialog: Dialog
    var binding: T? = null

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (arguments?.containsKey("INITIAL_STATE") == true) {
            dialog!!.setOnShowListener { dialog ->
                val d = dialog as BottomSheetDialog
                val bottomSheetInternal = d.findViewById<View>(R.id.design_bottom_sheet)
                val behavior = BottomSheetBehavior.from(bottomSheetInternal!!)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
            }
        }
        // Inflate the layout for this fragment
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme) // your app theme here
        binding = CreateBinding(inflater.cloneInContext(contextThemeWrapper), container, savedInstanceState)

        this.fragmentRootView = binding!!.root.findViewById(R.id.fragment_root)
                ?: binding!!.root as LinearLayout

        this.errorContainer = ErrorContainerView(this.fragmentRootView)
        return binding!!.root
    }

    abstract fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): T?

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.bottomSheetDialog = super.onCreateDialog(savedInstanceState)
        if (initialExpansionState != null) {
            bottomSheetDialog.setOnShowListener {
                this.setExpansionState(initialExpansionState ?: BottomSheetBehavior.STATE_EXPANDED)
            }
        }

        return bottomSheetDialog
    }

    private fun setExpansionState(state: Int) {
        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.skipCollapsed = true
        behavior.peekHeight = this.requireView().height
        behavior.state = state
    }
}