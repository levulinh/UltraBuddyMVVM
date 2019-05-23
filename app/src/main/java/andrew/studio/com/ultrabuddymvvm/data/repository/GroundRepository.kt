package andrew.studio.com.ultrabuddymvvm.data.repository

import andrew.studio.com.ultrabuddymvvm.data.entity.GroundEntry
import androidx.lifecycle.LiveData

interface GroundRepository {
    suspend fun getCurrentGround(): LiveData<GroundEntry>
    suspend fun getCurrentGroundNonLive(): GroundEntry
    suspend fun addCurrentGround(userId: String, width: Int, height: Int, obstacles: String)
    suspend fun fetchGround(userId: String)
}