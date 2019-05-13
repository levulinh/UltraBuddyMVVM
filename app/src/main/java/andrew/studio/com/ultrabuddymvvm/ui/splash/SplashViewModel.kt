package andrew.studio.com.ultrabuddymvvm.ui.splash

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;

private const val DELAY_TIME_TO_NAVIGATE: Long = 1000L

class SplashViewModel : ViewModel() {

    private var _navigateToHomeEvent = MutableLiveData<Boolean>()
    val navigateToHomeEvent: LiveData<Boolean>
        get() = _navigateToHomeEvent
    fun doneNavigateToHome() {
        _navigateToHomeEvent.value = false
    }

    init {
        Handler().postDelayed({
            _navigateToHomeEvent.value = true
        }, DELAY_TIME_TO_NAVIGATE)
    }
}
