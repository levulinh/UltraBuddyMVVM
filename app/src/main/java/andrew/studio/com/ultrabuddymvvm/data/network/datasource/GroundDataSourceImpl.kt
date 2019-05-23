package andrew.studio.com.ultrabuddymvvm.data.network.datasource

import andrew.studio.com.ultrabuddymvvm.data.network.UltraBuddyApiService
import andrew.studio.com.ultrabuddymvvm.data.network.response.GroundResponse
import andrew.studio.com.ultrabuddymvvm.internal.NoConnectivityException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

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
            Log.e("Connectivity", "No internet connection", e)
        }
    }

    override suspend fun upsertGround(userId: String, width: Int, height: Int, obstacles: String) {
        try {
            val fetchedGround = ultraBuddyApiService
                .addGroundAsync(userId, width, height, obstacles)
                .await()
            _downloadedGround.postValue(fetchedGround)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }
}