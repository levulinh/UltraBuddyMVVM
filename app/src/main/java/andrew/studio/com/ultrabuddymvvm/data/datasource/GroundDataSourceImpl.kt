package andrew.studio.com.ultrabuddymvvm.data.datasource

import andrew.studio.com.ultrabuddymvvm.data.network.UltraBuddyApiService
import andrew.studio.com.ultrabuddymvvm.data.network.response.GroundResponse
import andrew.studio.com.ultrabuddymvvm.internal.NoConnectivityException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber

class GroundDataSourceImpl(
    private val ultraBuddyApiService: UltraBuddyApiService
) : GroundDataSource {
    private var _downloadedGround = MutableLiveData<GroundResponse>()
    override val downloadedGround: LiveData<GroundResponse>
        get() = _downloadedGround

    override suspend fun fetchCurrentGround(userId: String) {
        try {
            val fetchedGround = ultraBuddyApiService
                .getGroundAsync(userId)
                .await()
            _downloadedGround.postValue(fetchedGround)
        } catch (e: NoConnectivityException) {
            Timber.tag("Connectivity").e(e, "No internet connection")
        }
    }

    override suspend fun upsertGround(userId: String, width: Int, height: Int, obstacles: String) {
        try {
            val fetchedGround = ultraBuddyApiService
                .addGroundAsync(userId, width, height, obstacles)
                .await()
            _downloadedGround.postValue(fetchedGround)
        } catch (e: NoConnectivityException) {
            Timber.tag("Connectivity").e(e, "No internet connection")
        }
    }
}