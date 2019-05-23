package andrew.studio.com.ultrabuddymvvm.corealgorithms.models

import andrew.studio.com.ultrabuddymvvm.corealgorithms.constants.Const.DENS

class Polygon2D(val points: List<Point2D> = listOf()) {
    private fun containNoLine(p: Point2D): Boolean {
        val poly = this.points
        val n = poly.size
        if (n < 3) return false
        val exline = Segment2D(p, Point2D(9999f, p.y))
        var count = 0
        var i = 0
        do {
            val side = Segment2D(poly[i], poly[(i + 1) % n])
            if (Segment2D.intersect(side, exline)) {
                if (Segment2D.direction(side.p1, p, side.p2) == 0)
                    return p in side
                count++
            }
            i = (i + 1) % n
        } while (i != 0)
        return count % 2 == 1
    }

    operator fun contains(p: Point2D): Boolean {
        val poly = this.points
        val cell = listOf(
            p,
            Point2D(p.x - DENS, p.y),
            Point2D(p.x - DENS, p.y - DENS),
            Point2D(p.x, p.y - DENS)
        )

        val n = poly.size
        val m = cell.size
        if (n < 2) return false

        var i = 0
        do {
            var j = 0
            val sidePoly = Segment2D(poly[i], poly[(i + 1) % n])
            do {
                val sideCell = Segment2D(cell[j], cell[(j + 1) % m])
                if (Segment2D.intersect(sidePoly, sideCell)) return true
                j = (j + 1) % n
            } while (j != 0)
            i = (i + 1) % n
        } while (i != 0)

        if (!Polygon2D(poly).containNoLine(p) && !Polygon2D(cell).containNoLine(poly[0]))
            return false
        return true
    }

}