package com.xereon.xereon.adapter.loadStateAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView

/*class ChatLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<ChatLoadStateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        /*val binding =
            ProductLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)*/
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) =
        holder.bind(loadState)


    inner class ViewHolder(private val binding: ProductLoadStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonRetry.setOnClickListener {
                retry.invoke()
                binding.isLoading = true
                binding.isError = false
            }
        }

        fun bind(loadState: LoadState) {
            binding.isLoading = loadState is LoadState.Loading
            binding.isError = loadState is LoadState.Error
        }
    }
}*/