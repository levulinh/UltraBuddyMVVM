package andrew.studio.com.ultrabuddymvvm.ui.home

import andrew.studio.com.ultrabuddymvvm.corealgorithms.Core
import andrew.studio.com.ultrabuddymvvm.data.repository.MessageRepository
import andrew.studio.com.ultrabuddymvvm.data.repository.UserRepository
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeViewModelFactory(
    private val messageRepository:  MessageRepository,
    private val userRepository: UserRepository,
    private val core: Core,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(messageRepository, userRepository, core, application) as T
    }
}