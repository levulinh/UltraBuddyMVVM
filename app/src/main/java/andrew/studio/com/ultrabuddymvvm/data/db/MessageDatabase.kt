package andrew.studio.com.ultrabuddymvvm.data.db

import andrew.studio.com.ultrabuddymvvm.data.entity.GroundEntry
import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry
import andrew.studio.com.ultrabuddymvvm.data.entity.UserEntry
import andrew.studio.com.ultrabuddymvvm.data.typeConverters.PointConverter
import andrew.studio.com.ultrabuddymvvm.data.typeConverters.PolygonConverter
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [MessageEntry::class, GroundEntry::class, UserEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(PolygonConverter::class, PointConverter::class)
abstract class MessageDatabase : RoomDatabase() {
    abstract val messageDao: MessageDao
    abstract val groundDao: GroundDao
    abstract val userDao: UserDao

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