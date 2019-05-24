package andrew.studio.com.ultrabuddymvvm.data.repository

import andrew.studio.com.ultrabuddymvvm.data.db.UserDao
import andrew.studio.com.ultrabuddymvvm.data.entity.UserEntry
import andrew.studio.com.ultrabuddymvvm.data.datasource.UserDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val userDataSource: UserDataSource
    ) : UserRepository {
    override suspend fun getAllUser(): List<UserEntry> {
        return withContext(Dispatchers.IO) {
            fetchUsers()
            return@withContext getAllUserFromDao()
        }
    }

    private suspend fun getAllUserFromDao(): List<UserEntry> {
        return withContext(Dispatchers.IO) {
            userDao.getAllUsers()
        }
    }

    private suspend fun fetchUsers() {
        userDataSource.fetchAllUsers()
    }

    init {
        userDataSource.downloadedUsers.observeForever {
            persistUsers(it)
        }
    }

    private fun persistUsers(users: List<UserEntry>) {
        GlobalScope.launch {
            userDao.insertUser(users)
        }
    }
}