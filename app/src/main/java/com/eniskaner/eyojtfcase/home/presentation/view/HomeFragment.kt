package com.eniskaner.eyojtfcase.home.presentation.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.eniskaner.eyojtfcase.R
import com.eniskaner.eyojtfcase.common.base.BaseFragment
import com.eniskaner.eyojtfcase.common.util.launchAndRepeatWithViewLifecycle
import com.eniskaner.eyojtfcase.databinding.FragmentHomeBinding
import com.eniskaner.eyojtfcase.home.domain.util.HomeUIState
import com.eniskaner.eyojtfcase.home.presentation.viewmodel.HomeViewModel
import com.eniskaner.eyojtfcase.mainactivity.presentation.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()
    private val navController: NavController by lazy {
        findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.actionBar?.show()

        clickDetektButton()
        observeSugarBeetCount()
    }

    override fun setBinding(): FragmentHomeBinding =
        FragmentHomeBinding.inflate(layoutInflater)

    private fun clickDetektButton() {
        binding.btnAction.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                navController.navigate(R.id.action_homeFragment_to_cameraFragment)
            } else {
                requireContext().checkSelfPermission(android.Manifest.permission.CAMERA)
                //requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
            }
        }
    }

    private fun observeSugarBeetCount() {
        viewModel.sugarBeetCountState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is HomeUIState.Loading -> {
                    binding.progressIndicator.isVisible = true
                }

                is HomeUIState.Failure -> {
                    binding.progressIndicator.isVisible = false
                    Toast.makeText(requireContext(), state.error.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

                is HomeUIState.Success -> {
                    binding.progressIndicator.isVisible = false
                    binding.textDetektCount.text = state.data.toString()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSugarBeetCountFromFireStore()
        launchAndRepeatWithViewLifecycle {
            launch {
                observeSugarBeetCount()
            }
        }
    }

}