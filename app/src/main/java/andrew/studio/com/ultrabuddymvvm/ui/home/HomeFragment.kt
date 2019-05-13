package andrew.studio.com.ultrabuddymvvm.ui.home

import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry
import andrew.studio.com.ultrabuddymvvm.databinding.HomeFragmentBinding
import andrew.studio.com.ultrabuddymvvm.internal.ScopedFragment
import andrew.studio.com.ultrabuddymvvm.R
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class HomeFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: HomeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.swipeRequest)

        val adapter = ConversationAdapter()
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        layoutManager.stackFromEnd = true

        binding.recyclerConversation.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            this.setOnScrollChangeListener { _, _, _, _, _ ->
                binding.header.isSelected = this.canScrollVertically(-1)
                if (this.canScrollVertically(1)) {
                    binding.fabScrollDown.show()
                } else binding.fabScrollDown.hide()
            }
        }

        viewModel.apply {
            storedMessage.observe(this@HomeFragment, Observer {
                updateMessageEntries(it, binding, adapter)
            })

            emptyLayoutVisible.observe(this@HomeFragment, Observer {
                if (it == true) {
                    binding.apply {
                        layoutEmpty.visibility = View.VISIBLE
                        recyclerConversation.visibility = View.GONE
                    }
                } else {
                    binding.apply {
                        layoutEmpty.visibility = View.GONE
                        recyclerConversation.visibility = View.VISIBLE
                    }
                }
            })

            showRequestSheetEvent.observe(this@HomeFragment, Observer {
                if (it == true) {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    } else
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            })

            hideRequestSheetEvent.observe(this@HomeFragment, Observer {
                if (it == true) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            })

            navigateToAboutEvent.observe(this@HomeFragment, Observer {
                if (it == true) {
                    findNavController().navigate(R.id.action_homeFragment_to_aboutFragment)
                    viewModel.doneNavigateToAbout()
                }
            })

            scrollDownEvent.observe(this@HomeFragment, Observer {
                if (it == true) {
                    binding.recyclerConversation.smoothScrollToPosition(adapter.itemCount - 1)
                }
            })
        }

        launch {
            val allMessage = viewModel.message.await()
            allMessage.observe(this@HomeFragment, Observer {
                updateMessageEntries(it, binding, adapter)
            })
        }

        return binding.root
    }

    private fun updateMessageEntries(
        it: List<MessageEntry>?,
        binding: HomeFragmentBinding,
        adapter: ConversationAdapter
    ) {
        it ?: return
        if (it.isEmpty()) {
            viewModel.onConversationLoaded(true)
            binding.header.isSelected = false
        } else {
            viewModel.onConversationLoaded(false)
            adapter.submitList(it)
            binding.recyclerConversation.smoothScrollToPosition(it.size - 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, view!!.findNavController())
                || super.onOptionsItemSelected(item)
    }
}
