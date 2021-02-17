package com.xereon.xereon.ui.chat

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.data.chat.Chat
import com.xereon.xereon.databinding.FragmentChatOverviewBinding
import com.xereon.xereon.ui.main.MainActivity
import com.xereon.xereon.util.DialogHelper
import com.xereon.xereon.util.ui.doNavigate
import com.xereon.xereon.util.ui.observeLiveData
import com.xereon.xereon.util.ui.showError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatOverviewFragment : Fragment(R.layout.fragment_chat_overview) {
    private val viewModel by viewModels<ChatOverviewFragmentViewModel>()

    private var _binding: FragmentChatOverviewBinding? = null
    private val binding get() = _binding!!

    private val chatsAdapter = ChatsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentChatOverviewBinding.bind(view)

        chatsAdapter.setOnItemClickListener { id, name ->
            doNavigate(ChatOverviewFragmentDirections.actionChatOverviewFragmentToChatFragment(id, name))
        }

        binding.recycler.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatsAdapter
            setHasFixedSize(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            (requireActivity() as MainActivity).goBack()
        }

        viewModel.chats.observeLiveData(this) {
            binding.loading.isVisible = false
            chatsAdapter.submitList(it)
            if (it.isEmpty())
                binding.noMessages.isVisible = true
        }

        viewModel.events.observeLiveData(this) {
            if (it is ChatEvents.ShowNoAccountDialog)
                showNotLoggedInDialog()
        }
        viewModel.exceptions.observeLiveData(this) { showError(it) }

        viewModel.getAllChats()
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