package andrew.studio.com.ultrabuddymvvm.ui.settings.obstaclesettings

import andrew.studio.com.ultrabuddymvvm.data.entity.Polygon
import andrew.studio.com.ultrabuddymvvm.databinding.ItemObstacleEditBinding
import andrew.studio.com.ultrabuddymvvm.utils.Helper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class ObstacleAdapter: ListAdapter<Polygon, RecyclerView.ViewHolder>(PolygonDiffCallback()) {
    private var mCurrentList: MutableList<String> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mCurrentList.clear()
        for (polygon in currentList) {
            mCurrentList.add(Helper.polygonToString(polygon))
        }
        return ObstacleViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ObstacleViewHolder).bind(getItem(position), mCurrentList, position)
    }

    fun getMyCurrentList(): MutableList<String>{
        return mCurrentList
    }

    class ObstacleViewHolder private constructor(private val binding: ItemObstacleEditBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Polygon, list: MutableList<String>, position: Int) {
            binding.textContent.setText(Helper.polygonToString(item))
            binding.textContent.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    list[position] = s.toString()
                }
            })
            binding.buttonDelete.setOnClickListener {
                if (binding.textContent.text.isNullOrEmpty()) {
                    binding.buttonDelete.text = "Del"
                    binding.textContent.setText(Helper.polygonToString(item))
                } else {
                    binding.buttonDelete.text = "Undo"
                    binding.textContent.setText("")
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ObstacleViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemObstacleEditBinding.inflate(layoutInflater, parent, false)
                return ObstacleViewHolder(binding)
            }
        }
    }
}

class PolygonDiffCallback: DiffUtil.ItemCallback<Polygon>() {

    override fun areItemsTheSame(oldItem: Polygon, newItem: Polygon): Boolean {
        return oldItem.points == newItem.points
    }

    override fun areContentsTheSame(oldItem: Polygon, newItem: Polygon): Boolean {
        return oldItem.points == newItem.points
    }

}