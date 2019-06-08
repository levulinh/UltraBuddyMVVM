package andrew.studio.com.ultrabuddymvvm.corealgorithms

class AStar(
    private var maze: Array<IntArray>,
    private var xstart: Int,
    private var ystart: Int,
    private var diag: Boolean
) {

    private var open: MutableList<StarNode> = mutableListOf()
    private var closed: MutableList<StarNode> = mutableListOf()
    private var pathAStar: MutableList<StarNode> = mutableListOf()
    private var now: StarNode
    private var xend: Int = 0
    private var yend: Int = 0

    // Node class for convienience
    class StarNode(var parent: StarNode?, var x: Int, var y: Int, var g: Double, var h: Double) :
        Comparable<StarNode> {
        // Compare by f value (g + h)
        override fun compareTo(other: StarNode): Int {
            return (this.g + this.h - (other.g + other.h)).toInt()
        }
    }

    init {
        this.now = StarNode(null, xstart, ystart, 0.toDouble(), 0.toDouble())
    }

    fun findPathTo(xend: Int, yend: Int): MutableList<StarNode>? {
        this.xend = xend
        this.yend = yend
        this.closed.add(this.now)
        addNeigborsToOpenList()
        while (this.now.x != this.xend || this.now.y != this.yend) {
            if (this.open.isEmpty()) { // Nothing to examine
                return null
            }
            this.now = this.open[0] // get first node (lowest f score)
            this.open.removeAt(0) // remove it
            this.closed.add(this.now) // and add to the closed
            addNeigborsToOpenList()
        }
        this.pathAStar.add(0, this.now)
        while (this.now.x != this.xstart || this.now.y != this.ystart) {
            this.now = this.now.parent!!
            this.pathAStar.add(0, this.now)
        }
        return this.pathAStar
    }

    /*
** Looks in a given List<> for a node
**
** @return (bool) NeightborInListFound
*/
    private fun findNeighborInList(array: MutableList<StarNode>, node: StarNode): Boolean {
        for (n in array) {
            if (n.x == node.x && n.y == node.y) return true
        }
        return false
    }

    /*
** Calulate distance between this.now and xend/yend
**
** @return (int) distance
*/
    private fun distance(dx: Int, dy: Int): Double {
        return if (this.diag) { // if diagonal movement is alloweed
            Math.hypot(
                (this.now.x + dx - this.xend).toDouble(),
                (this.now.y + dy - this.yend).toDouble()
            ) // return hypothenuse
        } else {
            Math.abs((this.now.x + dx - this.xend).toDouble()) + Math.abs((this.now.y + dy - this.yend).toDouble()) // else return "Manhattan distance"
        }
    }

    private fun addNeigborsToOpenList() {
        var node: StarNode
        for (x in -1..1) {
            for (y in -1..1) {
                if (!this.diag && x != 0 && y != 0) {
                    continue // skip if diagonal movement is not allowed
                }
                node = StarNode(this.now, this.now.x + x, this.now.y + y, this.now.g, this.distance(x, y))
                if ((x != 0 || y != 0) // not this.now
                    && this.now.x + x >= 0 && this.now.x + x < this.maze[0].size // check maze boundaries
                    && this.now.y + y >= 0 && this.now.y + y < this.maze.size
                    && this.maze[this.now.y + y][this.now.x + x] != -1 // check if square is walkable
                    && !findNeighborInList(this.open, node) && !findNeighborInList(this.closed, node)
                ) { // if not already done
                    node.g = node.parent!!.g + 1.0 // Horizontal/vertical cost = 1.0
                    node.g += maze[this.now.y + y][this.now.x + x] // add movement cost for this square

                    // diagonal cost = sqrt(hor_cost² + vert_cost²)
                    // in this example the cost would be 12.2 instead of 11
                    /*
                        if (diag && x != 0 && y != 0) {
                            node.g += .4;	// Diagonal movement cost = 1.4
                        }
                        */
                    this.open.add(node)
                }
            }
        }
        this.open.sort()
    }
}