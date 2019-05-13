package andrew.studio.com.ultrabuddymvvm.ui.home

import andrew.studio.com.ultrabuddymvvm.data.entity.MessageEntry
import andrew.studio.com.ultrabuddymvvm.databinding.ItemChatBubbleBinding
import andrew.studio.com.ultrabuddymvvm.databinding.ItemMyChatBubbleBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

const val DEFAULT_ADMIN_NAME = "admin"
const val ITEM_ADMIN_MESSAGE = 0
const val ITEM_MY_MESSAGE = 1

class ConversationAdapter : ListAdapter<MessageEntry, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_ADMIN_MESSAGE -> AdminViewHolder.from(parent)
            ITEM_MY_MESSAGE -> MyViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val previousItem = if (position == 0) null else getItem(position - 1)
        when (holder) {
            is AdminViewHolder -> {
                val item = getItem(position)
                holder.bind(item, previousItem)
            }
            is MyViewHolder -> {
                val item = getItem(position)
                holder.bind(item, previousItem)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.from.userName == DEFAULT_ADMIN_NAME) ITEM_ADMIN_MESSAGE
        else ITEM_MY_MESSAGE
    }

    class AdminViewHolder private constructor(private val binding: ItemChatBubbleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageEntry, previousItem: MessageEntry?) {

            binding.imageAvatar.visibility =
                if (previousItem == null || previousItem.from.userName != "admin") View.VISIBLE
                else View.INVISIBLE

            if (previousItem != null)
                if (item.from.id != previousItem.from.id) binding.extraSpace.visibility = View.VISIBLE
                else binding.extraSpace.visibility = View.GONE

            binding.message = item
        }

        companion object {
            fun from(parent: ViewGroup): AdminViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemChatBubbleBinding.inflate(layoutInflater, parent, false)
                return AdminViewHolder(binding)
            }
        }
    }

    class MyViewHolder private constructor(private val binding: ItemMyChatBubbleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageEntry, previousItem: MessageEntry?) {
            binding.message = item
            if (previousItem != null)
                if (item.from.id != previousItem.from.id) binding.extraSpace.visibility = View.VISIBLE
                else binding.extraSpace.visibility = View.GONE
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMyChatBubbleBinding.inflate(layoutInflater, parent, false)

                return MyViewHolder(binding)
            }
        }
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<MessageEntry>() {
    override fun areItemsTheSame(oldItem: MessageEntry, newItem: MessageEntry): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MessageEntry, newItem: MessageEntry): Boolean {
        return oldItem == newItem
    }

}




