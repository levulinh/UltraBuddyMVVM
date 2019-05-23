package andrew.studio.com.ultrabuddymvvm.data.repository

import andrew.studio.com.ultrabuddymvvm.data.entity.UserEntry
import androidx.lifecycle.LiveData

interface UserRepository {
    suspend fun getAllUser(): List<UserEntry>
}