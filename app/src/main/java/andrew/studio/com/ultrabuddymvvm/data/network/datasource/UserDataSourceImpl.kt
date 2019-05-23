package andrew.studio.com.ultrabuddymvvm.data.network.datasource

import andrew.studio.com.ultrabuddymvvm.data.entity.UserEntry
import andrew.studio.com.ultrabuddymvvm.data.network.UltraBuddyApiService
import andrew.studio.com.ultrabuddymvvm.internal.NoConnectivityException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class UserDataSourceImpl(
    private val ultraBuddyApiService: UltraBuddyApiService
) : UserDataSource {
    private var _downloadedUsers = MutableLiveData<List<UserEntry>>()
    override val downloadedUsers: LiveData<List<UserEntry>>
        get() = _downloadedUsers

    override suspend fun fetchAllUsers() {
        try {
            val fetchedUsers = ultraBuddyApiService
                .getAllUserAsync()
                .await()
            _downloadedUsers.postValue(fetchedUsers.users)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }
}