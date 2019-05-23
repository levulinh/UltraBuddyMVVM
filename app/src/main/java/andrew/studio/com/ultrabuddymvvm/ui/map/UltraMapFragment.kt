package andrew.studio.com.ultrabuddymvvm.ui.map

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.data.entity.Point
import andrew.studio.com.ultrabuddymvvm.data.entity.Polygon
import andrew.studio.com.ultrabuddymvvm.databinding.UltraMapFragmentBinding
import andrew.studio.com.ultrabuddymvvm.internal.ScopedFragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class UltraMapFragment : ScopedFragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: UltraMapViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: UltraMapFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.ultra_map_fragment, container, false)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(UltraMapViewModel::class.java)

        viewModel.apply {
            groundParameters.observe(this@UltraMapFragment, Observer {
                if (it != null) {
                    binding.ground.visibility = View.VISIBLE
                    binding.layoutLoading.visibility = View.GONE

                    val obstaclesObject: List<Polygon>
                    obstaclesObject = if (it.obstacles.isEmpty())
                        emptyList()
                    else {
                        val listType = object : TypeToken<List<Polygon>>() {
                        }.type
                        Gson().fromJson(it.obstacles, listType)
                    }
                    binding.ground.apply {
                        groundHeight = it.height
                        groundWidth = it.width
                        obstacles = obstaclesObject
                    }
                }
            })
            robotPositionPoint.observe(this@UltraMapFragment, Observer {
                binding.ground.apply {
                    robotPosition = it
                }
            })
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}
