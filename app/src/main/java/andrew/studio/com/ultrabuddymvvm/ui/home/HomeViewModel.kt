package andrew.studio.com.ultrabuddymvvm.ui.home

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.UltraBuddyApplication
import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry
import andrew.studio.com.ultrabuddymvvm.data.repository.MessageRepository
import andrew.studio.com.ultrabuddymvvm.internal.lazyDeferred
import android.app.Application
import android.service.voice.AlwaysOnHotwordDetector
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*
import org.eclipse.paho.client.mqttv3.*
import java.io.UnsupportedEncodingException
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.Charset

const val MY_ID = "5cc8244ea17725001735abd8"
const val ADMIN_ID = "5cc8241ca17725001735abd6"

class HomeViewModel(private val messageRepository: MessageRepository, application: Application) :
    AndroidViewModel(application) {
    private val applicationContext = application.applicationContext
    private var job = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + job)

    private val state = application as UltraBuddyApplication
    private val client = state.client

    private var _storedMessage = MutableLiveData<List<MessageEntry>>()
    val storedMessage: LiveData<List<MessageEntry>>
        get() = _storedMessage

    private var _hideRequestSheetEvent = MutableLiveData<Boolean>()
    val hideRequestSheetEvent: LiveData<Boolean>
        get() = _hideRequestSheetEvent

    private var _navigateToAboutEvent = MutableLiveData<Boolean>().apply { value = false }
    val navigateToAboutEvent: LiveData<Boolean>
        get() = _navigateToAboutEvent

    fun doneNavigateToAbout() {
        _navigateToAboutEvent.value = false
    }

    private var _status = MutableLiveData<Boolean>().apply { value = false }

    val isProcessVisible: LiveData<Int> = Transformations.map(_status) { isLoading ->
        if (isLoading) View.VISIBLE else View.GONE
    }

    val statusString: LiveData<String> = Transformations.map(_status) { isLoading ->
        if (isLoading) applicationContext.getString(R.string.sending_message)
        else applicationContext.getString(R.string.swipe_up_to_see_more)
    }

    private var _scrollDownEvent = MutableLiveData<Boolean>()
    val scrollDownEvent: LiveData<Boolean>
        get() = _scrollDownEvent

    private var _showRequestSheetEvent = MutableLiveData<Boolean>()
    val showRequestSheetEvent: LiveData<Boolean>
        get() = _showRequestSheetEvent

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    init {
        fetchData()
    }

    private fun fetchData() {
        uiScope.launch {
            _storedMessage.value = withContext(Dispatchers.IO) {
                messageRepository.getAllStoredMessage(ADMIN_ID, MY_ID)
            }
        }
    }

    val message by lazyDeferred {
        messageRepository.getAllMessage(ADMIN_ID, MY_ID)
    }

    fun onClickScrollDownFab() {
        _scrollDownEvent.value = true
    }

    private var _emptyLayoutVisible = MutableLiveData<Boolean>()
    val emptyLayoutVisible: LiveData<Boolean>
        get() = _emptyLayoutVisible

    fun onConversationLoaded(isEmpty: Boolean) {
        _emptyLayoutVisible.value = isEmpty
    }

    fun onRequestClick(position: Int) {
        val requestStrings = applicationContext.resources.getStringArray(R.array.request_strings)
        mqttPublish(topic = "ub/request", payload = "req:$position")
        _hideRequestSheetEvent.value = true

        uiScope.launch {
            _status.value = true
            _storedMessage.value = withContext(Dispatchers.IO) {
                messageRepository.addNewMessage(from = MY_ID, to = ADMIN_ID, content = requestStrings[position])
            }
            _status.value = false
            respondForCode(position)

        }
    }

    fun toggleRequestSheet() {
        _showRequestSheetEvent.value = true
    }

    private suspend fun respondForCode(position: Int) {
        val responseStrings = applicationContext.resources.getStringArray(R.array.response_strings)
        _storedMessage.value = withContext(Dispatchers.IO) {
            messageRepository.addNewMessage(from = ADMIN_ID, to = MY_ID, content = responseStrings[position])
        }
        when (position) {
            3 -> _navigateToAboutEvent.postValue(true)
        }
    }

    private fun mqttPublish(topic: String, payload: String) {

        val encodedPayload: ByteArray
        try {
            encodedPayload = payload.toByteArray(Charsets.UTF_8)
            val message = MqttMessage(encodedPayload)
            client.publish(topic, message)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

}
