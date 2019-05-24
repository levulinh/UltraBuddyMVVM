package andrew.studio.com.ultrabuddymvvm.data.datasource

import andrew.studio.com.ultrabuddymvvm.data.entity.UserEntry
import androidx.lifecycle.LiveData

interface UserDataSource {
    val downloadedUsers: LiveData<List<UserEntry>>

    suspend fun fetchAllUsers()
}