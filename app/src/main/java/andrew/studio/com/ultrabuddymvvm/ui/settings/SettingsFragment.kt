package andrew.studio.com.ultrabuddymvvm.ui.settings

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.internal.Helper
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
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
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume()")
        val sharedPreferences = preferenceManager.sharedPreferences
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }
}
