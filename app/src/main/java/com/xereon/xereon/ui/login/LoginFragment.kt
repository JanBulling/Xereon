package com.xereon.xereon.ui.login

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.databinding.FrgLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.frg_login) {

    private val viewModel by viewModels<LoginViewModel>()

    private var _binding: FrgLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgLoginBinding.bind(view)

        binding.apply {
            loginBtn.setOnClickListener {
                val emailText = loginEmailInput.editText?.text.toString()
                val pwdText = loginPasswordInput.editText?.text.toString()
                viewModel.performLogin(emailText, pwdText)
            }

            loginForgotPassword.setOnClickListener {

            }

            loginNoAccount.setOnClickListener {

            }
        }

        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.loginData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is LoginViewModel.LoginEvent.Loading ->
                    binding.loginLoading.isVisible = true
                else ->
                    binding.loginLoading.isVisible = false
            }
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect {
                when (it) {
                    is LoginViewModel.LoginEvent.LoginError -> showError(it.messageId)
                }
            }
        }
    }

    private fun showError(@StringRes messageId: Int) {
        val snackbar = Snackbar.make(requireView(), messageId, Snackbar.LENGTH_LONG);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            snackbar.view.setBackgroundColor(resources.getColor(R.color.error, null))
        snackbar.show()
    }

}