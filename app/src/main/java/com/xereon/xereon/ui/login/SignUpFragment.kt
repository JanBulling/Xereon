package com.xereon.xereon.ui.login

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.databinding.FrgSignUpBinding
import com.xereon.xereon.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.frg_sign_up) {
    private val viewModel by viewModels<LoginViewModel>()

    private var _binding: FrgSignUpBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgSignUpBinding.bind(view)

        binding.apply {
            signupBtn.setOnClickListener {
                val gender =
                    if (signupGenderInput.checkedRadioButtonId == R.id.signup_gender_male)
                        getString(R.string.gender_male)
                    else getString(R.string.gender_female)

                val userName = signupNameInput.editText?.text.toString()
                val emailText = signupEmailInput.editText?.text.toString()
                val pwdText = signupPasswordInput.editText?.text.toString()

                val nameComplete = "$gender $userName"
                viewModel.performSignUp(nameComplete, emailText, pwdText)
            }

            signupAlreadyAccount.setOnClickListener {
                findNavController().navigate(R.id.action_SignUp_to_Login)
            }

            back.setOnClickListener { requireActivity().onBackPressed() }
        }

        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.loginData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoginViewModel.LoginEvent.Success -> {
                    binding.signupLoading.isVisible = false

                    preferences.edit().putInt(
                        Constants.PREF_APPLICATION_STATE,
                        Constants.ApplicationState.LOGGED_IN_NO_LOCATION_NOT_VALID.index
                    ).apply()

                    findNavController().navigate(R.id.action_SignUp_to_ChooseLocation)
                }
                is LoginViewModel.LoginEvent.Loading ->
                    binding.signupLoading.isVisible = true
                is LoginViewModel.LoginEvent.LoginError ->
                    binding.signupLoading.isVisible = false
            }
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect {
                if (it is LoginViewModel.LoginEvent.LoginError)
                    showError(it.messageId)
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