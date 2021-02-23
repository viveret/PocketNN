package com.viveret.pocketn2.view.input

import android.view.View

interface ViewInputResolver {
    fun resolve(view: View): Any?
}