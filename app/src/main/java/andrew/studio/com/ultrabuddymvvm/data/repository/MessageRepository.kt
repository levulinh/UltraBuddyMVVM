package andrew.studio.com.ultrabuddymvvm.data.repository

import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry
import andrew.studio.com.ultrabuddymvvm.data.entity.UserEntry
import androidx.lifecycle.LiveData

interface MessageRepository {
    suspend fun getAllMessage(from: String, to: String): LiveData<List<MessageEntry>>
    suspend fun getAllStoredMessage(from: String, to: String): List<MessageEntry>
    suspend fun addNewMessage(from: String, to: String, content: String, code: Int): List<MessageEntry>
    suspend fun addNewMessageNoDelay(from: UserEntry, to: UserEntry, content: String, code: Int): List<MessageEntry>
    suspend fun postNewMessage(from: String, to: String, content: String, code: Int)
}