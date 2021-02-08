package com.xereon.xereon.adapter.pagingAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.R
import com.xereon.xereon.data.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter() :
    PagingDataAdapter<ChatMessage, ChatAdapter.ViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            VIEW_TYPE_FROM_STORE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_chat_message_left, parent, false)
                ViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_chat_message_right, parent, false)
                ViewHolder(view)
            }
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatMessage = getItem(position)

        if (chatMessage != null)
            holder.bind(chatMessage)
    }

    override fun getItemViewType(position: Int) =
        if (getItem(position)?.fromAppUser == true)
            VIEW_TYPE_FROM_USER
        else
            VIEW_TYPE_FROM_STORE


    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        fun bind(message: ChatMessage) {
            val text = view.findViewById<TextView>(R.id.chat_message_text)
            val date = view.findViewById<TextView>(R.id.chat_message_date)

            text.text = message.message
            val dateFromLong = Date(message.date * 1000L)
            val sdf = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
            date.text = sdf.format(dateFromLong)
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
                oldItem.hashCode() == newItem.hashCode()
        }

        const val VIEW_TYPE_FROM_STORE = 0
        const val VIEW_TYPE_FROM_USER = 1
    }
}