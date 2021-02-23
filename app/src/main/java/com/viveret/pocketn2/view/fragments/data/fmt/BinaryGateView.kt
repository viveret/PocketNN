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
import kotlin.math.roundToInt

class BinaryGateView : DataFormatView {
    override val nameResId: Int = R.string.title_fragment_data_view_binary_gate

    override fun supportsData(data: DataSlice): Boolean = data[DataRole.Input]?.first?.vals?.size in 1..8

    override fun getViewForData(data: DataSlice, parent: ViewGroup, project: NeuralNetProject?): View {
        val v = IdxImageViewReal(parent.context)
        v.setData(data)
        return v
    }

    class IdxImageViewReal : View {
        constructor(context: Context) : super(context)

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, xyz: Int) : super(context, attrs, defStyleAttr, xyz)

        private lateinit var valueInfo: ValueInfo

        fun setData(data: DataSlice) {
            this.valueInfo = ValueInfo(data)
        }

        override fun onDraw(canvas: Canvas?) {
            valueInfo.onDraw(canvas!!)
        }

        class ValueInfo(data: DataSlice) {
            val p = Paint()

            init {
                p.style = Paint.Style.FILL
                p.textSize = 20f
            }

            val vals = data[DataRole.Input]!!.first.vals
            val minVal = vals.minOrNull()!!
            val maxVal = vals.maxOrNull()!!
            val span = maxVal - minVal
            val pixels = vals.map { x -> ((x - minVal) / span * 255).roundToInt() }
            val width = vals.size

            fun onDraw(canvas: Canvas) {
                val sz = canvas.width * 1.0f / width
                for (x in 0 until width) {
                    val pixel = pixels[x]
                    p.color = Color.argb(255, pixel, pixel, pixel)
                    val r = RectF(x * sz, 0.0f, x * sz + sz, sz)
                    canvas.drawRect(r, p)
                }
            }
        }
    }
}