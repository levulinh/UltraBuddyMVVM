package andrew.studio.com.ultrabuddymvvm.data.entity

import com.google.gson.annotations.SerializedName

data class UserEntry(
    @SerializedName("_id")
    val id: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("user_name")
    val userName: String
)