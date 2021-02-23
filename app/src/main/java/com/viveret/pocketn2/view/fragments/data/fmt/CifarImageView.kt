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
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

class CifarImageView : DataFormatView {
    override val nameResId: Int = R.string.title_fragment_data_view_cifar_image

    override fun supportsData(dataSlice: DataSlice): Boolean {
        val totalSize = dataSlice[DataRole.Input]?.first?.vals?.size
        return if (totalSize != null) {
            val sqrt = sqrt((totalSize / 3).toDouble())
            sqrt.toString() == floor(sqrt).toString()
        } else {
            false
        }
    }

    override fun getViewForData(data: DataSlice, parent: ViewGroup, project: NeuralNetProject?): View {
        val v = CifarImageViewReal(parent.context)
        v.setData(data)
        return v
    }

    class CifarImageViewReal : View {
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
            val width = sqrt((vals.size / 3).toDouble()).roundToInt()
            val height = width
            val r = RectF()

            fun onDraw(canvas: Canvas) {
                val sz = min(canvas.width, canvas.height) * 1.0f / width
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val pixelR = pixels[y * width + x + width * height * 0]
                        val pixelG = pixels[y * width + x + width * height * 1]
                        val pixelB = pixels[y * width + x + width * height * 2]
                        p.color = Color.argb(255, pixelR, pixelG, pixelB)
                        r.set(x * sz, y * sz, x * sz + sz, y * sz + sz)
                        canvas.drawRect(r, p)
                    }
                }
            }
        }
    }
}