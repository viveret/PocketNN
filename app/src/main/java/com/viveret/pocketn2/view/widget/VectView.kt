package com.viveret.pocketn2.view.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.media.ThumbnailUtils
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.viveret.pocketn2.ConfigurableIsCancelable
import com.viveret.tinydnn.basis.Vect
import com.viveret.tinydnn.data.graphics.BitmapDecorator
import com.viveret.tinydnn.data.graphics.BitmapPipeline
import com.viveret.tinydnn.data.graphics.ColorModes
import kotlin.math.*

class VectView : View {
    val points = ArrayList<Point>()
    val paint = Paint()

    private var mX = 0.0f
    private var mY = 0.0f
    private var mPreviousX = 0.0f
    private var mPreviousY = 0.0f
    private var dy = 0.0f
    private var dx = 0.0f
    private var wasMoving = false

    private var frag: ConfigurableIsCancelable? = null
    var translatable: Boolean = false
    var holdToDraw: Boolean = true
    var tapToDrawPoint = false
    lateinit var valueInfo: ValueInfo
    val bmpMatrix = Matrix()
    var colorMode = ColorModes.RED
    var invertColor = false
    var bitmapPipeline = BitmapPipeline(listOf(object : BitmapDecorator {
        override fun apply(src: Bitmap): Bitmap {
            val srcMinSize = min(src.width, src.height)
            return ThumbnailUtils.extractThumbnail(src, srcMinSize, srcMinSize)
        }
    }, object : BitmapDecorator {
        override fun apply(src: Bitmap): Bitmap =
                Bitmap.createScaledBitmap(src, valueInfo.width, valueInfo.height, false)
    }, object : BitmapDecorator {
        override fun apply(src: Bitmap): Bitmap =
                Bitmap.createBitmap(src, 0, 0, src.height, src.width, bmpMatrix, true)
    }))

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, xyz: Int) : super(context, attrs, defStyleAttr, xyz)

    fun updateDrawViewFromBitmap(bitmap: Bitmap) {
        val finalBmp = this.bitmapPipeline.apply(bitmap)

        // Copy pixels to destination
        for (y in 0 until finalBmp.height) {
            for (x in 0 until finalBmp.width) {
                if (y * finalBmp.width + x < this.valueInfo.pixels.size) {
                    val raw = finalBmp.getPixel(x, y)
                    val filteredColor = this.colorMode.filter(raw)
                    this.valueInfo.pixels[y * finalBmp.width + x] = filteredColor
                }
            }
        }

        // Normalize
        val minVal = this.valueInfo.pixels.minOrNull()
        val maxVal = this.valueInfo.pixels.maxOrNull()
        if (minVal != null && maxVal != null) {
            val gap = (maxVal - minVal).toDouble()
            if (abs(gap) > 0.0001) {
                for (i in 0 until this.valueInfo.pixels.size) {
                    this.valueInfo.pixels[i] = floor((this.valueInfo.pixels[i] - minVal) / gap * 255).toInt()
                }
            }
        }

        if (invertColor) {
            for (i in 0 until this.valueInfo.pixels.size) {
                this.valueInfo.pixels[i] = 255 - this.valueInfo.pixels[i]
            }
        }

        this.invalidate()
    }

    fun setInputSize(inputSize: Long) {
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        paint.textSize = 20.0f
        this.valueInfo = ValueInfo(Vect(FloatArray(inputSize.toInt()), inputSize.toInt()))

        val lp = this.layoutParams
        lp.height = lp.width
        this.layoutParams = lp
    }

    fun attachFragment(frag: ConfigurableIsCancelable) {
        this.frag = frag
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val lp = this.layoutParams
        lp.width = width
        lp.height = lp.width
        this.layoutParams = lp

        valueInfo.onDraw(canvas!!)

        if (this.tapToDrawPoint) {
            if (this.translatable) {
                canvas.translate(-xOffset, -yOffset)
            }
            canvas.translate(width / 2.0f, height / 2.0f)

            for (p in points) {
                //canvas.drawCircle(p.x.toFloat(), p.y.toFloat(), 50.0f, paint)
            }
        }
    }

    fun reset() {
        this.valueInfo.reset()
        this.invalidate()
    }

    val xOffset: Float
        get() = dx + mX

    val yOffset: Float
        get() = dy + mY

    override fun performClick(): Boolean {
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (this.tapToDrawPoint || this.translatable || this.holdToDraw) {
            when (event.actionMasked) {
                MotionEvent.ACTION_MOVE -> {
                    frag?.setIsCancelable(false)
                    dx = mPreviousX - event.x
                    dy = mPreviousY - event.y
                    if (holdToDraw) {
                        this.valueInfo.onTouchMove(dx, dy, event)
                    }
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_DOWN -> {
                    frag?.setIsCancelable(false)
                    wasMoving = false
                    mPreviousX = event.x
                    mPreviousY = event.y
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    frag?.setIsCancelable(true)
                    if (!wasMoving && (dx * dx + dy * dy) < 20 * 20) {
                        var x = event.x - dx - width / 2
                        var y = event.y + dy - height / 2
                        if (this.translatable) {
                            x += mX
                            y += mY
                        }
                        points.add(Point(x.toInt(), y.toInt()))
                    } else {
                        mX += dx
                        mY += dy
                    }
                    wasMoving = true

                    dx = 0.0f
                    dy = 0.0f
                    invalidate()
                    return performClick()
                }
            }
        }
        return false
    }

    class ValueInfo(file: Vect) {
        val p = Paint()
        val rect = RectF()

        init {
            p.style = Paint.Style.FILL
            p.textSize = 20f
        }

        val vals = file.vals
        val minVal = vals.minOrNull() ?: 0.0f
        val maxVal = vals.maxOrNull() ?: 0.0f
        val span = maxVal - minVal
        val pixels = if (span != 0.0f) vals.map { x -> ((x - minVal) / span * 255).roundToInt() }.toMutableList() else vals.map { x -> x.toInt() }.toMutableList()
        val width = sqrt(vals.size.toDouble()).roundToInt()
        val height = width

        fun onDraw(canvas: Canvas) {
            val sz = min(canvas.width, canvas.height) * 1.0f / width
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val pixel = pixels[y * width + x]
                    p.color = Color.argb(255, pixel, pixel, pixel)
                    rect.set(x * sz, y * sz, x * sz + sz, y * sz + sz)
                    canvas.drawRect(rect, p)
                }
            }
        }

        fun onTouchMove(dx: Float, dy: Float, event: MotionEvent) {
            val x = floor(event.x.toDouble() / this.width).toInt()
            val y = floor(event.y.toDouble() / this.height).toInt()
            val i = y * width + x
            if (i < pixels.size && i >= 0) {
                pixels[i] = 255
            }
        }

        fun reset() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this.pixels.replaceAll { 0 }
            } else {
                for (i in 0 until this.pixels.size) {
                    this.pixels[i] = 0
                }
            }
        }
    }
}