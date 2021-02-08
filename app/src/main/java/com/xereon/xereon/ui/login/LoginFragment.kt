package com.xereon.xereon.ui.login

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.databinding.FrgLoginBinding
import com.xereon.xereon.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.frg_login) {
    private val viewModel by viewModels<LoginViewModel>()

    private var _binding: FrgLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgLoginBinding.bind(view)

        binding.apply {
            loginBtn.setOnClickListener {
                val emailText = loginEmailInput.editText?.text.toString()
                val pwdText = loginPasswordInput.editText?.text.toString()
                viewModel.performLogin(emailText, pwdText)
            }

            loginForgotPassword.setOnClickListener {
                Toast.makeText(requireContext(), "Forgot Password", Toast.LENGTH_SHORT).show()
                //findNavController().navigate(R.id.action_Login_to_ForgotPassword)
            }

            loginNoAccount.setOnClickListener {
                findNavController().navigate(R.id.action_Login_to_SignUp)
            }

            back.setOnClickListener { requireActivity().onBackPressed() }
        }

        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.loginData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is LoginViewModel.LoginEvent.Success -> {
                    binding.loginLoading.isVisible = false

                    if(it.loginData.isVerified)
                        preferences.edit().putInt(
                            Constants.PREF_APPLICATION_STATE,
                            Constants.ApplicationState.LOGGED_IN_NO_LOCATION_VALID.index
                        ).apply()
                    else
                        preferences.edit().putInt(
                            Constants.PREF_APPLICATION_STATE,
                            Constants.ApplicationState.LOGGED_IN_NO_LOCATION_NOT_VALID.index
                        ).apply()

                    findNavController().navigate(R.id.action_Login_to_ChooseLocation)
                }
                is LoginViewModel.LoginEvent.Loading ->
                    binding.loginLoading.isVisible = true
                is LoginViewModel.LoginEvent.LoginError ->
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
            snackbar.view.setBackgroundColor(resources.getColor(R.color.type_azure, null))
        snackbar.show()
    }

}