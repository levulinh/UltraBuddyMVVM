package andrew.studio.com.ultrabuddymvvm

import andrew.studio.com.ultrabuddymvvm.corealgorithms.Core
import andrew.studio.com.ultrabuddymvvm.corealgorithms.CoreImpl
import andrew.studio.com.ultrabuddymvvm.data.db.MessageDatabase
import andrew.studio.com.ultrabuddymvvm.data.network.*
import andrew.studio.com.ultrabuddymvvm.data.network.datasource.*
import andrew.studio.com.ultrabuddymvvm.data.repository.*
import andrew.studio.com.ultrabuddymvvm.ui.home.HomeViewModelFactory
import andrew.studio.com.ultrabuddymvvm.ui.map.UltraMapViewModelFactory
import android.app.Application
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import timber.log.Timber

const val SERVER = "tcp://m15.cloudmqtt.com"
const val PORT = 16051
const val USERNAME = "ajvnasnj"
const val PASSWORD = "dzuidzA-nbdy"

class UltraBuddyApplication : Application(), KodeinAware {
    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@UltraBuddyApplication))

        bind() from singleton { MessageDatabase(instance()) }
        bind() from singleton { instance<MessageDatabase>().messageDao }
        bind() from singleton { instance<MessageDatabase>().groundDao }
        bind() from singleton { instance<MessageDatabase>().userDao }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { UltraBuddyApiService(instance()) }
        bind<MessageDataSource>() with singleton {
            MessageDataSourceImpl(
                instance()
            )
        }
        bind<MessageRepository>() with singleton { MessageRepositoryImpl(instance(), instance(), instance()) }
        bind<UserDataSource>() with singleton {
            UserDataSourceImpl(
                instance()
            )
        }
        bind<GroundDataSource>() with singleton { GroundDataSourceImpl(instance()) }
        bind<GroundRepository>() with singleton { GroundRepositoryImpl(instance(), instance()) }
        bind<UserRepository>() with singleton { UserRepositoryImpl(instance(), instance()) }
        bind<Core>() with singleton { CoreImpl(instance()) }
        bind() from provider { HomeViewModelFactory(instance(), instance(), instance(), instance()) }
        bind() from provider { UltraMapViewModelFactory(instance(), instance()) }
    }

    lateinit var client: MqttAndroidClient
    lateinit var options: MqttConnectOptions

    override fun onCreate() {
        super.onCreate()
        if (!BuildConfig.DEBUG) {
            Timber.plant(NoLoggingTree())
        } else
            Timber.plant(Timber.DebugTree())

        val clientId = MqttClient.generateClientId()
        options = MqttConnectOptions().also {
            it.userName = USERNAME
            it.password = PASSWORD.toCharArray()
            it.isAutomaticReconnect = true
        }
        client = MqttAndroidClient(this.applicationContext, "$SERVER:$PORT", clientId)
    }

    class NoLoggingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            // Do nothing
        }

    }
}