package andrew.studio.com.ultrabuddymvvm.ui.home

import andrew.studio.com.ultrabuddymvvm.data.repository.MessageRepository
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeViewModelFactory(
    private val messageRepository:  MessageRepository,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(messageRepository, application) as T
    }
}