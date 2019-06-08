package andrew.studio.com.ultrabuddymvvm.corealgorithms

import andrew.studio.com.ultrabuddymvvm.corealgorithms.constants.Const.DENS
import andrew.studio.com.ultrabuddymvvm.corealgorithms.constants.Const.SAFE_DIST
import andrew.studio.com.ultrabuddymvvm.corealgorithms.constants.Direction.EAST
import andrew.studio.com.ultrabuddymvvm.corealgorithms.constants.Direction.NORTH
import andrew.studio.com.ultrabuddymvvm.corealgorithms.constants.Direction.SOUTH
import andrew.studio.com.ultrabuddymvvm.corealgorithms.constants.Direction.WEST
import andrew.studio.com.ultrabuddymvvm.corealgorithms.models.Node
import andrew.studio.com.ultrabuddymvvm.corealgorithms.models.Playground
import andrew.studio.com.ultrabuddymvvm.corealgorithms.models.Point2D
import andrew.studio.com.ultrabuddymvvm.internal.Helper.GRID_SIZE
import andrew.studio.com.ultrabuddymvvm.internal.Helper.GROUND_HEIGHT
import andrew.studio.com.ultrabuddymvvm.internal.Helper.GROUND_WIDTH
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.*


class CoreImpl(context: Context) : Core {
    private val appContext = context.applicationContext
    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    private val dir = arrayOf(
        intArrayOf(0, 1),
        intArrayOf(1, 0),
        intArrayOf(0, -1),
        intArrayOf(-1, 0)
    )

    private val ACTION_ARRAY = arrayOf(
        charArrayOf('S', 'A', 'R', 'L'),
        charArrayOf('A', 'S', 'L', 'R'),
        charArrayOf('L', 'R', 'S', 'A'),
        charArrayOf('R', 'L', 'A', 'S')

    )

    override val ground = Array(200) { BooleanArray(200) }
    private var numCol = 1
    private var numRow = 1
    override var path = mutableListOf<Node>()
    private val pathEncoded = mutableListOf<Int>()
    private var currentDir = NORTH
    private var currentDirAStar = NORTH
    private val tracking = IntArray(40000)
    private val isFree = Array(200) { BooleanArray(200) }

    override var trackFound = false
    override val w = preferences.getString(GROUND_WIDTH, "1000")!!.toInt()
    override val h = preferences.getString(GROUND_HEIGHT, "1000")!!.toInt()
    override val dens = preferences.getString(GRID_SIZE, "$DENS")!!.toInt()
    override var us: Int = 0
    override var vs: Int = 0
    override var ue: Int = 0
    override var ve: Int = 0

    init {
        numCol = w / dens
        numRow = h / dens
        for (i in 0 until 200)
            for (j in 0 until 200)
                isFree[i][j] = true
        for (i in 0 until tracking.size)
            tracking[i] = -1
    }

    private fun toRealPosition(i: Int, j: Int, dens: Int): Point2D {
        return Point2D(j.toFloat() * dens, i.toFloat() * dens)
    }

    private fun encode(x: Int, y: Int, numCol: Int): Int {
        return x * numCol + y
    }

    private fun decode(x: Int, numCol: Int): List<Int> {
        val u = x / numCol
        val v = x % numCol
        val d = mutableListOf<Int>()
        d.add(u)
        d.add(v)
        return d.toList()
    }

    override fun genMatrix(h: Int, w: Int, dens: Int, p: Playground) {
        val numRow = h / dens
        val numCol = w / dens

        for (i in 0 until 200)
            for (j in 0 until 200)
                ground[i][j] = false

        for (i in 0..numRow)
            for (j in 0..numCol)
                ground[i][j] = true

        for (i in 0..numRow)
            for (j in 0..numCol) {
                val realX = dens * (j + 0.5)
                val realY = dens * (i + 0.5)
                if (realX < SAFE_DIST || realX > w - SAFE_DIST || realY < SAFE_DIST || realY > h - SAFE_DIST) {
                    ground[i][j] = false
                }
            }

        for (poly in p.obstacles) {
            for (i in 0..numRow)
                for (j in 0..numCol) {
                    if (ground[i][j] && toRealPosition(i, j, dens) in poly)
                        ground[i][j] = false
                }
        }
    }

    private fun isAccessible(u: Int, v: Int): Boolean {
        if (u < 0 || u > numCol || v < 0 || v > numRow) return false
        return ground[u][v]
    }

    override fun doBfs(vs: Int, us: Int, ve: Int, ue: Int) {
        val q: Queue<Node> = ArrayDeque<Node>()

        if (!ground[us][vs]) {
            return
        }

        tracking[encode(us, vs, numCol)] = -1
        isFree[us][vs] = false
        q.add(Node(us, vs, 0))

        while (!q.isEmpty()) {
            val front = q.remove()
            for (i in 0 until 4) {
                val ua = front.x + dir[i][0]
                val va = front.y + dir[i][1]
                if (isFree[ua][va] && isAccessible(ua, va)) {
                    isFree[ua][va] = false
                    q.add(Node(ua, va, front.count + 1))
                    tracking[encode(ua, va, numCol)] = encode(front.x, front.y, numCol)
                    if (ua == ue && va == ve) {
                        trackFound = true
                        return
                    }
                }
            }
        }

    }

    override fun track(u: Int, v: Int) {
        path.add(Node(u, v))
        pathEncoded.add(encode(u, v, numCol))

        if (tracking[encode(u, v, numCol)] != -1) {
            val prev = decode(tracking[encode(u, v, numCol)], numCol)
            track(prev[0], prev[1])
        } else return
    }

    override fun toStarGround(): Array<IntArray> {
        val array = Array(numRow) { IntArray(numCol) }
        for (i in 0 until numRow)
            for (j in 0 until numCol) {
                array[i][j] = if (ground[i][j]) 0 else -1
            }
        return array
    }

    override fun toAction(sourceNode: Node, targetNode: Node): String {
        var turnDir: Int = NORTH
        if (targetNode.y > sourceNode.y) turnDir = EAST
        if (targetNode.y < sourceNode.y) turnDir = WEST
        if (targetNode.x > sourceNode.x) turnDir = NORTH
        if (targetNode.x < sourceNode.x) turnDir = SOUTH

        val action = ACTION_ARRAY[currentDir][turnDir]
        currentDir = turnDir
        return action.toString()
    }

    override fun toActionAstar(sourceNode: AStar.StarNode, targetNode: AStar.StarNode): String {
        var turnDir: Int = NORTH
        if (targetNode.x > sourceNode.x) turnDir = EAST
        if (targetNode.x < sourceNode.x) turnDir = WEST
        if (targetNode.y > sourceNode.y) turnDir = NORTH
        if (targetNode.y < sourceNode.y) turnDir = SOUTH

        val action = ACTION_ARRAY[currentDirAStar][turnDir]
        currentDirAStar = turnDir
        return action.toString()
    }

}