package com.xereon.xereon.ui.chat

import android.os.Bundle
import android.os.Handler
import android.provider.SyncStateContract
import android.util.Log
import android.util.Log.d
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.R
import com.xereon.xereon.adapter.loadStateAdapter.ChatLoadStateAdapter
import com.xereon.xereon.adapter.pagingAdapter.ChatAdapter
import com.xereon.xereon.databinding.FrgChatBinding
import com.xereon.xereon.di.ProvideUserId
import com.xereon.xereon.ui.MainActivity
import com.xereon.xereon.ui.MainActivityCallback
import com.xereon.xereon.ui.product.DefaultProductFragmentArgs
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.frg_chat) {
    private val viewModel: ChatViewModel by viewModels()
    private val args by navArgs<ChatFragmentArgs>()

    private var _binding: FrgChatBinding? = null
    private val binding get() = _binding!!

    private var initialLoad: Boolean = true
    private val chatAdapter = ChatAdapter()

    @JvmField @Inject @ProvideUserId var userId: Int = Constants.DEFAULT_USER_ID

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgChatBinding.bind(view)

        (requireActivity() as MainActivityCallback).setActionBarTitle(args.storeName)

        chatAdapter.addLoadStateListener { loadStates ->
            binding.apply {
                if (loadStates.source.refresh is LoadState.NotLoading) {
                    binding.chatRecycler.scrollToPosition(0)
                    binding.chatLoading.isVisible = false
                }

                chatLoadingInitial.isVisible = loadStates.source.refresh is LoadState.Loading && initialLoad  //initial

                chatError.isVisible = loadStates.source.refresh is LoadState.Error

                //empty view
                chatNoMessages.isVisible = loadStates.source.refresh is LoadState.NotLoading &&
                        loadStates.append.endOfPaginationReached &&
                        chatAdapter.itemCount < 1
            }
        }

        binding.apply {
            chatRecycler.apply {
                setHasFixedSize(true)
                itemAnimator = null
                adapter = chatAdapter.withLoadStateHeader(header = ChatLoadStateAdapter{
                    chatAdapter.retry()
                })
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true)
            }

            chatSend.setOnClickListener {
                val text = chatInput.editText?.text.toString()
                if (text.isNotBlank()) {
                    viewModel.sendMessage(text, userId, args.storeId)
                    chatInput.editText?.setText("")
                }
            }
        }

        view.setOnClickListener {
            Log.d(TAG, "clicked on root")
            binding.chatInput.editText?.clearFocus()
        }

        subscribeToObserver()

        viewModel.getChatMessages(userId, args.storeId)
    }

    private fun subscribeToObserver() {
        viewModel.chatMessages.observe(viewLifecycleOwner, Observer {
            chatAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            initialLoad = false
        })

        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is ChatViewModel.ChatEvent.Load -> {
                    binding.chatLoading.isVisible = true
                }
                is ChatViewModel.ChatEvent.SendSuccess -> {
                    chatAdapter.refresh()
                }
                is ChatViewModel.ChatEvent.SendError -> {
                    binding.chatLoading.isVisible = false
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}