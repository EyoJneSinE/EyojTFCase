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
import com.eniskaner.eyojtfcase.databinding.FragmentRegisterBinding
import com.eniskaner.eyojtfcase.firebase.presentation.util.launchAndRepeatWithViewLifecycle
import com.eniskaner.eyojtfcase.firebase.presentation.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

    private val registerViewModel: RegisterViewModel by viewModels()

    private val navController: NavController by lazy {
        findNavController()
    }


    override fun setBinding(): FragmentRegisterBinding =
        FragmentRegisterBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmPasswordFocusListener()

        binding.btnAction.setOnClickListener {
            signUpClicked(
                email = binding.signUpEmail.text.toString().trim(),
                password = binding.signUpPassword.text.toString().trim()
            )
        }

        launchAndRepeatWithViewLifecycle {
            launch {
                observeSignUp()
            }
        }
    }

    private fun signUpClicked(email:String, password:String){
        val emailHelperText = binding.textFieldEmail.helperText
        val passwordHelperText = binding.textFieldPassword.helperText
        val confirmPasswordHelperText = binding.textFieldConfirmPassword.helperText
        if(emailHelperText == null && passwordHelperText == null && confirmPasswordHelperText == null){
            registerViewModel.signUpFirebase(email,password)
        }else{
            //showToast(requireContext(),R.string.lutfen_eposta_sifre_kontrol_ediniz)
        }
    }
    private fun confirmPasswordFocusListener(){
        binding.signUpConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val password = binding.signUpPassword.text.toString()
                val confirmPassword = binding.signUpConfirmPassword.text.toString()
                if(password != confirmPassword){
                    binding.textFieldConfirmPassword.error = "Passwords not the same"
                }else{
                    binding.textFieldConfirmPassword.error = null
                }
            }
        })
    }

    private fun observeSignUp() {
        registerViewModel.signUpState.observe(viewLifecycleOwner) { state ->
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
                    Toast.makeText(requireContext(), "Successfully signed up", Toast.LENGTH_SHORT)
                        .show()
                    navController.navigate(R.id.main)
                }
            }
        }
    }

}