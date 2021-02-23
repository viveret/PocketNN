package com.viveret.pocketn2.view.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.viveret.pocketn2.R
import com.viveret.pocketn2.view.holders.ErrorViewHolder
import org.jetbrains.anko.runOnUiThread

class ErrorContainerView(val context: Context) {
    private var holder: ErrorViewHolder? = null

    constructor(holder: ErrorViewHolder) : this(holder.view.context) {
        this.holder = holder
    }

    constructor(e: Exception, errorMessageContainer: LinearLayout) : this(ErrorViewHolder(errorMessageContainer)) {
        replaceWith(e)
    }

    constructor(errorMessageContainer: LinearLayout) : this(errorMessageContainer.context) {
        val v = errorMessageContainer.findViewById<View?>(R.id.tvErrorBody)
        if (v != null && v is TextView) {
            this.holder = ErrorViewHolder(v, errorMessageContainer)
        } else {
            holder = ErrorViewHolder(errorMessageContainer)
        }
    }

    fun setError(e: Exception, relatedView: View) {
        if (this.holder == null) {
            this.holder = ErrorViewHolder(relatedView.parent as ViewGroup)
        }
        holder?.relatedView = relatedView
        setError(e)
    }

    fun setError(e: Exception) {
        context.runOnUiThread {
            Log.e("com.viveret.pocketn2", e.message, e)
            try {
                if (holder?.view?.visibility != View.VISIBLE) {
                    holder?.view?.visibility = View.VISIBLE
                    holder?.view?.scaleY = 0f
                    replaceWith(e)
                    holder?.view?.animate()?.scaleY(1f)
                } else {
                    holder?.view?.animate()?.scaleY(0f)?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            replaceWith(e)
                            holder?.view?.animate()?.scaleY(1f)?.setListener(null)
                        }
                    })
                }
            } catch (e2: Exception) {
                Toast.makeText(context, "Error. ${e2.message} & ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun removeHolderViewFromParent() {
        val parent = holder?.view?.parent
        if (parent != null) {
            val errorBodyParentContainer = parent as ViewGroup
            errorBodyParentContainer.removeView(holder?.view)
        }
    }

    private fun replaceWith(e: Exception) {
        removeHolderViewFromParent()
        holder?.item = e
        holder?.refresh()
    }

    fun hide() {
        val parent = holder?.view?.parent
        if ((parent as ViewGroup).id != R.id.fragment_root) {
            var destination = parent
            while (destination != null) {
                val errorBodyParentContainer = destination as ViewGroup
                if (errorBodyParentContainer.id == R.id.fragment_root) {
                    parent.removeView(holder?.view)
                    holder?.view?.visibility = View.GONE
                    errorBodyParentContainer.addView(holder?.view)
                    destination = null
                } else {
                    destination = errorBodyParentContainer.parent
                }
            }
        }
    }
}