package andrew.studio.com.ultrabuddymvvm.ui.settings.obstaclesettings

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.data.entity.Polygon
import andrew.studio.com.ultrabuddymvvm.databinding.ObstacleSettingsFragmentBinding
import andrew.studio.com.ultrabuddymvvm.internal.ScopedFragment
import andrew.studio.com.ultrabuddymvvm.utils.Helper
import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
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

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ObstacleSettingsFragment : ScopedFragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()

    private val viewModelFactory by instance<ObstacleSettingsViewModelFactory>()

    private lateinit var viewModel: ObstacleSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: ObstacleSettingsFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.obstacle_settings_fragment, container, false)

        val adapter = ObstacleAdapter()
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        setHasOptionsMenu(true)

        binding.rvObstacle.apply {
            this.adapter = adapter
            this.layoutManager = linearLayoutManager
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ObstacleSettingsViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.apply {
            currentGroundParams.observe(this@ObstacleSettingsFragment, Observer {
                if (it != null) {
                    val list = it
                    adapter.submitList(list)
                }
            })

            eventClearEditText.observe(this@ObstacleSettingsFragment, Observer {
                binding.textContent.setText("")

                val inputMethodManager =
                    activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    activity!!.currentFocus.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            })
        }

        binding.lifecycleOwner = this
        return binding.root
    }

    private fun saveObstacle() {
        viewModel.saveObstacles()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.obstacle_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> saveObstacle()
        }
        return super.onOptionsItemSelected(item)
    }
}
