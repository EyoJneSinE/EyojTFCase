package com.eniskaner.eyojtfcase.firebase.presentation.view

import android.os.Bundle
import android.view.View
import com.eniskaner.eyojtfcase.common.base.BaseFragment
import com.eniskaner.eyojtfcase.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun setBinding(): FragmentLoginBinding =
        FragmentLoginBinding.inflate(layoutInflater)

}