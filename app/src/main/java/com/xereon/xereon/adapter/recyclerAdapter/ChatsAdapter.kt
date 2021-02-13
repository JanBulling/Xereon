package com.xereon.xereon.adapter.recyclerAdapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.data.model.Chat
import com.xereon.xereon.databinding.RecyclerChatOverviewBinding
import com.xereon.xereon.databinding.RecyclerOrderStoreBinding
import com.xereon.xereon.db.StoreBasic
import java.util.*

class ChatsAdapter : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    private lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RecyclerChatOverviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = differ.currentList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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
    fun setOnItemClickListener(clickListener: ItemClickListener) {
        itemClickListener = clickListener
    }

    interface ItemClickListener {
        fun onItemClick(chat: Chat)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(
        val binding: RecyclerChatOverviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (::itemClickListener.isInitialized) {
                    val currentIndex = bindingAdapterPosition
                    if (currentIndex != RecyclerView.NO_POSITION) {
                        val chat = differ.currentList[currentIndex]
                        if (chat != null)
                            itemClickListener.onItemClick(chat)
                    }
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