package com.viveret.pocketn2.view.input

import android.view.View

class GenericViewInputResolver<T>(val resolver: (T) -> Any?, val caster: (View) -> T): ViewInputResolver  where T: View {
    override fun resolve(view: View): Any? = resolver(caster(view))
}