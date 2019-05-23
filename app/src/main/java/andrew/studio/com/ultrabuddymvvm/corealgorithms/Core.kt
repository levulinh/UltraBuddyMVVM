package andrew.studio.com.ultrabuddymvvm.corealgorithms

import andrew.studio.com.ultrabuddymvvm.corealgorithms.models.Node
import andrew.studio.com.ultrabuddymvvm.corealgorithms.models.Playground

interface Core {
    val ground: Array<BooleanArray>
    var path: MutableList<Node>
    var trackFound: Boolean
    val w: Int
    val h: Int
    val dens: Int
    var us: Int
    var vs: Int
    var ue: Int
    var ve: Int

    fun genMatrix(h: Int, w: Int, dens: Int, p: Playground)
    fun doBfs(vs: Int, us: Int, ve: Int, ue: Int)
    fun track(u: Int, v: Int)
    fun toAction(sourceNode: Node, targetNode: Node): String
}