package andrew.studio.com.ultrabuddymvvm.data.repository

import andrew.studio.com.ultrabuddymvvm.data.db.MessageDao
import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry
import andrew.studio.com.ultrabuddymvvm.data.network.MessageDataSource
import andrew.studio.com.ultrabuddymvvm.data.network.response.AllMessageResponse
import andrew.studio.com.ultrabuddymvvm.data.network.response.MessageResponse
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageRepositoryImpl(
    private val messageDao: MessageDao,
    private val messageDataSource: MessageDataSource
) : MessageRepository {

    override suspend fun getAllStoredMessage(from: String, to: String): List<MessageEntry> {
        return withContext(Dispatchers.IO) {
            return@withContext messageDao.getAllFromNonLive(from, to)
        }
    }

    init {
        messageDataSource.downloadedMessage.observeForever{newMessages ->
            persistAllMessages(newMessages)
        }

        messageDataSource.newAddedMessage.observeForever{newMessage ->
            persistMessage(newMessage)
        }
    }

    override suspend fun getAllMessage(from: String, to: String): LiveData<List<MessageEntry>> {
        return withContext(Dispatchers.IO) {
            fetchMessage(from, to)
            return@withContext getAllDataFromDao(from, to)
        }
    }

    override suspend fun addNewMessage(from: String, to: String, content: String): List<MessageEntry> {
        return withContext(Dispatchers.IO) {
            addMessage(from, to, content)
            return@withContext getAllStoredMessage(from, to)
        }
    }

    private suspend fun getAllDataFromDao(from: String, to: String): LiveData<List<MessageEntry>> {
        return withContext(Dispatchers.IO) {
            messageDao.getAllFrom(from, to)
        }
    }

    private fun persistMessage(fetchedMessage: MessageResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            messageDao.insert(fetchedMessage.message)
        }
    }

    private fun persistAllMessages(fetchedMessages: AllMessageResponse){
        GlobalScope.launch(Dispatchers.IO) {
            messageDao.dropOldTable()
            messageDao.insert(fetchedMessages.message)
        }
    }

    private suspend fun fetchMessage(from: String, to: String) {
        messageDataSource.fetchAllMessages(from, to)
    }

    private suspend fun addMessage(from: String, to: String, content: String) {
        messageDataSource.addNewMessage(from, to, content)
    }
}