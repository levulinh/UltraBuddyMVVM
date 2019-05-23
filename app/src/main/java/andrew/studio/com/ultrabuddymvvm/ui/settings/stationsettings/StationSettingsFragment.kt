package andrew.studio.com.ultrabuddymvvm.ui.settings.stationsettings

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.databinding.StationSettingsFragmentBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

class StationSettingsFragment : Fragment() {

    private lateinit var viewModel: StationSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: StationSettingsFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.station_settings_fragment, container, false)
        return binding.root
    }

}
