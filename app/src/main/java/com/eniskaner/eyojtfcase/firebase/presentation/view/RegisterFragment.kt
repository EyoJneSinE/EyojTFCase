package com.eniskaner.eyojtfcase.firebase.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eniskaner.eyojtfcase.R
import com.eniskaner.eyojtfcase.common.base.BaseFragment
import com.eniskaner.eyojtfcase.databinding.FragmentRegisterBinding

class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    override fun setBinding(): FragmentRegisterBinding = FragmentRegisterBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}