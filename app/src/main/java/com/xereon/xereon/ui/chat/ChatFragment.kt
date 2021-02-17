package com.xereon.xereon.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.R
import com.xereon.xereon.databinding.FragmentChatBinding
import com.xereon.xereon.ui.main.MainActivity
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.DialogHelper
import com.xereon.xereon.util.ui.observeLiveData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat) {
    private val viewModel by viewModels<ChatFragmentViewModel>()
    private val args by navArgs<ChatFragmentArgs>()

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var initialLoad: Boolean = true
    private val chatAdapter = ChatPagingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentChatBinding.bind(view)

        chatAdapter.addLoadStateListener { loadStates ->
            binding.apply {
                if (loadStates.source.refresh is LoadState.NotLoading) {
                    binding.recycler.scrollToPosition(0)
                    binding.loading.isVisible = false
                }

                chatLoadingInitial.isVisible = loadStates.source.refresh is LoadState.Loading && initialLoad  //initial

                //chatError.isVisible = loadStates.source.refresh is LoadState.Error

                //empty view
                //chatNoMessages.isVisible = loadStates.source.refresh is LoadState.NotLoading &&
                //        loadStates.append.endOfPaginationReached &&
                //        chatAdapter.itemCount < 1
            }
        }

        binding.apply {
            toolbar.title = args.storeName
            toolbar.setNavigationOnClickListener {
                (requireActivity() as MainActivity).goBack()
            }

            recycler.apply {
                setHasFixedSize(true)
                itemAnimator = null
                adapter = chatAdapter.withLoadStateHeaderAndFooter(
                    header = ChatStateAdapter { chatAdapter.retry() },
                    footer = ChatStateAdapter { chatAdapter.retry() }
                )
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true)
            }

            send.setOnClickListener {
                val text = input.editText?.text.toString()
                if (text.isNotBlank()) {
                    viewModel.sendMessage(text, args.storeId)
                    input.editText?.setText("")
                }
            }
        }

        view.setOnClickListener {
            binding.input.editText?.clearFocus()
        }


        viewModel.chatMessages.observeLiveData(this) {
            chatAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        viewModel.events.observeLiveData(this) {
            if (it is ChatEvents.ShowNoAccountDialog)
                showNotLoggedInDialog()
        }

        viewModel.getMessages(args.storeId)
    }

    private fun showNotLoggedInDialog() {
        val dialog = DialogHelper.DialogInstance(
            context = requireContext(),
            title = "Nicht angemeldet",
            message = "Sie müssen bei Xereon angemeldet sein, um Filialen über die integrierte " +
                    "Chat-Funktion der App kontaktieren zu können.",
            negativeButton = "Abbrechen",
            negativeButtonFunction = {(requireActivity() as MainActivity).goBack() },
            positiveButton = "Anmelden",
            cancelable = false
        )
        DialogHelper.showDialog(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}