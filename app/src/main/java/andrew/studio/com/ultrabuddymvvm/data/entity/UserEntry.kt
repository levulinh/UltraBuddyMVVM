package andrew.studio.com.ultrabuddymvvm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class UserEntry(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("_id")
    val id: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("user_name")
    val userName: String
)