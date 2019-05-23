package andrew.studio.com.ultrabuddymvvm.corealgorithms.models

import andrew.studio.com.ultrabuddymvvm.corealgorithms.utils.Utils

class Playground(val A: Point2D, val B: Point2D, val C: Point2D) {
    lateinit var D: Point2D
    private lateinit var ca: Circle2D
    private lateinit var cb: Circle2D
    private lateinit var cc: Circle2D
    var obstacles = mutableListOf<Polygon2D>()


    private fun bestPointFrom(_ca: Circle2D, _cb: Circle2D, _cc: Circle2D): Point2D {
        val interAB: List<Point2D>
        var interlc: List<Point2D>
        val D: Point2D
        val E: Point2D
        val l: Line2D

        return if (!_ca.hasIntersection(_cb)) {
            l = Line2D(_ca.center, _cb.center)
            interlc = _ca.intersection(l)
            D = _cb.closestPoint(interlc[0], interlc[1])
            interlc = _cb.intersection(l)
            E = _ca.closestPoint(interlc[0], interlc[1])
            D + (E - D) / 2f
        }else{
            interAB = _ca.intersection(_cb)
            if (interAB.size == 1) interAB[0]
            else _cc.closestPoint(interAB[0], interAB[1])
        }
    }

    fun initCirclesWithNoise(){
        ca = Circle2D(A, D.distance(A) + Utils.generateRangedRandom(0.3f))
        cb = Circle2D(B, D.distance(B) + Utils.generateRangedRandom(0.3f))
        cc = Circle2D(C, D.distance(C) + Utils.generateRangedRandom(0.3f))
    }

    fun estimateD(): Point2D{
        val Iab = bestPointFrom(ca, cb, cc)
        val Ibc = bestPointFrom(cb, cc, ca)
        val Ica = bestPointFrom(cc, ca, cb)

        return (Iab + Ibc + Ica) / 3f
    }

}