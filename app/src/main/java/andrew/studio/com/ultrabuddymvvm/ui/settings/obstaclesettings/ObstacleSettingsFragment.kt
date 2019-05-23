package andrew.studio.com.ultrabuddymvvm.ui.settings.obstaclesettings

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.databinding.ObstacleSettingsFragmentBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

class ObstacleSettingsFragment : Fragment() {

    private lateinit var viewModel: ObstacleSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: ObstacleSettingsFragmentBinding
                = DataBindingUtil.inflate(inflater, R.layout.obstacle_settings_fragment, container, false)
        return binding.root
    }

}
