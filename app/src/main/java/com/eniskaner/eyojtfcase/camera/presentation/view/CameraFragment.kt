package com.eniskaner.eyojtfcase.camera.presentation.view

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.TextureView
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.eniskaner.eyojtfcase.camera.presentation.viewmodel.CameraViewModel
import com.eniskaner.eyojtfcase.common.base.BaseFragment
import com.eniskaner.eyojtfcase.common.util.Constants.LABELS_TXT
import com.eniskaner.eyojtfcase.common.util.Constants.VIDEO_THREAD
import com.eniskaner.eyojtfcase.databinding.FragmentCameraBinding
import com.eniskaner.eyojtfcase.ml.Detect
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.support.common.FileUtil

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>() {

    lateinit var labels: List<String>
    private lateinit var bitmap: Bitmap
    private lateinit var handler: Handler
    private lateinit var model: Detect
    private val viewModel: CameraViewModel by viewModels()
    private val navController: NavController by lazy {
        findNavController()
    }

    override fun setBinding(): FragmentCameraBinding =
        FragmentCameraBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        labels = FileUtil.loadLabels(requireContext(), LABELS_TXT)
        model = Detect.newInstance(requireContext())
        observeTextureView()
        clickCloseButton()
    }

    private fun clickCloseButton() {
        binding.closeBtn.setOnClickListener {
            viewModel.updateSugarBeetCount()
            navController.popBackStack()
        }
    }

    private fun observeTextureView() {
        val handlerThread = HandlerThread(VIDEO_THREAD)
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        binding.textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                binding.textureView.surfaceTexture?.let {
                    viewModel.openCamera(
                        it,
                        requireContext(),
                        handler
                    )
                }
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                bitmap = binding.textureView.bitmap!!
                binding.imageView.setImageBitmap(viewModel.onDetection(bitmap, model, labels))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.updateSugarBeetCount()
        viewModel.closeCamera()
        model.close()
    }
}