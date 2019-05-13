package andrew.studio.com.ultrabuddymvvm.data.db

import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.threeten.bp.LocalDate

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messageEntries: List<MessageEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messageEntry: MessageEntry)

    @Query("select * from messages")
    fun getAllData(): LiveData<List<MessageEntry>>

    @Query("select * from messages where ((from_id = :fromId) and (to_id = :toId)) or ((from_id = :toId) and (to_id = :fromId)) order by sentTime asc")
    fun getAllFrom(fromId: String, toId: String): LiveData<List<MessageEntry>>

    @Query("select * from messages where ((from_id = :fromId) and (to_id = :toId)) or ((from_id = :toId) and (to_id = :fromId)) order by sentTime asc")
    fun getAllFromNonLive(fromId: String, toId: String): List<MessageEntry>

    @Query("delete from messages")
    fun dropOldTable()

}