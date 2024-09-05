package com.eniskaner.eyojtfcase.firebase.presentation.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.eniskaner.eyojtfcase.R
import com.eniskaner.eyojtfcase.common.base.BaseFragment
import com.eniskaner.eyojtfcase.common.util.AuthUIState
import com.eniskaner.eyojtfcase.databinding.FragmentLoginBinding
import com.eniskaner.eyojtfcase.firebase.presentation.util.launchAndRepeatWithViewLifecycle
import com.eniskaner.eyojtfcase.firebase.presentation.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val loginViewModel: LoginViewModel by viewModels()
    private var isUserLoggedIn: Boolean = true
    private val navController: NavController by lazy {
        findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isUserLoggedIn = loginViewModel.isUserLoggedIn

        if (isUserLoggedIn) {
            navController.navigate(R.id.main)
        }

        passwordFocusListener()

        binding.btnAction.setOnClickListener {
            signInClicked(
                email = binding.signInEmail.text.toString().trim(),
                password = binding.signInPassword.text.toString().trim()
            )
        }

        launchAndRepeatWithViewLifecycle {
            launch {
                observeSignInState()
            }
        }
    }
    override fun setBinding(): FragmentLoginBinding =
        FragmentLoginBinding.inflate(layoutInflater)

    private fun observeSignInState() {
        loginViewModel.signInFirebaseState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthUIState.Loading -> {
                    binding.progressIndicator.isVisible = true
                }

                is AuthUIState.Failure -> {
                    binding.progressIndicator.isVisible = false
                    Toast.makeText(requireContext(), state.error.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

                is AuthUIState.Success -> {
                    binding.progressIndicator.isVisible = false
                    navController.navigate(R.id.main)
                }
            }
        }
    }

    private fun signInClicked(email:String, password:String){
        val emailHelperText = binding.textFieldEmail.helperText
        val passwordHelperText = binding.textFieldPassword.helperText
        if(emailHelperText == null && passwordHelperText == null){
            loginViewModel.signInFirebase(
                email = email,
                password = password
            )
        }else{
            //showToast(requireContext(),R.string.required_email_and_password)
        }
    }

    private fun passwordFocusListener(){
        binding.signInPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                binding.textFieldPassword.helperText = validPassword()
            }
        })
    }

    private fun validPassword(): String? {
        val password = binding.signInPassword.text.toString()
        if(password.isEmpty()){
            return "Need Password"
        }
        return null
    }

}