package andrew.studio.com.ultrabuddymvvm.ui.settings.obstaclesettings

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.data.entity.GroundEntry
import andrew.studio.com.ultrabuddymvvm.data.entity.Polygon
import andrew.studio.com.ultrabuddymvvm.data.repository.GroundRepository
import andrew.studio.com.ultrabuddymvvm.utils.Helper
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class ObstacleSettingsViewModel(
    private val groundRepository: GroundRepository,
    application: Application
) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private var _currentGroundParams = MutableLiveData<GroundEntry>()
    val currentGroundParams: LiveData<GroundEntry>
        get() = _currentGroundParams

    private var _newObstacle = MutableLiveData<Polygon>()
    val newObstacle: LiveData<Polygon>
        get() = _newObstacle

    init {
        fetchCurrentGround()
    }

    private fun fetchCurrentGround() {
        uiScope.launch {
            _currentGroundParams.value =
                withContext(Dispatchers.IO) { return@withContext groundRepository.getCurrentGroundNonLive() }
        }
    }

    fun onAddButtonClick(str: String) {
        val polygon: Polygon
        try {
            polygon = Helper.stringToPolygon(str)
            _newObstacle.value = polygon
        } catch (e: NumberFormatException) {
            Toast.makeText(context, context.getString(R.string.illegal_syntax), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}
