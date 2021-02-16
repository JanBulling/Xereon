package com.xereon.xereon.ui.chat

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.R
import com.xereon.xereon.adapter.pagingAdapter.ChatAdapter
import com.xereon.xereon.databinding.FrgChatBinding
import com.xereon.xereon.util.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.frg_chat) {
    private val viewModel: ChatViewModel by viewModels()
    //private val args by navArgs<ChatFragmentArgs>()

    private var _binding: FrgChatBinding? = null
    private val binding get() = _binding!!

    private var initialLoad: Boolean = true
    private val chatAdapter = ChatAdapter()

    //@JvmField @Inject @InjectUserId var userId: Int = Constants.DEFAULT_USER_ID
    //@Inject @InjectApplicationState lateinit var applicationState: Constants.ApplicationState

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgChatBinding.bind(view)

        //(requireActivity() as MainActivityCallback).setActionBarTitle(args.storeName)

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
                adapter = chatAdapter/*.withLoadStateHeader(header = ChatLoadStateAdapter{
                    chatAdapter.retry()
                })*/
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true)
            }

            chatSend.setOnClickListener {
                val text = chatInput.editText?.text.toString()
                if (text.isNotBlank()) {
                    viewModel.sendMessage(text, 1, 1)//userId, args.storeId)
                    chatInput.editText?.setText("")
                }
            }
        }

        view.setOnClickListener {
            Log.d(TAG, "clicked on root")
            binding.chatInput.editText?.clearFocus()
        }

        subscribeToObserver()

        //Log.d(TAG, "ApplicationState: $applicationState")
        //if (applicationState == Constants.ApplicationState.SKIPPED_HAS_LOCATION ||
        //    applicationState == Constants.ApplicationState.SKIPPED_HAS_LOCATION ||
        //    applicationState == Constants.ApplicationState.FIRST_OPENED) {

        //    showNotLoggedInDialog()
        //}

        viewModel.getChatMessages(1, 1)//userId, args.storeId)
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

    private fun showNotLoggedInDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Nicht angemeldet")
            .setMessage("Sie müssen bei Xereon angemeldet sein, um Filialen über die integrierte " +
                    "Chat-Funktion der App kontaktieren zu können.")
            .setNegativeButton("Abbrechen") { _, _ ->
                requireActivity().onBackPressed()
            }
            .setNeutralButton("Anmelden") { _, _ ->
                requireActivity().onBackPressed()
                Toast.makeText(requireContext(), "Amelden wird durchgeführt", Toast.LENGTH_SHORT)
                    .show()
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}