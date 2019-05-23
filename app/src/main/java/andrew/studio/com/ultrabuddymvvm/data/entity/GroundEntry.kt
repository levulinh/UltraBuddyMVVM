package andrew.studio.com.ultrabuddymvvm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

const val CURRENT_GROUND_ID = ""

@Entity(tableName = "ground-table")
data class GroundEntry(
    val width: Int,
    val height: Int,
    val obstacles: String,
    @PrimaryKey(autoGenerate = false)
    val userId: String
)