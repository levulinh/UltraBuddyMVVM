package andrew.studio.com.ultrabuddymvvm.data.repository

import andrew.studio.com.ultrabuddymvvm.data.db.MessageDao
import andrew.studio.com.ultrabuddymvvm.data.db.UserDao
import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry
import andrew.studio.com.ultrabuddymvvm.data.entity.UserEntry
import andrew.studio.com.ultrabuddymvvm.data.network.datasource.MessageDataSource
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
    private val userDao: UserDao,
    private val messageDataSource: MessageDataSource
) : MessageRepository {

    private val source = Regex("[a-z0-9]+").toString()
    private val outputStrLength = 24

    private fun getFakeId() = (0..outputStrLength)
        .map { source.random() }
        .joinToString("")

    override suspend fun getAllStoredMessage(from: String, to: String): List<MessageEntry> {
        return withContext(Dispatchers.IO) {
            return@withContext messageDao.getAllFromNonLive(from, to)
        }
    }

    init {
        messageDataSource.downloadedMessage.observeForever { newMessages ->
            persistAllMessages(newMessages)
        }

        messageDataSource.newAddedMessage.observeForever { newMessage ->
            persistMessage(newMessage)
        }
    }

    override suspend fun getAllMessage(from: String, to: String): LiveData<List<MessageEntry>> {
        return withContext(Dispatchers.IO) {
            fetchMessage(from, to)
            return@withContext getAllDataFromDao(from, to)
        }
    }

    override suspend fun addNewMessageNoDelay(
        from: UserEntry,
        to: UserEntry,
        content: String,
        code: Int
    ): List<MessageEntry> {
        return withContext(Dispatchers.IO) {
            addMessageNoDelay(from, to, content, code)
            return@withContext getAllStoredMessage(from.id, to.id)
        }
    }

    override suspend fun addNewMessage(from: String, to: String, content: String, code: Int): List<MessageEntry> {
        return withContext(Dispatchers.IO) {
            addMessage(from, to, content, code)
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

    private fun persistAllMessages(fetchedMessages: AllMessageResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            messageDao.dropOldTable()
            messageDao.insert(fetchedMessages.message)
        }
    }

    private suspend fun fetchMessage(from: String, to: String) {
        messageDataSource.fetchAllMessages(from, to)
    }

    override suspend fun postNewMessage(from: String, to: String, content: String, code: Int) {
        messageDataSource.addNewMessageNoRefresh(from, to, content, code)
    }

    private fun addMessageNoDelay(from: UserEntry, to: UserEntry, content: String, code: Int) {
        MessageResponse(
            MessageEntry(
                id = getFakeId(),
                from = from,
                to = to,
                content = content,
                code = code,
                sentTime = System.currentTimeMillis()
            )
        ).also {
            persistMessage(it)
        }
    }

    private suspend fun addMessage(from: String, to: String, content: String, code: Int) {
        messageDataSource.addNewMessage(from, to, content, code)
    }

}