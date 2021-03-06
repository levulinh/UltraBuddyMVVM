package andrew.studio.com.ultrabuddymvvm.ui.map

import andrew.studio.com.ultrabuddymvvm.data.repository.GroundRepository
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UltraMapViewModelFactory(
    private val groundRepository: GroundRepository,
    private val application: Application) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UltraMapViewModel(groundRepository, application) as T
    }
}