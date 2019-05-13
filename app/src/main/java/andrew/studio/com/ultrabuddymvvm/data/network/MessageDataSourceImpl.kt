package andrew.studio.com.ultrabuddymvvm.data.network

import andrew.studio.com.ultrabuddymvvm.data.network.response.AllMessageResponse
import andrew.studio.com.ultrabuddymvvm.data.network.response.MessageResponse
import andrew.studio.com.ultrabuddymvvm.internal.NoConnectivityException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MessageDataSourceImpl(
    private val ultraBuddyApiService: UltraBuddyApiService
) : MessageDataSource {
    private var _downloadedAllMessages = MutableLiveData<AllMessageResponse>()
    override val downloadedMessage: LiveData<AllMessageResponse>
        get() = _downloadedAllMessages

    private var _newAddedMessage = MutableLiveData<MessageResponse>()
    override val newAddedMessage: LiveData<MessageResponse>
        get() = _newAddedMessage

    override suspend fun addNewMessage(from: String, to: String, content: String) {
        try {
            val addedNewMessage = ultraBuddyApiService
                .addNewMessageAsync(from, to, content)
                .await()
            _newAddedMessage.postValue(addedNewMessage)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }

    override suspend fun fetchAllMessages(from: String, to: String) {
        try{
            val fetchedAllMessages = ultraBuddyApiService
                .getAllMessageAsync(from, to)
                .await()
            _downloadedAllMessages.postValue(fetchedAllMessages)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }
}