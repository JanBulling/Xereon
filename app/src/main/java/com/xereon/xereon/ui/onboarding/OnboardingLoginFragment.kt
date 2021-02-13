package com.xereon.xereon.ui.onboarding

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.databinding.FragmentOnboardingLoginBinding
import com.xereon.xereon.ui.onboarding.OnboardingEvents.*
import com.xereon.xereon.util.ui.doNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingLoginFragment : Fragment(R.layout.fragment_onboarding_login) {
    private val viewModel by viewModels<OnboardingLoginFragmentViewModel>()

    private var _binding: FragmentOnboardingLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboardingLoginBinding.bind(view)

        binding.apply {
            onboardingBack.setOnClickListener { viewModel.onBackButtonClick() }
            onboardingForgotPassword.setOnClickListener { viewModel.onForgotPasswordClick() }
            onboardingChooseRegister.setOnClickListener { viewModel.onChooseRegisterClick() }
            onboardingLoginBtn.setOnClickListener {
                val email = onboardingEmailInput.editText?.text.toString()
                val password = onboardingPasswordInput.editText?.text.toString()
                viewModel.onLoginButtonClick(email, password)
            }
        }

        viewModel.loginEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NavigateToForgotPassword ->
                    doNavigate(
                        OnboardingLoginFragmentDirections
                            .actionOnboardingLoginFragmentToOnboardingForgotPasswordFragment()
                    )

                is NavigateToRegister ->
                    doNavigate(
                        OnboardingLoginFragmentDirections
                            .actionOnboardingLoginFragmentToOnboardingRegisterFragment()
                    )

                is Success ->
                    (requireActivity() as OnboardingActivity).completeOnboarding(it.data)

                is Loading ->
                    binding.onboardingLoading.isVisible = true

                is Error -> {
                    binding.onboardingLoading.isVisible = false
                    showError(it.message)
                }

                is NavigateBack ->
                    (requireActivity() as OnboardingActivity).goBack()
            }
        })
    }

    private fun showError(@StringRes messageId: Int) {
        val snackbar = Snackbar.make(requireView(), messageId, Snackbar.LENGTH_LONG);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            snackbar.view.setBackgroundColor(resources.getColor(R.color.type_azure, null))
        snackbar.show()
    }

    override fun onResume() {
        super.onResume()
        binding.onboardingContainer.sendAccessibilityEvent(AccessibilityEvent.TYPE_ANNOUNCEMENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}