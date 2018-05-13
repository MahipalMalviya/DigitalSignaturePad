package mahipal.signaturepaddemo

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.content.res.Resources
import android.graphics.Bitmap


class Signature(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var mPaint = Paint()
    private var mPath = Path()

    private var mExtraCanvas: Canvas? = null
    private var mExtraBitmap: Bitmap? = null
    private var mFrame: Rect? = null

    private var minWidth: Int = 0

    private val DEFAULT_ATTR_PEN_MIN_WIDTH_PX = 3
    private val DEFAULT_ATTR_PEN_COLOR = Color.BLACK
    private val DEFAULT_ATTR_PEN_STROKE = 10
    private val DEFAULT_ATTR_CANVAS_BACKGROUND = Color.WHITE

    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()

    private val TOUCH_TOLERANCE = 4f

    init {
        val a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.Signature)

        try {
            minWidth = a.getDimensionPixelSize(R.styleable.Signature_penWidth,convertDpToPx(DEFAULT_ATTR_PEN_MIN_WIDTH_PX.toFloat()))
            mPaint.color = a.getColor(R.styleable.Signature_penColor,DEFAULT_ATTR_PEN_COLOR)
            mPaint.strokeWidth = a.getInteger(R.styleable.Signature_penStrokeWidth, DEFAULT_ATTR_PEN_STROKE).toFloat()
            mExtraCanvas?.drawColor(a.getColor(R.styleable.Signature_backgroundColor,DEFAULT_ATTR_CANVAS_BACKGROUND))
        }finally {
            a.recycle()
        }

        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
    }

    fun setPenColor(colorRes: Int){
        try{
            mPaint.color = resources.getColor(colorRes)
        } catch (ex: Resources.NotFoundException){
            mPaint.color = Color.parseColor("#000000")
        }
    }

    fun setPenStrokeWidth(penStrokeWidth: Int){
        mPaint.strokeWidth = penStrokeWidth.toFloat()
    }

    fun setMinWidth(penMinWidth: Float){
        minWidth = convertDpToPx(penMinWidth)
    }

    fun setCanvasColor(colorRes: Int){
        mExtraCanvas?.drawColor(colorRes)
    }

    fun clear() {
        if (mExtraBitmap != null){
            mExtraBitmap = null
            ensureSignatureBitmap()
        }
        invalidate()
    }

    override fun onSizeChanged(width: Int, height: Int,
                               oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        mExtraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mExtraCanvas = Canvas(mExtraBitmap)

        mFrame = Rect()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (mExtraBitmap != null) {
            canvas?.drawBitmap(mExtraBitmap, 0f, 0f, null)
        }
        canvas?.drawRect(mFrame, mPaint)
    }

    private fun touchStart(x: Float?, y: Float?) {
        x?.let { y?.let { it1 -> mPath.moveTo(it, it1) } }
        mX = x!!
        mY = y!!
    }

    private fun touchMove(x: Float?, y: Float?) {
        val dx = x?.minus(mX)?.let { Math.abs(it) }
        val dy = y?.minus(mY)?.let { Math.abs(it) }
        if (dx != null && dy != null) {
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                // QuadTo() adds a quadratic bezier from the last point,
                // approaching control point (x1,y1), and ending at (x2,y2).
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
                mX = x
                mY = y
                // Draw the path in the extra bitmap to save it.
                mExtraCanvas?.drawPath(mPath, mPaint)
            }
        }
    }

    private fun touchUp() {
        mPath.reset()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> touchStart(x, y)
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun ensureSignatureBitmap() {
        if (mExtraBitmap == null) {
            mExtraBitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888)
            mExtraCanvas = Canvas(mExtraBitmap)
        }
    }

    fun getSignatureBitmap(): Bitmap? {
        val originalBitmap = getBitmap()
        if (originalBitmap != null) {
            val bgColorBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bgColorBitmap)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(originalBitmap,0F,0F,null)
            return bgColorBitmap
        }
        return originalBitmap
    }

    private fun getBitmap(): Bitmap?{
        ensureSignatureBitmap()
        return mExtraBitmap
    }

    private fun convertDpToPx(dp: Float): Int {
        return Math.round(context.resources.displayMetrics.density * dp)
    }

    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }
}