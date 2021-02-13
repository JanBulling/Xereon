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
import com.xereon.xereon.databinding.FragmentOnboardingForgotPasswordBinding
import com.xereon.xereon.util.ui.doNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingForgotPasswordFragment : Fragment(R.layout.fragment_onboarding_forgot_password) {
    private val viewModel by viewModels<OnboardingForgotPasswordViewModel>()

    private var _binding: FragmentOnboardingForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboardingForgotPasswordBinding.bind(view)

        binding.apply {
            onboardingBack.setOnClickListener { viewModel.onBackButtonClick() }
            onboardingResetPasswordBtn.setOnClickListener {
                val email = onboardingEmailInput.editText?.text.toString()
                viewModel.onForgotPasswordClick(email)
            }
        }

        viewModel.loginEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                is OnboardingEvents.Success -> {
                    binding.onboardingLoading.isVisible = false
                    showSuccessfulMessage()
                }

                is OnboardingEvents.Loading ->
                    binding.onboardingLoading.isVisible = true

                is OnboardingEvents.Error -> {
                    binding.onboardingLoading.isVisible = false
                    showError(it.message)
                }

                is OnboardingEvents.NavigateBack ->
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

    private fun showSuccessfulMessage() {
        val snackbar = Snackbar.make(requireView(), "Zurücksetzen erflogreich. Überprüfen Sie Ihre Emails.", Snackbar.LENGTH_LONG);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            snackbar.view.setBackgroundColor(resources.getColor(R.color.type_green, null))
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