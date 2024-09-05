package com.eniskaner.eyojtfcase.home.presentation.view

import android.os.Bundle
import android.view.View
import com.eniskaner.eyojtfcase.common.base.BaseFragment
import com.eniskaner.eyojtfcase.databinding.FragmentHomeBinding
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setBinding(): FragmentHomeBinding =
        FragmentHomeBinding.inflate(layoutInflater)

}