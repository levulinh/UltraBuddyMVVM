package andrew.studio.com.ultrabuddymvvm.data.network.response

import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry

data class AllMessageResponse(
    val message: List<MessageEntry>
)