package andrew.studio.com.ultrabuddymvvm

import andrew.studio.com.ultrabuddymvvm.data.db.MessageDatabase
import andrew.studio.com.ultrabuddymvvm.data.network.*
import andrew.studio.com.ultrabuddymvvm.data.repository.MessageRepository
import andrew.studio.com.ultrabuddymvvm.data.repository.MessageRepositoryImpl
import andrew.studio.com.ultrabuddymvvm.ui.home.HomeViewModelFactory
import android.app.Application
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

const val SERVER = "tcp://m15.cloudmqtt.com"
const val PORT = 16051
const val USERNAME = "ajvnasnj"
const val PASSWORD = "dzuidzA-nbdy"

class UltraBuddyApplication: Application(), KodeinAware{
    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@UltraBuddyApplication))

        bind() from singleton { MessageDatabase(instance()) }
        bind() from singleton { instance<MessageDatabase>().messageDao }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { UltraBuddyApiService(instance()) }
        bind<MessageDataSource>() with singleton { MessageDataSourceImpl(instance()) }
        bind<MessageRepository>() with singleton { MessageRepositoryImpl(instance(), instance()) }
        bind() from provider { HomeViewModelFactory(instance(), instance()) }
    }

    lateinit var client: MqttAndroidClient
    lateinit var options: MqttConnectOptions

    override fun onCreate() {
        super.onCreate()
        val clientId = MqttClient.generateClientId()
        options = MqttConnectOptions().also {
            it.userName = USERNAME
            it.password = PASSWORD.toCharArray()
            it.isAutomaticReconnect = true
        }
        client = MqttAndroidClient(this.applicationContext, "$SERVER:$PORT", clientId)
    }
}