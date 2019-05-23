package andrew.studio.com.ultrabuddymvvm.ui.settings.obstaclesettings

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.data.entity.Polygon
import andrew.studio.com.ultrabuddymvvm.databinding.ObstacleSettingsFragmentBinding
import andrew.studio.com.ultrabuddymvvm.internal.ScopedFragment
import andrew.studio.com.ultrabuddymvvm.utils.Helper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber

class ObstacleSettingsFragment : ScopedFragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()

    private val viewModelFactory by instance<ObstacleSettingsViewModelFactory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: ObstacleSettingsFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.obstacle_settings_fragment, container, false)

        val adapter = ObstacleAdapter()
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        binding.rvObstacle.apply {
            this.adapter = adapter
            this.layoutManager = linearLayoutManager
        }

        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(ObstacleSettingsViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.apply {
            viewModel.currentGroundParams.observe(this@ObstacleSettingsFragment, Observer {
                if (it != null)
                {
                    val list = Helper.gsonToList<Polygon>(it.obstacles)
                    adapter.submitList(list)
                }
            })

            viewModel.newObstacle.observe(this@ObstacleSettingsFragment, Observer {
                Timber.d(it.points.toString())
            })
        }

        binding.lifecycleOwner = this
        return binding.root
    }

}
