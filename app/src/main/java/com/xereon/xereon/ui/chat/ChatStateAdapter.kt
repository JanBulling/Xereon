package com.xereon.xereon.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.databinding.SearchStateAdapterBinding

class ChatStateAdapter(private val retry: () -> Unit):
    LoadStateAdapter<ChatStateAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): VH {
        val binding = SearchStateAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class VH(private val binding: SearchStateAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.stateRetry.setOnClickListener {
                retry.invoke()
                binding.stateLoading.isVisible = true
                binding.stateError.isVisible = false
                binding.stateRetry.isVisible = false
            }
        }

        fun bind(loadState: LoadState) {
            val isError = loadState is LoadState.Error
            binding.apply {
                stateError.isVisible = isError
                stateRetry.isVisible = isError
                stateLoading.isVisible = loadState is LoadState.Loading
            }
        }
    }
}