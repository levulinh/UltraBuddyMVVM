package andrew.studio.com.ultrabuddymvvm.ui.settings.obstaclesettings

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.data.entity.Polygon
import andrew.studio.com.ultrabuddymvvm.data.repository.GroundRepository
import andrew.studio.com.ultrabuddymvvm.utils.Helper
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception
import java.lang.NumberFormatException

const val MY_ID = "5cc8244ea17725001735abd8"
class ObstacleSettingsViewModel(
    private val groundRepository: GroundRepository,
    application: Application
) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private var _currentGroundParams = MutableLiveData<List<Polygon>>()
    val currentGroundParams: LiveData<List<Polygon>>
        get() = _currentGroundParams

    private var _eventClearEditText = MutableLiveData<Boolean>()
    val eventClearEditText: LiveData<Boolean>
        get() = _eventClearEditText

    lateinit var localObstacleList: List<Polygon>

    init {
        fetchCurrentGround()
    }

    private fun fetchCurrentGround() {
        uiScope.launch {
            localObstacleList =
                withContext(Dispatchers.IO) {
                    Helper.gsonToList<Polygon>(groundRepository.getCurrentGroundNonLive().obstacles)
                }
            _currentGroundParams.value = localObstacleList
        }
    }

    fun onAddButtonClick(str: String) {
        val polygon: Polygon
        _eventClearEditText.value = true
        try {
            polygon = Helper.stringToPolygon(str)
            val list = localObstacleList

            val newList: List<Polygon>

            list.toMutableList().also {
                it.add(polygon)
                newList = it.toList()
                localObstacleList = newList
            }
            _currentGroundParams.value = newList
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.illegal_syntax), Toast.LENGTH_SHORT).show()
        }
    }

    fun saveObstacles(adapter: ObstacleAdapter) {
        val list = adapter.getMyCurrentList()
        val polygonList = mutableListOf<Polygon>()
        try {
            for (s in list) {
                if (s.isEmpty()) continue
                polygonList.add(Helper.stringToPolygon(s))
            }
//            Timber.d(Helper.objectToString(polygonList))
            uiScope.launch {
                groundRepository.updateObstacles(userId = MY_ID, obstacles = Helper.objectToString(polygonList))
                Toast.makeText(context, "The obstacles has been saved!", Toast.LENGTH_LONG).show()
            }
        } catch (ex: Exception) {
            Toast.makeText(context, context.getString(R.string.illegal_syntax), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}
