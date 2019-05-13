package andrew.studio.com.ultrabuddymvvm.data.repository

import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry
import androidx.lifecycle.LiveData

interface MessageRepository {
    suspend fun getAllMessage(from: String, to: String): LiveData<List<MessageEntry>>
    suspend fun getAllStoredMessage(from: String, to: String): List<MessageEntry>
    suspend fun addNewMessage(from: String, to: String, content: String): List<MessageEntry>
}