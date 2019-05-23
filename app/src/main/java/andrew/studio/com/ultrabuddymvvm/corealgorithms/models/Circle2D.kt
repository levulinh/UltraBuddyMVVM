package andrew.studio.com.ultrabuddymvvm.corealgorithms.models

import andrew.studio.com.ultrabuddymvvm.corealgorithms.constants.Const.EPS
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


class Circle2D(val center: Point2D, val radius: Float) {
    constructor(center: Point2D, pointOnCircle: Point2D) : this(center, center.distance(pointOnCircle))

    private fun shortestDistance(p: Point2D): Float {
        return abs(p.distance(center) - radius)
    }

    fun closestPoint(p1: Point2D, p2: Point2D): Point2D {
        val dist1 = shortestDistance(p1)
        val dist2 = shortestDistance(p2)
        return if (dist1 >= dist2) p2 else p1
    }

    fun intersection(l: Line2D): List<Point2D> {
        val a = l.a
        val b = l.b
        val x0 = center.x
        val y0 = center.y
        val r = radius

        val m = 1 + a.pow(2)
        val n = -2 * x0 + 2 * a * (b - y0)
        val p = x0.pow(2) + (b - y0).pow(2) - r.pow(2)

        val delta = n.pow(2) - 4 * m * p
        val xr0: Float
        val yr0: Float
        val xr1: Float
        val yr1: Float

        if (delta < -EPS) return listOf()
        if (-EPS <= delta && delta <= EPS) {
            xr0 = -n / (2 * m)
            yr0 = a * xr0 + b
            return listOf(Point2D(xr0, yr0))
        }

        xr0 = (-n + sqrt(delta)) / (2 * m)
        xr1 = (-n - sqrt(delta)) / (2 * m)
        yr0 = a * xr0 + b
        yr1 = a * xr1 + b
        return listOf(Point2D(xr0, yr0), Point2D(xr1, yr1))
    }

    // Source: https://stackoverflow.com/questions/3349125/circle-circle-intersection-points
    // Get a list of intersection points of 2 circles
    // INPUT: c (Circle2D)
    // OUTPUT: (vector<Point2D>)
    fun intersection(c: Circle2D): ArrayList<Point2D> {
        val r0 = this.radius
        val r1 = c.radius
        val h: Float

        val P0 = this.center
        val P1 = c.center
        val P2: Point2D

        val d = P0.distance(P1)
        val a: Float
        val x0: Float
        val x1: Float
        val x2: Float
        val y0: Float
        val y1: Float
        val y2: Float
        val x31: Float
        val x32: Float
        val y31: Float
        val y32: Float
        if (d > r0 + r1 - EPS || d < Math.abs(r0 - r1) + EPS) {
            return ArrayList()
        } else {
            a = ((Math.pow(r0.toDouble(), 2.0) - Math.pow(r1.toDouble(), 2.0) + Math.pow(d.toDouble(), 2.0)) / (2 * d))
                .toFloat()
            P2 = P0 + (P1 + -P0) * a / d
            x0 = P0.x
            y0 = P0.y
            x1 = P1.x
            y1 = P1.y
            x2 = P2.x
            y2 = P2.y
            h = Math.sqrt(Math.pow(r0.toDouble(), 2.0) - Math.pow(a.toDouble(), 2.0)).toFloat()
            x31 = x2 + h * (y1 - y0) / d
            y31 = y2 - h * (x1 - x0) / d

            x32 = x2 - h * (y1 - y0) / d
            y32 = y2 + h * (x1 - x0) / d

            return ArrayList(listOf(Point2D(x31, y31), Point2D(x32, y32)))
        }
    }

    fun hasIntersection(c: Circle2D): Boolean{
        val r0=  radius
        val r1 = c.radius

        val P0 = center
        val P1 = c.center

        val d = P0.distance(P1)
        return !(d> r0 + r1 -EPS || d< abs(r0-r1) + EPS)
    }

}