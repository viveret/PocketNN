package com.viveret.pocketn2.view.input

import android.view.View

class ConstResolver(val value: Any?): ViewInputResolver {
    override fun resolve(view: View): Any? = value
}