package andrew.studio.com.ultrabuddymvvm.data.network.datasource

import andrew.studio.com.ultrabuddymvvm.data.network.response.GroundResponse
import androidx.lifecycle.LiveData

interface GroundDataSource {
    val downloadedGround: LiveData<GroundResponse>
    suspend fun fetchCurrentGround(userId: String)
    suspend fun upsertGround(userId: String, width: Int, height: Int, obstacles: String)
}