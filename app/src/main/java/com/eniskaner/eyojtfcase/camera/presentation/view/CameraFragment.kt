package com.eniskaner.eyojtfcase.camera.presentation.view

import android.os.Bundle
import android.view.View
import com.eniskaner.eyojtfcase.common.base.BaseFragment
import com.eniskaner.eyojtfcase.databinding.FragmentCameraBinding

class CameraFragment : BaseFragment<FragmentCameraBinding>() {

    override fun setBinding(): FragmentCameraBinding =
        FragmentCameraBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}