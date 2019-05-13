package andrew.studio.com.ultrabuddymvvm.ui

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.UltraBuddyApplication
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoTitle)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val globalState = applicationContext as UltraBuddyApplication
        val client = globalState.client
        val options = globalState.options
        connect(client, options)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    private fun connect(client: MqttAndroidClient, options: MqttConnectOptions) {
        val token = client.connect(options)
        token.actionCallback = object : IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Toast.makeText(this@MainActivity, "MQTT Connected", Toast.LENGTH_SHORT).show()
                mqttSubscribe(client, "ub/response")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Toast.makeText(this@MainActivity, "MQTT Connect fail", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun mqttSubscribe(client: MqttAndroidClient, topic: String) {
        val qos = 1
        val token = client.subscribe(topic, qos)
        token.actionCallback = object : IMqttActionListener {
            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.i("MQTT:SUBSCRIBE", "FAILED")
            }

            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.i("MQTT:SUBSCRIBE", "SUCCESS")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }
}