package andrew.studio.com.ultrabuddymvvm.data.repository

import andrew.studio.com.ultrabuddymvvm.data.db.GroundDao
import andrew.studio.com.ultrabuddymvvm.data.entity.GroundEntry
import andrew.studio.com.ultrabuddymvvm.data.network.datasource.GroundDataSource
import andrew.studio.com.ultrabuddymvvm.internal.Helper
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroundRepositoryImpl(private val groundDao: GroundDao,
                           private val groundDataSource: GroundDataSource

) : GroundRepository {
    override suspend fun fetchGround(userId: String) {
        GlobalScope.launch {
            groundDataSource.fetchCurrentGround(userId)
        }
    }

    init {
        groundDataSource.downloadedGround.observeForever {
            persistGround(it.ground)
        }
    }

    private fun persistGround(ground: GroundEntry) {
        GlobalScope.launch {
            groundDao.upsert(ground)
        }
    }

    override suspend fun addCurrentGround(userId: String, width: Int, height: Int, obstacles: String) {
        withContext(Dispatchers.IO){
            groundDataSource.upsertGround(userId, width, height, obstacles)
        }
    }

    override suspend fun getCurrentGround(): LiveData<GroundEntry> {
        return withContext(Dispatchers.IO) {
            groundDao.getGroundLive()
        }
    }

    override suspend fun getCurrentGroundNonLive(): GroundEntry {
        return withContext(Dispatchers.IO){
            return@withContext groundDao.getGround()
        }
    }

}