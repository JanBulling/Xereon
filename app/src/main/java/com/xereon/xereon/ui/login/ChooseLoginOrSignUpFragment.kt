package com.xereon.xereon.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xereon.xereon.R
import com.xereon.xereon.databinding.FrgChooseLoginOrSignupBinding
import com.xereon.xereon.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseLoginOrSignUpFragment : Fragment(R.layout.frg_choose_login_or_signup) {

    private var _binding: FrgChooseLoginOrSignupBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var preferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgChooseLoginOrSignupBinding.bind(view)

        binding.apply {
            chooseLoginBtn.setOnClickListener {
                findNavController().navigate(R.id.action_Choose_to_Login)
            }

            chooseSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_Choose_to_SignUp)
            }

            chooseSkip.setOnClickListener {
                preferences.edit().putInt(
                    Constants.PREF_APPLICATION_STATE,
                    Constants.ApplicationState.SKIPPED_NO_LOCATION.index
                ).apply()

                findNavController().navigate(R.id.action_Choose_to_ChooseLocation)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}