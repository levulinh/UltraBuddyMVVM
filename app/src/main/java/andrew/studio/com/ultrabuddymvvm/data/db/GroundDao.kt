package andrew.studio.com.ultrabuddymvvm.data.db

import andrew.studio.com.ultrabuddymvvm.data.entity.CURRENT_GROUND_ID
import andrew.studio.com.ultrabuddymvvm.data.entity.GroundEntry
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GroundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(ground: GroundEntry)

    @Query("select * from `ground-table` where userId = '5cc8244ea17725001735abd8'")
    fun getGround(): GroundEntry

    @Query("select * from `ground-table` where userId = '5cc8244ea17725001735abd8'")
    fun getGroundLive(): LiveData<GroundEntry>

    @Query("update `ground-table` set obstacles = :newObstacles")
    fun updateObstacles(newObstacles: String)
}