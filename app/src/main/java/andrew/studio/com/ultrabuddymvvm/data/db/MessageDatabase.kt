package andrew.studio.com.ultrabuddymvvm.data.db

import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MessageEntry::class],
    version = 1,
    exportSchema = false
)
abstract class MessageDatabase : RoomDatabase() {
    abstract val messageDao: MessageDao

    companion object{
        @Volatile private var instace: MessageDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instace ?: synchronized(LOCK){
            instace ?: buildDatabase(context). also { instace = it }
        }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                    MessageDatabase::class.java, "messageDatabase.db")
                    .build()
    }
}