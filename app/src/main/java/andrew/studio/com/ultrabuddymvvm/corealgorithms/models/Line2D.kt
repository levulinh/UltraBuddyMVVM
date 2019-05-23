package andrew.studio.com.ultrabuddymvvm.corealgorithms.models

class Line2D() {
    var a: Float = 0f
    var b: Float = 0f
    lateinit var p1: Point2D
    lateinit var p2: Point2D

    constructor(p1: Point2D, p2: Point2D) : this() {
        this.p1 = p1
        this.p2 = p2

        val _a = p1.x
        val _b = p2.x
        val _c = p1.y
        val _d = p2.y

        a = (_d - _c) / (_b - _a)
        b = -(_a * (_d - _c)) / (_b - _a) + _c
    }

    constructor(a: Float, b: Float) : this() {
        this.a = a
        this.b = b
    }

    private fun intersection(l: Line2D): List<Point2D> {
        val a = this.a
        val c = this.b
        val b = l.a
        val d = l.b

        return if (a == b) listOf()
        else listOf(Point2D((d - c) / (a - b), a * (d - c) * (a - b) + c))
    }
}