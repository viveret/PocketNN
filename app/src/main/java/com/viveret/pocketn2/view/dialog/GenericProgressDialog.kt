package com.viveret.pocketn2.view.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.viveret.pocketn2.databinding.FragmentDialogProgressBinding
import com.viveret.tinydnn.util.AppLifecycleContext

abstract class GenericProgressDialog(context: AppLifecycleContext):
        AbstractProgressDialog<FragmentDialogProgressBinding>(context) {

    override fun createBinding(layoutInflater: LayoutInflater, container: ViewGroup): FragmentDialogProgressBinding {
        return FragmentDialogProgressBinding.inflate(layoutInflater, container, false)
    }
}