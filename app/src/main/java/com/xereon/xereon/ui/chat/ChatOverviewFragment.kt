package com.xereon.xereon.ui.chat

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.pagingAdapter.ChatAdapter
import com.xereon.xereon.adapter.recyclerAdapter.ChatsAdapter
import com.xereon.xereon.data.model.Chat
import com.xereon.xereon.databinding.FrgChatOverviewBinding
import com.xereon.xereon.di.InjectApplicationState
import com.xereon.xereon.di.InjectUserId
import com.xereon.xereon.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatOverviewFragment : Fragment(R.layout.frg_chat_overview) {
    private val viewModel: ChatViewModel by viewModels()

    private var _binding: FrgChatOverviewBinding? = null
    private val binding get() = _binding!!

    private val chatsAdapter = ChatsAdapter()

    @JvmField @Inject @InjectUserId var userId: Int = Constants.DEFAULT_USER_ID
    @Inject @InjectApplicationState lateinit var applicationState: Constants.ApplicationState


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgChatOverviewBinding.bind(view)

        chatsAdapter.setOnItemClickListener(object: ChatsAdapter.ItemClickListener{
            override fun onItemClick(chat: Chat) {
                val action = ChatOverviewFragmentDirections.actionChatOverviewToChat(chat.storeID ?: -1, chat.storeName ?: "Store")
                findNavController().navigate(action)
            }
    })

        binding.chatOverviewRecycler.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatsAdapter
            setHasFixedSize(true)
        }

        subscribeToObserver()

        viewModel.getAllChats(userId)

        if (applicationState == Constants.ApplicationState.SKIPPED_HAS_LOCATION ||
            applicationState == Constants.ApplicationState.SKIPPED_HAS_LOCATION ||
            applicationState == Constants.ApplicationState.FIRST_OPENED ||
            userId == Constants.DEFAULT_USER_ID) {

            showNotLoggedInDialog()
        }
    }

    private fun subscribeToObserver() {
        viewModel.chatsData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is ChatViewModel.ChatOverviewEvent.Success -> {
                    if (it.data.isEmpty()) {
                        binding.chatOverviewNoMessages.isVisible = true
                    } else {
                        binding.chatOverviewNoMessages.isVisible = false
                        chatsAdapter.submitList(it.data)
                    }

                    binding.chatOverviewLoading.isVisible = false
                }
                is ChatViewModel.ChatOverviewEvent.Error -> {
                    binding.chatOverviewLoading.isVisible = false
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
                Toast.makeText(requireContext(), "Amelden wird durchgeführt", Toast.LENGTH_SHORT)
                    .show()
                requireActivity().onBackPressed()
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}