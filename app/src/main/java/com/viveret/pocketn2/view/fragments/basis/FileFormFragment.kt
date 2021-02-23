package com.viveret.pocketn2.view.fragments.basis

import androidx.viewbinding.ViewBinding
import com.viveret.pocketn2.view.widget.editor.FileNameView
import com.viveret.tinydnn.enums.FileFormat

abstract class FileFormFragment<T : ViewBinding>:  BottomSheetFormFragment<T>() {
    fun fileNameView(): FileNameView = FileNameView(binding!!.root, projectProvider!!){
        when (this.getSaveFormat()) {
            FileFormat.Binary -> "bin"
            FileFormat.Json -> "json"
            FileFormat.PortableBinary -> "pbin"
            else -> ""
        }
    }

    abstract fun getSaveFormat(): FileFormat?
}