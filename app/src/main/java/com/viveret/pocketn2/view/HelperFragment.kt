package com.viveret.pocketn2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class HelperFragment<T>: Fragment() where T: Object {
    private lateinit var binding: T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)!!
        onInflatedView(v, binding)
        return v
    }

    abstract fun onInflatedView(v: View, binding: T)
}