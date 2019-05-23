package andrew.studio.com.ultrabuddymvvm.corealgorithms.models

import kotlin.math.pow
import kotlin.math.sqrt

class Point2D(val x: Float, val y: Float) {

    fun clone(p: Point2D): Point2D {
        return Point2D(p.x, p.y)
    }

    fun distance(p: Point2D): Float {
        return sqrt((this.x - p.x).pow(2) + (this.y - p.y).pow(2))
    }

    override fun toString(): String {
        return "(${this.x}, ${this.y})"
    }

    operator fun unaryMinus(): Point2D {
        return Point2D(-this.x, -this.y)
    }

    operator fun plus(p: Point2D): Point2D {
        return Point2D(this.x + p.x, this.y + p.y)
    }

    operator fun minus(p: Point2D): Point2D {
        return this + (-p)
    }

    operator fun times(a: Float): Point2D {
        return Point2D(this.x * a, this.y * a)
    }

    operator fun div(a: Float): Point2D {
        return Point2D(this.x / a, this.y / a)
    }

}