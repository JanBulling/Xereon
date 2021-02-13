package com.xereon.xereon.ui.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.xereon.xereon.R
import com.xereon.xereon.databinding.FragmentOnboardingBinding
import com.xereon.xereon.util.ui.doNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val viewModel by viewModels<OnboardingFragmentViewModel>()

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboardingBinding.bind(view)

        binding.apply {
            onboardingLoginBtn.setOnClickListener { viewModel.onChooseLoginClicked() }
            onboardingSignUp.setOnClickListener { viewModel.onChooseCreateAccountClicked() }
            onboardingSkip.setOnClickListener { viewModel.onSkippedButtonClick() }
        }
        viewModel.routeToScreen.observe(viewLifecycleOwner, Observer {
            when(it) {
                is OnboardingEvents.NavigateToRegister ->
                    doNavigate(
                        OnboardingFragmentDirections
                            .actionOnboardingFragmentToOnboardingRegisterFragment()
                    )
                is OnboardingEvents.NavigateToLogin ->
                    doNavigate(
                        OnboardingFragmentDirections
                            .actionOnboardingFragmentToOnboardingLoginFragment()
                    )
                is OnboardingEvents.NavigateToLocation ->
                    (requireActivity() as OnboardingActivity).completeOnboarding()
                is OnboardingEvents.NavigateToPrivacy ->
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.bundesregierung.de/breg-de/themen/corona-warn-app/corona-warn-app-leichte-sprache-gebaerdensprache")
                        )
                    )
            }
        })
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