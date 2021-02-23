package com.viveret.pocketn2.view.widget.editor

import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.viveret.pocketn2.R
import com.viveret.tinydnn.project.ProjectProvider
import java.io.File

class FileNameView(v: View, val projectProvider: ProjectProvider, val extensionProvider: () -> String) {
    private val etDirectory = v.findViewById<EditText>(R.id.etDirectory)
    private val etName = v.findViewById<EditText>(R.id.etName)
    private val etExtension = v.findViewById<EditText>(R.id.etExtension)
    private val tvSavePath = v.findViewById<TextView>(R.id.save_path)

    fun getSavePath(): File = File(getDirectory(), getFileName())

    private fun getDirectory(): File {
        val directory = this.etDirectory.text.toString().trim()
        return if (directory.isEmpty()) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) // DIRECTORY_DOWNLOADS
        } else {
            File(directory)
        }
    }

    private fun getFileName(): String {
        var nameWithoutExtension = this.etName.text.toString()
        if (nameWithoutExtension.isBlank()) {
            nameWithoutExtension = projectProvider.project!!.name.replace(Regex("[/\\\\|:?*]"), "")
        }
        var extension = this.etExtension.text.toString()
        if (extension.isBlank()) {
            extension = extensionProvider()
        }
        return "$nameWithoutExtension.$extension"
    }

    fun refreshSavePath(): Boolean {
        tvSavePath.text = getSavePath().canonicalPath
        return true
    }

    init {
        for (editText in arrayOf(this.etDirectory, this.etName, this.etExtension)) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    refreshSavePath()
                }
            })
        }
        refreshSavePath()
    }
}