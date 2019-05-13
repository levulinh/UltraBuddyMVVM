package andrew.studio.com.ultrabuddymvvm.data.network

import andrew.studio.com.ultrabuddymvvm.data.network.response.AllMessageResponse
import andrew.studio.com.ultrabuddymvvm.data.network.response.MessageResponse
import androidx.lifecycle.LiveData

interface MessageDataSource {
    val downloadedMessage: LiveData<AllMessageResponse>
    val newAddedMessage: LiveData<MessageResponse>

    suspend fun fetchAllMessages(
        from: String,
        to: String
    )

    suspend fun addNewMessage(
        from: String,
        to: String,
        content: String
    )
}