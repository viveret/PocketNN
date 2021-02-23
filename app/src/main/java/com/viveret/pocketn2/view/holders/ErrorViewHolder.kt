package com.viveret.pocketn2.view.holders

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.viveret.pocketn2.R
import com.viveret.tinydnn.error.UserException
import org.jetbrains.anko.runOnUiThread

class ErrorViewHolder(val view: TextView, val parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    var relatedView: View? = null
    lateinit var item: Exception

    fun refresh() {
        view.context.runOnUiThread {
            view.text = item.localizedMessage
            view.setTextColor(Color.RED)
            with(item) {
                if (this is UserException && this.relatedView != null) {
                    val relatedView = this.relatedView!!
                    moveUnder(relatedView)
                } else if (relatedView != null) {
                    moveUnder(relatedView!!)
                    relatedView = null
                }
            }
        }
    }

    private fun moveUnder(relatedView: View) {
        var insertIndex: Int
        var errorMessageContainer = relatedView.parent as LinearLayout
        insertIndex = errorMessageContainer.indexOfChild(relatedView) + 1
        while (errorMessageContainer.orientation != LinearLayout.VERTICAL) {
            val tmp = errorMessageContainer.parent as LinearLayout
            insertIndex = tmp.indexOfChild(errorMessageContainer) + 1
            errorMessageContainer = tmp
        }
        errorMessageContainer.addView(this.view, insertIndex)
    }

    constructor(parent: ViewGroup): this(inflateFrom(parent), parent)

    companion object {
        private fun inflateFrom(parent: ViewGroup): TextView {
            val ret = parent.findViewById(R.id.tvErrorBody) ?: TextView(parent.context)
            ret.setTextAppearance(R.style.AppTheme_ErrorBody)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            ret.layoutParams = lp
            ret.scaleY = 0f
            ret.animate().setStartDelay(0).scaleY(1f).setDuration(500).start()
            return ret
        }
    }
}