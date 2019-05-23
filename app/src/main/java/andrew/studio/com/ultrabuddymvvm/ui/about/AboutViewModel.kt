package andrew.studio.com.ultrabuddymvvm.ui.about

import andrew.studio.com.ultrabuddymvvm.UltraBuddyApplication
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.eclipse.paho.client.mqttv3.*

class AboutViewModel(application: Application) : AndroidViewModel(application){
    private val state = application as UltraBuddyApplication
    private val client = state.client

    private var _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackbarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent
    fun doneShowSnackbarEvent() {
        _showSnackbarEvent.value = false
    }

    private var _sendEmailEvent = MutableLiveData<Boolean>()
    val sendEmailEvent: LiveData<Boolean>
        get() = _sendEmailEvent

    fun onClickContactFab() {
        _showSnackbarEvent.value = true
        _sendEmailEvent.value = true
    }

    init {
        client.setCallback(object : MqttCallback{
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.i("MQTT:SUBSCRIBE", "$topic : ${message.toString()}")
            }

            override fun connectionLost(cause: Throwable?) {
                Log.i("MQTT:CONNECTION", "LOST")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.i("MQTT:PUBLISH", "DONE")
            }
        })
    }
}
