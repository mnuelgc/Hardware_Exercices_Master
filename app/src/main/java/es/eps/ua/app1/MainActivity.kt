package es.eps.ua.app1

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import es.eps.ua.app1.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityMainBinding
    private var imageCapture : ImageCapture? = null
    private lateinit var locationManager : LocationManager
    private lateinit var labelCoordinates : TextView


    private  val locationService : LocationService = LocationService()

    private lateinit var cameraExecutor : ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        labelCoordinates = viewBinding.gpsLayer
        // Request camera permission
        if (cameraPermissionGranted()){
            startCamera()
        }else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_CAMERA_PERMISSIONS, REQUEST_CAMERA_CODE_PERSMISSIONS)
        }
            // Set up the listeners for take photo and video capture buttons

            viewBinding.getGpsButton.setOnClickListener{takePhoto()}

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto(){
        //Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

        lifecycleScope.launch {
            Toast.makeText(baseContext, "LAUNCH", Toast.LENGTH_SHORT).show()

            val result = locationService.getUserLocation(this@MainActivity)
            if (result != null){
                Toast.makeText(baseContext, "ONRESULT", Toast.LENGTH_SHORT).show()

                labelCoordinates.text = "Latitud ${result.latitude} y longitud de la foto ${result.longitude}"
            }
        }


        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object wich contains file + metadata

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues)
            .build()

        // Set up image capture listener, wich is trggered after photo has been taken

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError (exc : ImageCaptureException){
                    Log.e(TAG, "Photo  capture failes : ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeed: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    if (gpsPermissionGranted()){
                        writeLocationData()
                    }else {
                        ActivityCompat.requestPermissions(
                            this@MainActivity, REQUIRED_GPS_PERMISSIONS, REQUEST_GPS_CODE_PERSMISSIONS)

                    }
                }
            }
        )
    }
    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Use to bind the lifecycle of camera to the lifecycle owner

            val cameraProvider : ProcessCameraProvider = cameraProviderFuture.get()

            //Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.cameraSpace.surfaceProvider)
                }
            // select back camera as default
            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try{ //Unbind use case before rebind
                cameraProvider.unbindAll()

                //Bind use case to camera

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
            }catch (exc: Exception){
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))

    }


    private fun writeLocationData()
    {
    }
    private fun cameraPermissionGranted() = REQUIRED_CAMERA_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun gpsPermissionGranted() = REQUIRED_GPS_PERMISSIONS.all {
           ContextCompat.checkSelfPermission(
               baseContext, it
           ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CAMERA_CODE_PERSMISSIONS){
            if (cameraPermissionGranted()){
                startCamera()
            }else{
                Toast.makeText(this,
                    "Permission not granted by the user", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CAMERA_CODE_PERSMISSIONS = 10
        private const val REQUEST_GPS_CODE_PERSMISSIONS = 20
        private  val REQUIRED_CAMERA_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        private  val REQUIRED_GPS_PERMISSIONS =
            mutableListOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).toTypedArray()

    }
}
