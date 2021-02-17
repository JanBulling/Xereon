package com.xereon.xereon.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.data.chat.Chat
import com.xereon.xereon.databinding.RecyclerChatOverviewBinding

class ChatsAdapter : RecyclerView.Adapter<ChatsAdapter.VH>() {
    private lateinit var onClickAction: (Int, String) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            RecyclerChatOverviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount() = differ.currentList.size


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(differ.currentList[position])
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private val diffCallback = object : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat) =
            oldItem.storeID == newItem.storeID

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat) =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Chat>) {
        differ.submitList(list)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(action: (Int, String) -> Unit) {
        onClickAction = action
    }

    interface ItemClickListener {
        fun onItemClick(chat: Chat)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class VH(val binding: RecyclerChatOverviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (::onClickAction.isInitialized) {
                    val index = bindingAdapterPosition
                    if (index == RecyclerView.NO_POSITION) return@setOnClickListener
                    val item = differ.currentList[index] ?: return@setOnClickListener
                    onClickAction.invoke(item.storeID, item.storeName)
                }
            }
        }

        fun bind(chat: Chat) {
            binding.apply {
                recyclerChatOverviewLogo.clipToOutline = true
                Glide.with(recyclerChatOverviewLogo.context).load(chat.logoImageURL)
                    .into(recyclerChatOverviewLogo)
                recyclerChatOverviewName.text = chat.storeName
                recyclerChatOverviewNumberMessages.isVisible = chat.unreadMessages > 0
                recyclerChatOverviewNumberMessages.text = chat.unreadMessages.toString()
                recyclerChatOverviewTimeLastMessage.text = chat.timeFromLastMessage
            }
        }

    }
}