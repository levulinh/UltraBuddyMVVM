package andrew.studio.com.ultrabuddymvvm.ui.home

import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry
import andrew.studio.com.ultrabuddymvvm.databinding.HomeFragmentBinding
import andrew.studio.com.ultrabuddymvvm.internal.ScopedFragment
import andrew.studio.com.ultrabuddymvvm.R
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
//            storedMessage.observe(this@HomeFragment, Observer {
//                updateMessageEntries(it, binding, adapter)
//            })

            emptyLayoutVisible.observe(this@HomeFragment, Observer {
                if (it == true) {

                    binding.apply {
                        fabScrollDown.hide()
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

            navigateResponse.observe(this@HomeFragment, Observer {
                val responseArray = resources.getStringArray(R.array.response_strings)
                when (it) {
                    0 -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUltraMapFragment())
                        Toast.makeText(context, responseArray[0] , Toast.LENGTH_SHORT).show()
                        viewModel.doneNavigate()
                    }
                    3 -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAboutFragment())
                        Toast.makeText(context, responseArray[3] , Toast.LENGTH_SHORT).show()
                        viewModel.doneNavigate()
                    }
                }
            })

            scrollDownEvent.observe(this@HomeFragment, Observer {
                if (it == true) {
                    binding.recyclerConversation.smoothScrollToPosition(adapter.itemCount - 1)
                }
            })
        }

        launch {
            binding.shimmerLoading.startShimmer()
            val allMessage = viewModel.message.await()
            allMessage.observe(this@HomeFragment, Observer {
                binding.shimmerLoading.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
                updateMessageEntries(it, binding, adapter)
            })
        }

        return binding.root
    }

    private fun updateMessageEntries(
        messages: List<MessageEntry>?,
        binding: HomeFragmentBinding,
        adapter: ConversationAdapter
    ) {
        messages ?: return
        if (messages.isEmpty()) {
            viewModel.onConversationLoaded(true)
            binding.header.isSelected = false
        } else {
            viewModel.onConversationLoaded(false)
            adapter.submitList(messages)
            binding.recyclerConversation.smoothScrollToPosition(messages.size - 1)
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
