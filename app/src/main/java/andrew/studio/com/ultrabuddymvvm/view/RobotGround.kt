package andrew.studio.com.ultrabuddymvvm.view

import andrew.studio.com.ultrabuddymvvm.R
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.math.roundToInt

private const val INNER_DOT_RADIUS = 20f
private const val OUTER_DOT_RADIUS = 30f
private const val TRIANGLE_OFFSET = 35
private const val TRIANGLE_HEIGHT = 10
private const val TRIANGLE_WIDTH = 30

private const val CONE_HEIGHT = 100
private const val CONE_RADIUS = 20 + INNER_DOT_RADIUS

class RobotGround(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val groundColor = getColorFromId(R.color.lightBlue)
    private val groundStrokeColor = getColorFromId(R.color.darkBlue)
    private val obstacleColor = getColorFromId(R.color.darkGray)
    private val testPointColor = getColorFromId(R.color.red)

    private val white = getColorFromId(R.color.white)

    private val robotDotInnerColor = getColorFromId(R.color.red)
    private val robotDotOuterColor = getColorFromId(R.color.clearLightRed)
    private val robotDirectionColor = getColorFromId(R.color.lightRed)
    private val robotDirectionColorTransparent = getColorFromId(R.color.transparent)

    private val borderWidth = 4.0f

    private val groundWidth = 100
    private val groundHeight = 80

    object Directions {
        const val EAST = 0
        const val WEST = 1
        const val SOUTH = 2
        const val NORTH = 3

    }

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
        canvas.translate(0f, (height.toFloat() - groundHeight.toUltraUnit()) / 2 + groundHeight.toUltraUnit())
        canvas.scale(1f, -1f)

        // Do the drawings
        drawGround(canvas)
        drawRobot(canvas, x = 30, y = groundHeight / 2, dir = Directions.EAST)
    }

    private fun drawRobot(canvas: Canvas, x: Int, y: Int, dir: Int) {
        val xUnit = x.toUltraUnit()
        val yUnit = y.toUltraUnit()

        paint.color = robotDotOuterColor
        paint.style = Paint.Style.FILL

        canvas.drawCircle(xUnit, yUnit, OUTER_DOT_RADIUS, paint)

        paint.color = robotDotInnerColor
        paint.style = Paint.Style.FILL

        val directionTriangle: Path = when (dir) {
            Directions.NORTH -> {
                Path().apply {
                    moveTo(xUnit - INNER_DOT_RADIUS, yUnit)
                    lineTo(xUnit + INNER_DOT_RADIUS, yUnit)
                    lineTo(xUnit + CONE_RADIUS, yUnit + CONE_HEIGHT)
                    lineTo(xUnit - CONE_RADIUS, yUnit + CONE_RADIUS)
                    close()
                }
            }
            Directions.SOUTH -> {
                Path().apply {
                    moveTo(xUnit - INNER_DOT_RADIUS, yUnit)
                    lineTo(xUnit + INNER_DOT_RADIUS, yUnit)
                    lineTo(xUnit + CONE_RADIUS, yUnit - CONE_HEIGHT)
                    lineTo(xUnit - CONE_RADIUS, yUnit - CONE_RADIUS)
                    close()
                }
            }
            Directions.EAST -> {
                Path().apply {
                    moveTo(xUnit, yUnit + INNER_DOT_RADIUS)
                    lineTo(xUnit, yUnit - INNER_DOT_RADIUS)
                    lineTo(xUnit + CONE_HEIGHT, yUnit - CONE_RADIUS)
                    lineTo(xUnit + CONE_HEIGHT, yUnit + CONE_RADIUS)
                    close()
                }
            }
            else -> {
                Path().apply {
                    moveTo(xUnit, yUnit + INNER_DOT_RADIUS)
                    lineTo(xUnit, yUnit - INNER_DOT_RADIUS)
                    lineTo(xUnit - CONE_HEIGHT, yUnit - CONE_RADIUS)
                    lineTo(xUnit - CONE_HEIGHT, yUnit + CONE_RADIUS)
                    close()
                }
            }
        }

        paint.shader = when (dir) {
            Directions.NORTH -> LinearGradient(
                xUnit,
                yUnit,
                xUnit,
                yUnit + CONE_HEIGHT,
                robotDirectionColor,
                robotDirectionColorTransparent,
                Shader.TileMode.MIRROR
            )
            Directions.EAST -> LinearGradient(
                xUnit,
                yUnit,
                xUnit + CONE_HEIGHT,
                yUnit,
                robotDirectionColor,
                robotDirectionColorTransparent,
                Shader.TileMode.MIRROR
            )
            Directions.SOUTH -> LinearGradient(
                xUnit,
                yUnit,
                xUnit,
                yUnit - CONE_HEIGHT,
                robotDirectionColor,
                robotDirectionColorTransparent,
                Shader.TileMode.MIRROR
            )
            else -> LinearGradient(
                xUnit,
                yUnit,
                xUnit - CONE_HEIGHT,
                yUnit,
                robotDirectionColor,
                robotDirectionColorTransparent,
                Shader.TileMode.MIRROR
            )
        }

        canvas.drawPath(directionTriangle, paint)

        paint.reset()

        paint.color = robotDotInnerColor
        paint.style = Paint.Style.FILL

        canvas.drawCircle(xUnit, yUnit, INNER_DOT_RADIUS, paint)

        paint.color = white
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth

        canvas.drawCircle(xUnit, yUnit, INNER_DOT_RADIUS, paint)
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
        paint.strokeWidth = borderWidth
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