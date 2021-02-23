package com.viveret.pocketn2.view.widget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.viveret.tinydnn.project.actions.ProjectAction
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import android.view.animation.Animation
import android.view.animation.Transformation
import com.viveret.pocketn2.view.fragments.sandbox.ProjectActionsFragment

class ActionListView(scroll: ViewGroup, val actions: List<Int>, val listener: OnItemSelectedListener, val onDismiss: () -> Unit) : View.OnClickListener {
    val context: Context = scroll.context
    private val rootView: ViewGroup = scroll

    init {
        for (action in this.actions) {
            scroll.addView(createView(action))
        }
    }

    override fun onClick(v: View?) {
        if (v != null && v is Button) {
            val tag = v.tag
            if (tag is Int) {
                val selectCallback = listener.onSelected(actions.single { it == tag })
                if (selectCallback.dismiss) {
                    onDismiss()
                }
                selectCallback.callback()
            }
        }
    }

//    fun collapse() {
//        val a = DropDownAnim(rootView, rootView.measuredHeight, true)
//        a.duration = 1000 * 2
//        rootView.startAnimation(a)
//    }

    fun createView(item: Int): View {
        val btn = Button(this.context)
        btn.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        btn.setOnClickListener(this)
        bindView(btn, item)
        return btn
    }

    fun bindView(btn: View, item: Int) {
        if (btn is Button) {
            //btn.setText(btn.context.resources.getIdentifier(item.name, "string", "com.viveret.pocketn2"))
            btn.setText(item)
            btn.tag = item
        }
    }

    // https://stackoverflow.com/questions/4946295/android-expand-collapse-animation
    inner class DropDownAnim(private val view: View, private val targetHeight: Int, private val down: Boolean) : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val newHeight = targetHeight * if (down) interpolatedTime else (1 - interpolatedTime)
            view.layoutParams.height = newHeight.toInt()
            view.requestLayout()
        }

        override fun willChangeBounds(): Boolean = true
    }
}