package com.xereon.xereon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.databinding.RecyclerLoadStateBinding

class ProductLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<ProductLoadStateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val binding =
            RecyclerLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }


    inner class ViewHolder(private val binding: RecyclerLoadStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonRetry.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            binding.isLoading = loadState is LoadState.Loading
            binding.isError = loadState is LoadState.Error
        }
    }
}