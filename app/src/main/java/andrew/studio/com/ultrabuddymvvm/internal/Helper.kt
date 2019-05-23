package andrew.studio.com.ultrabuddymvvm.internal

import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.UnsupportedEncodingException

object Helper {
    const val GROUND_WIDTH = "GROUND_WIDTH"
    const val GROUND_HEIGHT = "GROUND_HEIGHT"
    const val GRID_SIZE = "GRID_SIZE"
    const val WIFI_SSID = "WIFI_SSID"
    const val WIFI_PASSWORD = "WIFI_PASSWORD"
    const val OBSTACLES = "OBSTACLES"

    fun mqttPublish(client: MqttAndroidClient, topic: String, payload: String) {

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

    fun mqttSubscribe(client: MqttAndroidClient, topic: String) {
        val qos = 1
        val token = client.subscribe(topic, qos)
        token.actionCallback = object : IMqttActionListener {
            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.i("MQTT:SUBSCRIBE", "FAILED")
            }

            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.i("MQTT:SUBSCRIBE", "SUCCESS")
            }
        }
    }
}