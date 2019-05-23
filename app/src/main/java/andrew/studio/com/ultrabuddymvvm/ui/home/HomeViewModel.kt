package andrew.studio.com.ultrabuddymvvm.ui.home

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.UltraBuddyApplication
import andrew.studio.com.ultrabuddymvvm.corealgorithms.Core
import andrew.studio.com.ultrabuddymvvm.corealgorithms.models.Playground
import andrew.studio.com.ultrabuddymvvm.corealgorithms.models.Point2D
import andrew.studio.com.ultrabuddymvvm.corealgorithms.models.Polygon2D
import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry
import andrew.studio.com.ultrabuddymvvm.data.entity.UserEntry
import andrew.studio.com.ultrabuddymvvm.data.repository.MessageRepository
import andrew.studio.com.ultrabuddymvvm.data.repository.UserRepository
import andrew.studio.com.ultrabuddymvvm.internal.Helper
import andrew.studio.com.ultrabuddymvvm.internal.lazyDeferred
import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*
import timber.log.Timber

const val MY_ID = "5cc8244ea17725001735abd8"
const val ADMIN_ID = "5cc8241ca17725001735abd6"

class HomeViewModel(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val core: Core,
    application: Application
) :
    AndroidViewModel(application) {
    private val applicationContext = application.applicationContext
    private var job = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + job)

    private val state = application as UltraBuddyApplication
    private val client = state.client

    private var _storedMessage = MutableLiveData<List<MessageEntry>>()
    val storedMessage: LiveData<List<MessageEntry>>
        get() = _storedMessage

    private var listUser = listOf<UserEntry>()

    private var _hideRequestSheetEvent = MutableLiveData<Boolean>()
    val hideRequestSheetEvent: LiveData<Boolean>
        get() = _hideRequestSheetEvent

    private var _navigateResponse = MutableLiveData<Int>().apply { value = -1 }
    val navigateResponse: LiveData<Int>
        get() = _navigateResponse

    fun doneNavigate() {
        _navigateResponse.value = -1
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

    private lateinit var userAdmin: UserEntry
    private lateinit var userMe: UserEntry

    private lateinit var p: Playground

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    init {
        initPlayGround()
//        fetchData()
        fetchUser()
    }

    private fun initPlayGround() {
        p = Playground(Point2D(0f, 0f), Point2D(1000f, 0f), Point2D(0f, 1000f))
            .apply {
                D = Point2D(270f, 520f)
                initCirclesWithNoise()
                obstacles.apply {
                    add(
                        Polygon2D(
                            listOf(
                                Point2D(366f, 611f),
                                Point2D(143f, 547f),
                                Point2D(212f, 319f),
                                Point2D(466f, 312f)
                            )
                        )
                    )

                    add(
                        Polygon2D(
                            listOf(
                                Point2D(800f, 300f),
                                Point2D(800f, 0f)
                            )
                        )
                    )

                    add(
                        Polygon2D(
                            listOf(
                                Point2D(0f, 150f),
                                Point2D(500f, 150f)
                            )
                        )
                    )
                }

            }

        core.apply {
            genMatrix(core.h, core.w, core.dens, p)
            us = 1
            vs = 2
            ue = 4
            ve = 13

            doBfs(us, vs, ue, ve)
            if (trackFound){
                track(ve, ue)
                path = path.asReversed()
                var commandString = ""
                for (i in 0 until path.size-1) {
                    commandString += toAction(path[i], path[i+1])
                }
                commandString += "E"
                Timber.d(commandString)
            } else {
                Timber.d("No Track Found")
            }
        }
    }

    private fun fetchUser() {
        uiScope.launch {
            listUser = withContext(Dispatchers.IO) {
                userRepository.getAllUser()
            }
            for (user in listUser) {
                if (user.id == ADMIN_ID) userAdmin = user
                else if (user.id == MY_ID) userMe = user
            }

        }
    }

//    private fun fetchData() {
//        uiScope.launch {
//            _storedMessage.value = withContext(Dispatchers.IO) {
//                messageRepository.getAllStoredMessage(ADMIN_ID, MY_ID)
//            }
//        }
//    }

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
        Helper.mqttPublish(client, topic = "ub/request", payload = "req:$position")
        _hideRequestSheetEvent.value = true

        uiScope.launch {
            _status.value = true
            _storedMessage.value = withContext(Dispatchers.IO) {
                messageRepository.addNewMessageNoDelay(
                    from = userMe,
                    to = userAdmin,
                    content = requestStrings[position],
                    code = position
                )
            }
            messageRepository.postNewMessage(
                from = userMe.id,
                to = userAdmin.id,
                content = requestStrings[position],
                code = position
            )
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
            val result =
                messageRepository.addNewMessage(
                    from = ADMIN_ID,
                    to = MY_ID,
                    content = responseStrings[position],
                    code = position
                )
//            delay(500)
            result
        }

        _navigateResponse.postValue(position)
    }

}
