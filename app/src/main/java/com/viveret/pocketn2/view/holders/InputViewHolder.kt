package com.viveret.pocketn2.view.holders

import android.view.View
import com.viveret.pocketn2.view.input.ViewInputResolver
import java.lang.reflect.Constructor

class InputViewHolder(val constructor: Constructor<*>, val args: Array<Pair<View, ViewInputResolver>>) {
    fun get(): Any = get(args.map { x -> x.second.resolve(x.first) }.toTypedArray())

    fun get(args: Array<Any?>): Any = constructor.newInstance(*args)
}