package andrew.studio.com.ultrabuddymvvm.view

import andrew.studio.com.ultrabuddymvvm.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.math.roundToInt

class RobotGround(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)


    private var groundColor = getColorFromId(R.color.lightBlue)
    private var groundStrokeColor = getColorFromId(R.color.darkBlue)
    private var obstacleColor = getColorFromId(R.color.darkGray)
    private var testPointColor = getColorFromId(R.color.red)

    private var boderWidth = 4.0f

    private var groundWidth = 100
    private var groundHeight = 80

    private val unit: Float
        get() {
            val widthOnHeightRatio = width.toFloat() / height
            val mWidthOnHeightRatio = groundWidth.toFloat() / groundHeight

            return if (mWidthOnHeightRatio > widthOnHeightRatio) {
                width.toFloat() / groundWidth
            } else {
                height.toFloat() / groundHeight
            }
        }

    private fun getColorFromId(id: Int) = ResourcesCompat.getColor(resources, id, null)

    // Transform int coordination values to pixel value and fit the screen
    private fun Int.toUltraUnit(): Float = this * unit

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Translate the canvas to Descartes Coordination system and center vertical the ground
        canvas.translate(0f, (height.toFloat() - groundHeight.toUltraUnit()) / 2 + groundHeight)
        canvas.scale(1f, -1f)

        // Do the drawings
        drawGround(canvas)
        drawTestPoint(canvas)
    }

    private fun drawTestPoint(canvas: Canvas) {
        paint.color = testPointColor
        paint.style = Paint.Style.FILL

        canvas.drawCircle(30.toUltraUnit(), (groundHeight / 2).toUltraUnit(), 10f, paint)
    }

    private fun drawGround(canvas: Canvas) {
        paint.color = groundColor

        paint.style = Paint.Style.FILL

        val rect = getGroundRect(groundWidth, groundHeight).apply {
            left = 1
            top = 1
            right -= 1
            bottom -= 1
        }

        canvas.drawRect(rect, paint)

        paint.color = groundStrokeColor
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE

        canvas.drawRect(getGroundRect(groundWidth, groundHeight), paint)

    }

    private fun getGroundRect(groundWidth: Int, groundHeight: Int): Rect {
        val widthOnHeightRatio = width.toFloat() / height
        val mWidthOnHeightRatio = groundWidth.toFloat() / groundHeight

        val mRight: Int
        val mBottom: Int

        if (mWidthOnHeightRatio > widthOnHeightRatio) {
            mRight = width
            mBottom = (mRight / mWidthOnHeightRatio).roundToInt()
        } else {
            mBottom = height
            mRight = (mBottom * mWidthOnHeightRatio).roundToInt()
        }

        return Rect(0, 0, mRight, mBottom)
    }

}