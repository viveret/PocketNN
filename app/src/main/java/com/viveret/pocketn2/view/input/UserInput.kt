package com.viveret.pocketn2.view.input

import android.view.LayoutInflater
import android.view.View

interface UserInput {
    fun inflateView(inflater: LayoutInflater): View
    fun coerceViewToValue(view: View): Any?
    val name: Int
    val label: Int
    val description: Int
}