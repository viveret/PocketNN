package com.viveret.pocketn2.view.fragments.basis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.viewbinding.ViewBinding
import com.viveret.pocketn2.R
import com.viveret.pocketn2.view.fragments.project.ProjectBottomSheetFragment
import com.viveret.tinydnn.util.async.OnSelectedResult

abstract class BottomSheetFormFragment<T: ViewBinding> : ProjectBottomSheetFragment<T>() {
    protected var btnContinueTrain: ImageButton? = null

    abstract fun submit(): OnSelectedResult

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)

        this.btnContinueTrain = v!!.findViewById(R.id.btnContinue)
        btnContinueTrain?.setOnClickListener {
            try {
                val result = submit()
                if (result.dismiss) dismiss()
                result.callback()
            } catch (e: Exception) {
                errorContainer.setError(e)
            }
        }

        return v
    }
}