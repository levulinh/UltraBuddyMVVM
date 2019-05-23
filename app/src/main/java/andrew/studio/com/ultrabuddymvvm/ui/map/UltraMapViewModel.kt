package andrew.studio.com.ultrabuddymvvm.ui.map

import andrew.studio.com.ultrabuddymvvm.UltraBuddyApplication
import andrew.studio.com.ultrabuddymvvm.data.entity.GroundEntry
import andrew.studio.com.ultrabuddymvvm.data.entity.Point
import andrew.studio.com.ultrabuddymvvm.data.entity.Polygon
import andrew.studio.com.ultrabuddymvvm.data.repository.GroundRepository
import andrew.studio.com.ultrabuddymvvm.utils.Helper
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.jetbrains.annotations.TestOnly
import timber.log.Timber

const val MY_ID = "5cc8244ea17725001735abd8"
const val LOCATION_TOPIC = "ub/position"

class UltraMapViewModel(
    private val groundRepository: GroundRepository, application: Application
) :
    AndroidViewModel(application) {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private var _groundParameters = MutableLiveData<GroundEntry?>()
    val groundParameters: LiveData<GroundEntry?>
        get() = _groundParameters

    private var _robotPositionPoint = MutableLiveData<RobotPosition>()
    val robotPositionPoint: LiveData<RobotPosition>
        get() = _robotPositionPoint

    private val state = application as UltraBuddyApplication
    private val client = state.client

    init {
        initGround()
        setMqttCallback()
        fetchGroundParameters()
    }

    private fun setMqttCallback() {
        client.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val messageString = message.toString()
                when (topic) {
                    LOCATION_TOPIC -> {
                        if (messageString.contains('|')){
                            val paramList = messageString.split('|')
                            val point = RobotPosition(paramList[0].toInt(), paramList[1].toInt(), paramList[2].toLong())
                            _robotPositionPoint.value = point
                        }
                    }
                }
            }

            override fun connectionLost(cause: Throwable?) {
                Timber.tag("MQTT:CONNECTION").i("LOST")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Timber.tag("MQTT:PUBLISH").i("DONE")
            }
        })
    }

    private fun fetchGroundParameters() {
        uiScope.launch {
            _groundParameters.value = withContext(Dispatchers.IO) {
                return@withContext groundRepository.getCurrentGroundNonLive()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    @TestOnly
    fun initGround() {
        val w = 1000
        val h = 1000
        val obstacles = listOf(
            Polygon(
                listOf(
                    Point(366, 611),
                    Point(143, 547),
                    Point(212, 319),
                    Point(466, 312)
                )
            )
        )

        val obstacleString = Helper.objectToString(obstacles)

        val groundEntry = GroundEntry(w, h, obstacleString, userId = MY_ID)
        uiScope.launch {
            groundRepository.addCurrentGround(groundEntry.userId, groundEntry.width, groundEntry.height, obstacleString)
            fetchGroundParameters()
        }
    }

    class RobotPosition(val x: Int, val y: Int, val direction: Long)
}
