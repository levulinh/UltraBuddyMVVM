package andrew.studio.com.ultrabuddymvvm.corealgorithms.utils

import org.jetbrains.annotations.TestOnly
import kotlin.random.Random


object Utils {
    fun generateRangedRandom(max: Float): Float {
        return (2 * Random.nextFloat() - 1) * max
    }
}