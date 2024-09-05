package com.eniskaner.eyojtfcase.mainactivity.presentation.view

import android.Manifest
import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.Typeface
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.util.TypedValue
import android.view.Surface
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eniskaner.eyojtfcase.R
import com.eniskaner.eyojtfcase.objectdetekt.presentation.customview.IBoxTracker
import com.eniskaner.eyojtfcase.objectdetekt.presentation.customview.ImageUtils.convertYUV420ToARGB8888
import com.eniskaner.eyojtfcase.objectdetekt.presentation.customview.ImageUtils.getTransformationMatrix
import com.eniskaner.eyojtfcase.objectdetekt.presentation.customview.OverLayView
import com.eniskaner.eyojtfcase.objectdetekt.presentation.customview.TextBorder
import com.eniskaner.eyojtfcase.objectdetekt.presentation.view.CameraConnectionFragment
import java.io.IOException

open class MainActivity : AppCompatActivity(), ImageReader.OnImageAvailableListener {

    var resultTV: TextView? = null
    private var detector: Detector? = null
    private var frameToCropTransform: Matrix? = null
    private var cropToFrameTransform: Matrix? = null
    var minimumConfidence: Float = 0.5f

    // Configuration values for the prepackaged SSD model.
    private val MAINTAIN_ASPECT = false
    private val TEXT_SIZE_DIP = 10f

    var trackingOverlay: OverLayView? = null
    private var borderedText: TextBorder? = null

    var previewHeight = 0
    var previewWidth = 0
    private var sensorOrientation = 0

    var croppedBitmap: Bitmap? = null
    private var tracker: IBoxTracker? = null

    //TODO getting frames of live camera footage and passing them to model
    private var isProcessingFrame = false
    private val yuvBytes = arrayOfNulls<ByteArray>(3)
    private var rgbBytes: IntArray? = null
    private var yRowStride = 0
    private var postInferenceCallback: Runnable? = null
    private var imageConverter: Runnable? = null
    private var rgbFrameBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //TODO show live camera footage
        //TODO ask for permission of camera upon first launch of application
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
            ) {
                val permission = arrayOf(
                    Manifest.permission.CAMERA
                )
                requestPermissions(permission, 1122)
            } else {
                //TODO show live camera footage
                setFragment()
            }
        }else{
            //TODO show live camera footage
            setFragment()
        }
        resultTV = findViewById(R.id.textView)


        //TODO inialize object detector
        try {
            detector = TFLiteObjectDetectionAPIModel.create(
                this,
                "model2.tflite",
                "labelmap.txt",
                320,
                true
            )
            Log.d("tryLog", "success")
        } catch (e: IOException) {
            Log.d("tryException", "error in town" + e.message)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO show live camera footage
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //TODO show live camera footage
            setFragment()
        } else {
            finish()
        }
    }

    //TODO fragment which show llive footage from camera

    private fun setFragment() {
        val manager =
            getSystemService(Context.CAMERA_SERVICE) as CameraManager
        var cameraId: String? = null
        try {
            cameraId = manager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        val fragment: Fragment
        val camera2Fragment = CameraConnectionFragment.newInstance(
            object :
                CameraConnectionFragment.ConnectionCallback {
                override fun onPreviewSizeChosen(size: Size?, rotation: Int) {
                    previewHeight = size!!.height
                    previewWidth = size.width
                    val textSizePx = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        TEXT_SIZE_DIP,
                        resources.displayMetrics
                    )
                    borderedText = TextBorder(textSizePx)
                    borderedText!!.setTypeface(Typeface.MONOSPACE)
                    tracker = IBoxTracker(this@MainActivity)

                    val cropSize = 300
                    previewWidth = size.width
                    previewHeight = size.height
                    sensorOrientation = rotation - getScreenOrientation()
                    croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888)

                    frameToCropTransform = getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT
                    )
                    cropToFrameTransform = Matrix()
                    frameToCropTransform!!.invert(cropToFrameTransform)

                    trackingOverlay =
                        findViewById<View>(R.id.tracking_overlay) as OverLayView
                    trackingOverlay!!.addCallback(
                        object : OverLayView.DrawCallback {
                            override fun drawCallback(canvas: Canvas?) {
                                tracker!!.draw(canvas!!)
                            }
                        })
                    tracker!!.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation)
                }
            },
            this,
            R.layout.fragment_camera_connection,
            Size(640, 480)
        )
        camera2Fragment.setCamera(cameraId)
        fragment = camera2Fragment
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    override fun onImageAvailable(reader: ImageReader?) {
        // We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return
        }
        if (rgbBytes == null) {
            rgbBytes = IntArray(previewWidth * previewHeight)
        }
        try {
            val image = reader.acquireLatestImage() ?: return
            if (isProcessingFrame) {
                image.close()
                return
            }
            isProcessingFrame = true
            val planes = image.planes
            fillBytes(planes, yuvBytes)
            yRowStride = planes[0].rowStride
            val uvRowStride = planes[1].rowStride
            val uvPixelStride = planes[1].pixelStride
            imageConverter = Runnable {
                convertYUV420ToARGB8888(
                    yuvBytes[0]!!,
                    yuvBytes[1]!!,
                    yuvBytes[2]!!,
                    previewWidth,
                    previewHeight,
                    yRowStride,
                    uvRowStride,
                    uvPixelStride,
                    rgbBytes!!
                )
            }
            postInferenceCallback = Runnable {
                image.close()
                isProcessingFrame = false
            }
            processImage()
        } catch (e: Exception) {
            Log.d("tryError", e.message + "abc ")
            return
        }
    }

    private fun processImage() {
        imageConverter!!.run()
        rgbFrameBitmap =
            Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
        rgbBytes?.let { rgbFrameBitmap!!.setPixels(it, 0, previewWidth, 0, 0, previewWidth, previewHeight) }

        val canvas = Canvas(croppedBitmap!!)
        canvas.drawBitmap(rgbFrameBitmap!!, frameToCropTransform!!, null)

        //TODO pass image to model and get results
        val results = detector!!.recognizeImage(croppedBitmap)
        results.removeIf { t->t.confidence<minimumConfidence }
        for (result in results) {
            val location: RectF = result.getLocation()
            cropToFrameTransform!!.mapRect(location)
            result.setLocation(location)
        }

        tracker?.trackResults(results, 10)
        trackingOverlay?.postInvalidate()
        postInferenceCallback!!.run()

    }

    private fun fillBytes(
        planes: Array<Image.Plane>,
        yuvBytes: Array<ByteArray?>
    ) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null) {
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer[yuvBytes[i]]
        }
    }

    protected fun getScreenOrientation(): Int {
        return when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_270 -> 270
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_90 -> 90
            else -> 0
        }
    }
}