package andrew.studio.com.ultrabuddymvvm.ui.about

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import andrew.studio.com.ultrabuddymvvm.R
import andrew.studio.com.ultrabuddymvvm.UltraBuddyApplication
import andrew.studio.com.ultrabuddymvvm.databinding.AboutFragmentBinding
import andrew.studio.com.ultrabuddymvvm.internal.glide.GlideApp
import android.content.Intent
import android.net.Uri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar

class AboutFragment : Fragment() {

    private lateinit var viewModel: AboutViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: AboutFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.about_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(AboutViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.showSnackbarEvent.observe(this, Observer {
            if (it == true) {
                Snackbar.make(binding.layoutContent, "Sending email to Linh...", Snackbar.LENGTH_LONG).show()
                viewModel.doneShowSnackbarEvent()
            }
        })

        viewModel.sendEmailEvent.observe(this, Observer {
            if (it == true) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:") // only email apps should handle this
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("levulinhbk@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Hello From Ultra Buddy App")
                }
                if (intent.resolveActivity(context!!.packageManager) != null) {
                    startActivity(intent)
                }
            }
        })

        GlideApp.with(this)
            .load(R.drawable.image_my_avatar)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.imageAvatar)
        return binding.root


    }

}
