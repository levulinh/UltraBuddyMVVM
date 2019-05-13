package andrew.studio.com.ultrabuddymvvm.ui.map

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.databinding.UltraMapFragmentBinding
import androidx.databinding.DataBindingUtil

class UltraMapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: UltraMapFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.ultra_map_fragment, container, false)
        val viewModel = ViewModelProviders.of(this).get(UltraMapViewModel::class.java)

        return binding.root
    }


}
