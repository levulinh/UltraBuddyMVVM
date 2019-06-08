package andrew.studio.com.ultrabuddymvvm.ui.settings

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.data.repository.GroundRepository
import andrew.studio.com.ultrabuddymvvm.internal.Helper
import andrew.studio.com.ultrabuddymvvm.ui.map.UltraMapViewModelFactory
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber
const val MY_ID = "5cc8244ea17725001735abd8"
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener, KodeinAware {
    override val kodein: Kodein by closestKodein()
    private val groundRepository: GroundRepository by instance()

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val pref = findPreference(key)
        if (pref is EditTextPreference)
            if (pref.key == Helper.WIFI_PASSWORD)
                pref.summary = if (pref.text.isNullOrEmpty()) getString(R.string.pref_summary_password) else "********"
            else pref.summary = when (pref.key) {
                Helper.WIFI_SSID -> if (pref.text.isNullOrEmpty()) getString(R.string.pref_summary_ssid) else pref.text
                Helper.GROUND_HEIGHT -> if (pref.text.isNullOrEmpty()) getString(R.string.pref_summary_ground_height) else pref.text
                Helper.GROUND_WIDTH -> if (pref.text.isNullOrEmpty()) getString(R.string.pref_summary_ground_width) else pref.text
                else -> ""
            }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        val wifiSsidPref = findPreference(Helper.WIFI_SSID) as EditTextPreference
        wifiSsidPref.summary = if (wifiSsidPref.text.isNullOrEmpty()) getString(R.string.pref_summary_ssid) else wifiSsidPref.text
        val wifiPasswordPref = findPreference(Helper.WIFI_PASSWORD) as EditTextPreference
        wifiPasswordPref.summary = if (wifiPasswordPref.text.isNullOrEmpty()) getString(R.string.pref_summary_password) else "********"
        val groundWidthPref = findPreference(Helper.GROUND_WIDTH) as EditTextPreference
        groundWidthPref.summary = if (groundWidthPref.text.isNullOrEmpty()) getString(R.string.pref_summary_ground_width) else groundWidthPref.text
        val groundHeightPref = findPreference(Helper.GROUND_HEIGHT) as EditTextPreference
        groundHeightPref.summary = if (groundHeightPref.text.isNullOrEmpty()) getString(R.string.pref_summary_ground_height) else groundHeightPref.text


        val obstaclePref = findPreference(Helper.OBSTACLES)
        obstaclePref.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToObstacleSettingsFragment())
            true
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.title = "Settings"
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = null
        setHasOptionsMenu(true)


    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume()")
        val sharedPreferences = preferenceManager.sharedPreferences
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.obstacle_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                val sharedPreferences = preferenceManager.sharedPreferences
                val width = sharedPreferences.getString(Helper.GROUND_WIDTH, "0").toInt()
                val height = sharedPreferences.getString(Helper.GROUND_HEIGHT, "0").toInt()

                uiScope.launch {
                    groundRepository.updateWidthHeight(userId = MY_ID, width = width, height = height)
                    Toast.makeText(context, "Settings saved!", Toast.LENGTH_LONG).show()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
