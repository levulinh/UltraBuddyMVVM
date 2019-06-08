package andrew.studio.com.ultrabuddymvvm.view

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.data.entity.Point
import andrew.studio.com.ultrabuddymvvm.data.entity.Polygon
import andrew.studio.com.ultrabuddymvvm.ui.map.UltraMapViewModel.RobotPosition
import andrew.studio.com.ultrabuddymvvm.view.RobotGround.Directions.EAST
import andrew.studio.com.ultrabuddymvvm.view.RobotGround.Directions.NORTH
import andrew.studio.com.ultrabuddymvvm.view.RobotGround.Directions.SOUTH
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.math.roundToInt

class RobotGround(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    companion object {
        private const val INNER_DOT_RADIUS = 20f
        private const val OUTER_DOT_RADIUS = 30f

        private const val CONE_HEIGHT = 100
        private const val CONE_RADIUS = 20 + INNER_DOT_RADIUS
        private const val HORZ_OFFSET = 16
        private const val VERT_OFFSET = 16

        private const val DEFAULT_GROUND_WIDTH = 800
        private const val DEFAULT_GROUND_HEIGHT = 1000
        private const val DEFAULT_POSITION_X = 0
        private const val DEFAULT_POSITION_Y = 0
        private const val DEFAULT_DIRECTION = NORTH
        private const val DEFAULT_BORDER_WIDTH = 4.0f
    }

    private val mWidth: Int
        get() = width - 2 * VERT_OFFSET
    private val mHeight: Int
        get() = height - 2 * HORZ_OFFSET

    private val unit: Float
        get() {
            val widthOnHeightRatio = mWidth.toFloat() / mHeight
            val mWidthOnHeightRatio = groundWidth.toFloat() / groundHeight

            return if (mWidthOnHeightRatio > widthOnHeightRatio) {
                mWidth.toFloat() / groundWidth
            } else {
                mHeight.toFloat() / groundHeight
            }
        }

    private fun getColorFromId(id: Int) = ResourcesCompat.getColor(resources, id, null)

    // Transform int coordination values to pixel value and fit the screen
    private fun Int.toUltraUnit(): Float = this * unit

    private val groundColor = getColorFromId(R.color.lightBlue)
    private val groundStrokeColor = getColorFromId(R.color.darkBlue)
    private val obstacleColor = getColorFromId(R.color.darkSkyBlue)
    private val white = getColorFromId(R.color.white)
    private val robotDotInnerColor = getColorFromId(R.color.red)
    private val robotDotOuterColor = getColorFromId(R.color.clearLightRed)
    private val robotDirectionColor = getColorFromId(R.color.lightRed)
    private val robotDirectionColorTransparent = getColorFromId(R.color.transparent)
    private val trackColor = getColorFromId(R.color.orange)

    var obstacles: List<Polygon> = listOf()
        set (value) {
            field = value
            invalidate()
        }

    var robotPosition = RobotPosition(3*48, 2*48, NORTH)
        set(value) {
            field = value
            positionTrack.add(value)
            direction = value.direction
            invalidate()
        }

    private var positionTrack = mutableListOf<RobotPosition>()

    var borderWidth = DEFAULT_BORDER_WIDTH
        set(value) {
            field = value
            invalidate()
        }

    var groundWidth = DEFAULT_GROUND_WIDTH
        set(value) {
            field = value
            invalidate()
        }
    var groundHeight = DEFAULT_GROUND_HEIGHT
        set(value) {
            field = value
            invalidate()
        }

    var direction = DEFAULT_DIRECTION
        set(value) {
            field = value
            invalidate()
        }

    init {
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.RobotGround, 0, 0)

        direction = typedArray.getInt(R.styleable.RobotGround_direction, NORTH.toInt()).toLong()
        groundWidth = typedArray.getInt(R.styleable.RobotGround_groundWidth, DEFAULT_GROUND_WIDTH)
        groundHeight = typedArray.getInt(R.styleable.RobotGround_groundHeight, DEFAULT_GROUND_HEIGHT)

        typedArray.recycle()
    }

    object Directions {
        const val EAST = 1L
        const val WEST = 3L
        const val SOUTH = 2L
        const val NORTH = 0L
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Translate the canvas to Descartes Coordination system and center vertical the ground
        canvas.translate(
            HORZ_OFFSET.toFloat(),
            (mHeight.toFloat() - groundHeight.toUltraUnit()) / 2 + groundHeight.toUltraUnit()
        )
        canvas.scale(1f, -1f)

        // Do the drawings
        drawGround(canvas)
        drawObstacles(canvas)
        for (i in 0 until positionTrack.size - 1) {
            drawTrack(canvas, trackColor, positionTrack[i], positionTrack[i+1])
        }
        drawRobot(canvas)


    }

    private fun drawObstacles(canvas: Canvas) {
        for (poly: Polygon in obstacles) {
            paint.reset()
            drawPolygon(canvas, poly.points, obstacleColor)
        }
    }

    private fun drawPolygon(
        canvas: Canvas,
        points: List<Point>,
        color: Int,
        strokeColor: Int = Color.DKGRAY,
        isFilled: Boolean = true,
        isStroked: Boolean = false,
        isClosed: Boolean = true
    ) {
        paint.color = color
        if (points.size <= 2) return
        val path = Path().apply {
            moveTo(points[0].x.toUltraUnit(), points[0].y.toUltraUnit())
            for (i in 1 until points.size) {
                lineTo(points[i].x.toUltraUnit(), points[i].y.toUltraUnit())
            }
            if (isClosed) {
                close()
            }
        }
        if (isFilled) {
            paint.style = Paint.Style.FILL
            canvas.drawPath(path, paint)
        }
        if (isStroked) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderWidth
            paint.color = strokeColor
            canvas.drawPath(path, paint)
        }

    }


    private fun drawRobot(canvas: Canvas) {
        val xUnit = robotPosition.x.toUltraUnit()
        val yUnit = robotPosition.y.toUltraUnit()

        paint.color = robotDotOuterColor
        paint.style = Paint.Style.FILL

        canvas.drawCircle(xUnit, yUnit, OUTER_DOT_RADIUS, paint)

        paint.color = robotDotInnerColor
        paint.style = Paint.Style.FILL

        val directionIndicator: Path = when (direction) {
            NORTH -> {
                Path().apply {
                    moveTo(xUnit - INNER_DOT_RADIUS, yUnit)
                    lineTo(xUnit + INNER_DOT_RADIUS, yUnit)
                    lineTo(xUnit + CONE_RADIUS, yUnit + CONE_HEIGHT)
                    lineTo(xUnit - CONE_RADIUS, yUnit + CONE_HEIGHT)
                    close()
                }
            }
            SOUTH -> {
                Path().apply {
                    moveTo(xUnit - INNER_DOT_RADIUS, yUnit)
                    lineTo(xUnit + INNER_DOT_RADIUS, yUnit)
                    lineTo(xUnit + CONE_RADIUS, yUnit - CONE_HEIGHT)
                    lineTo(xUnit - CONE_RADIUS, yUnit - CONE_HEIGHT)
                    close()
                }
            }
            EAST -> {
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

        paint.shader = when (direction) {
            NORTH -> LinearGradient(
                xUnit,
                yUnit,
                xUnit,
                yUnit + CONE_HEIGHT,
                robotDirectionColor,
                robotDirectionColorTransparent,
                Shader.TileMode.MIRROR
            )
            EAST -> LinearGradient(
                xUnit,
                yUnit,
                xUnit + CONE_HEIGHT,
                yUnit,
                robotDirectionColor,
                robotDirectionColorTransparent,
                Shader.TileMode.MIRROR
            )
            SOUTH -> LinearGradient(
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

        canvas.drawPath(directionIndicator, paint)

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

    private fun drawTrack(canvas: Canvas, color: Int, startPoint: RobotPosition, endpoint: RobotPosition) {
        paint.color = color
        paint.style = Paint.Style.FILL
        paint.strokeWidth = borderWidth / 2

        canvas.drawCircle(startPoint.x.toUltraUnit(), startPoint.y.toUltraUnit(), 5f, paint)
        canvas.drawLine(startPoint.x.toUltraUnit(), startPoint.y.toUltraUnit(), endpoint.x.toUltraUnit(), endpoint.y.toUltraUnit(), paint)
        canvas.drawCircle(endpoint.x.toUltraUnit(), endpoint.y.toUltraUnit(), 5f, paint)
    }

//    private fun drawFilledPolygon(canvas: Canvas, poly: List<>)

    private fun getGroundRect(groundWidth: Int, groundHeight: Int): Rect {
        val widthOnHeightRatio = mWidth.toFloat() / mHeight
        val mWidthOnHeightRatio = groundWidth.toFloat() / groundHeight

        val mRight: Int
        val mBottom: Int

        if (mWidthOnHeightRatio > widthOnHeightRatio) {
            mRight = mWidth
            mBottom = (mRight / mWidthOnHeightRatio).roundToInt()
        } else {
            mBottom = mHeight
            mRight = (mBottom * mWidthOnHeightRatio).roundToInt()
        }

        return Rect(0, 0, mRight, mBottom)
    }
}