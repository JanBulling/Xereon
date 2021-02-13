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
import com.xereon.xereon.databinding.FragmentOnboardingRegisterBinding
import com.xereon.xereon.util.ui.doNavigate
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Error

@AndroidEntryPoint
class OnboardingRegisterFragment : Fragment(R.layout.fragment_onboarding_register) {

    private val viewModel by viewModels<OnboardingRegisterFragmentViewModel>()

    private var _binding: FragmentOnboardingRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboardingRegisterBinding.bind(view)

        binding.apply {
            onboardingBack.setOnClickListener { viewModel.onBackClick() }
            onboardingChooseLogin.setOnClickListener { viewModel.onChooseLoginClick() }
            onboardingRegisterBtn.setOnClickListener {
                val selectedGender =
                    if (onboardingGenderInput.checkedRadioButtonId == R.id.gender_male)
                        getString(R.string.gender_male)
                    else
                        getString(R.string.gender_female)

                val name = "$selectedGender ${onboardingNameInput.editText?.text}"
                val email = onboardingEmailInput.editText?.text.toString()
                val password = onboardingPasswordInput.editText?.text.toString()
                viewModel.onRegisterClick(name, email, password)
            }
        }
        viewModel.loginEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                is OnboardingEvents.NavigateToLogin ->
                    doNavigate(
                        OnboardingRegisterFragmentDirections
                            .actionOnboardingRegisterFragmentToOnboardingLoginFragment()
                    )

                is OnboardingEvents.Success ->
                    (requireActivity() as OnboardingActivity).completeOnboarding(it.data)

                is OnboardingEvents.Loading ->
                    binding.onboardingLoading.isVisible = true

                is OnboardingEvents.Error -> {
                    binding.onboardingLoading.isVisible = true
                    showError(it.message)
                }

                is OnboardingEvents.NavigateBack ->
                    (requireActivity() as OnboardingActivity).goBack()

                is OnboardingEvents.NavigateToLocation ->
                    (requireActivity() as OnboardingActivity).completeOnboarding()
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