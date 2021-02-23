package com.viveret.pocketn2.view.model

import com.viveret.tinydnn.util.async.Observable

interface MinimizableDialog: Observable {
    val title: String
    val icon: Int
    val visible: Boolean
    val minimizable: Boolean
    val dismissReason: DismissReason

    fun hideDialog()
    fun showDialog()
}