package andrew.studio.com.ultrabuddymvvm.data.db

import andrew.studio.com.ultrabuddymvvm.data.entity.UserEntry
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("select * from users where id=:userId")
    suspend fun getUser(userId: String): UserEntry

    @Query("select * from users")
    suspend fun getAllUsers(): List<UserEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(
        userEntry: UserEntry
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(
        userEntry: List<UserEntry>
    )
}