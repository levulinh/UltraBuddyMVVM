package andrew.studio.com.ultrabuddymvvm.corealgorithms.models

class Node(val x: Int, val y: Int, var count: Int = 0) {
    override fun toString(): String {
        return "($y, $x)"
    }

    fun toStringReverse(): String {
        return Node(y, x).toString()
    }
}