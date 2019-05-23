package andrew.studio.com.ultrabuddymvvm.corealgorithms.models

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


class Segment2D(val p1: Point2D, val p2: Point2D) {
    operator fun contains(p: Point2D): Boolean {
        val l = this
        return (p.x <= max(l.p1.x, l.p2.x) && p.x >= min(l.p1.x, l.p2.x) &&
                p.y <= max(l.p1.y, l.p2.y) && p.y >= min(l.p1.y, l.p2.y))
    }

    companion object {

        fun direction(a: Point2D, b: Point2D, c: Point2D): Int {
            val value = (b.y - a.y) * (c.x - b.x) - (b.x - a.x) * (c.y - b.y)
            if (value == 0f) {
                return 0
            } else if (value < 0) {
                return 2
            }
            return 1
        }

        fun intersect(l1: Segment2D, l2: Segment2D): Boolean {
            val dir1 = direction(l1.p1, l1.p2, l2.p1)
            val dir2 = direction(l1.p1, l1.p2, l2.p2)
            val dir3 = direction(l2.p1, l2.p2, l1.p1)
            val dir4 = direction(l2.p1, l2.p2, l1.p2)

            if (dir1 != dir2 && dir3 != dir4)
                return true           //they are intersecting
            if (dir1 == 0 && l2.p1 in l1)     //when p2 of line2 are on the line1
                return true
            if (dir2 == 0 && l2.p2 in l1)     //when p1 of line2 are on the line1
                return true
            if (dir3 == 0 && l1.p1 in l2)     //when p2 of line1 are on the line2
                return true
            if (dir4 == 0 && l1.p2 in l2)        //when p1 of line1 are on the line2
                return true
            return false
        }
    }
}