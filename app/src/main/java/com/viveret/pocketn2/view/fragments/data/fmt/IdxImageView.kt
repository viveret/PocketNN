package com.viveret.pocketn2.view.fragments.data.fmt

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.viveret.pocketn2.R
import com.viveret.tinydnn.basis.DataRole
import com.viveret.tinydnn.basis.DataSlice
import com.viveret.tinydnn.project.NeuralNetProject
import kotlin.math.*

class IdxImageView : DataFormatView {
    override val nameResId: Int = R.string.title_fragment_data_view_idx_image

    override fun supportsData(dataSlice: DataSlice): Boolean {
        val wholeSize = dataSlice[DataRole.Input]?.first?.vals?.size?.toDouble()
        return if (wholeSize != null) {
            val sqrt = sqrt(wholeSize)
            sqrt.toString() == floor(sqrt).toString()
        } else {
            false
        }
    }

    override fun getViewForData(dataSlice: DataSlice, parent: ViewGroup, project: NeuralNetProject?): View {
        val v = IdxImageViewReal(parent.context)
        v.setData(dataSlice)
        return v
    }

    class IdxImageViewReal : View {
        constructor(context: Context) : super(context)

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, xyz: Int) : super(context, attrs, defStyleAttr, xyz)

        private lateinit var valueInfo: ValueInfo

        fun setData(dataSlice: DataSlice) {
            this.valueInfo = ValueInfo(dataSlice)
        }

        override fun onDraw(canvas: Canvas?) {
            valueInfo.onDraw(canvas!!)
        }

        class ValueInfo(dataSlice: DataSlice) {
            val p = Paint()

            init {
                p.style = Paint.Style.FILL
                p.textSize = 20f
            }

            val vals = dataSlice[DataRole.Input]!!.first.vals
            val minVal = vals.minOrNull()!!
            val maxVal = vals.maxOrNull()!!
            val span = maxVal - minVal
            val pixels = vals.map { x -> ((x - minVal) / span * 255).roundToInt() }
            val width = sqrt(vals.size.toDouble()).roundToLong().toInt()
            val height = width

            fun onDraw(canvas: Canvas) {
                val sz = min(canvas.width, canvas.height) * 1.0f / width
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val pixel = pixels[y * width + x]
                        p.color = Color.argb(255, pixel, pixel, pixel)
                        val r = RectF(x * sz, y * sz, x * sz + sz, y * sz + sz)
                        canvas.drawRect(r, p)
                    }
                }
            }
        }
    }
}