package andrew.studio.com.ultrabuddymvvm.data.network.response

import andrew.studio.com.ultrabuddymvvm.data.entity.UserEntry

data class AllUserResponse (
    val users: List<UserEntry>
)