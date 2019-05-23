package andrew.studio.com.ultrabuddymvvm.ui.settings.obstaclesettings

import andrew.studio.com.ultrabuddymvvm.data.entity.Polygon
import andrew.studio.com.ultrabuddymvvm.databinding.ItemObstacleEditBinding
import andrew.studio.com.ultrabuddymvvm.utils.Helper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ObstacleAdapter: ListAdapter<Polygon, RecyclerView.ViewHolder>(PolygonDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ObstacleViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ObstacleViewHolder).bind(getItem(position))
    }

    class ObstacleViewHolder private constructor(private val binding: ItemObstacleEditBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Polygon) {
            binding.textContent.setText(Helper.polygonToString(item))
            binding.buttonDelete.setOnClickListener {
                if (binding.textContent.text.isNullOrEmpty()) {
                    binding.buttonDelete.text = "Del"
                    binding.textContent.setText(Helper.objectToString(item))
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