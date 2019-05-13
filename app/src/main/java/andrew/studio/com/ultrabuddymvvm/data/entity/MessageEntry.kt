package andrew.studio.com.ultrabuddymvvm.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "messages")
data class MessageEntry(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("_id")
    val id: String,
    val content: String,
    @Embedded(prefix = "from_")
    val from: UserEntry,
    @SerializedName("sent_time")
    val sentTime: Long,
    @Embedded(prefix = "to_")
    val to: UserEntry
)