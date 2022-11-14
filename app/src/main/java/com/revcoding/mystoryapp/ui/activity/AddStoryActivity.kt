package com.revcoding.mystoryapp.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.revcoding.mystoryapp.R
import com.revcoding.mystoryapp.ViewModelFactory
import com.revcoding.mystoryapp.data.model.UserPreference
import com.revcoding.mystoryapp.databinding.ActivityAddStoryBinding
import com.revcoding.mystoryapp.helper.createTempFile
import com.revcoding.mystoryapp.helper.reduceFileImage
import com.revcoding.mystoryapp.helper.rotateBitmap
import com.revcoding.mystoryapp.helper.uriToFile
import com.revcoding.mystoryapp.ui.viewmodel.AddStoryViewModel
import com.revcoding.mystoryapp.ui.viewmodel.MainViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            when {
                cameraPermissionGranted() -> {
                    openCamera()
                }
                !cameraPermissionGranted() -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.title_access_denied))
                        setMessage(getString(R.string.message_camera_access_denied))
                        setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun cameraPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_add_story)

        binding.capturePhoto.setOnClickListener { givePermission() }
        binding.choosePicture.setOnClickListener { openGallery() }
        binding.uploadStory.setOnClickListener { uploadStory() }

        setupViewModel()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        addStoryViewModel = ViewModelProvider(this)[AddStoryViewModel::class.java]
    }

    private fun givePermission(){
        if (!cameraPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.revcoding.mystoryapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadStory() {
        val description = binding.etDescriptionStory.text.toString()

        when {
            getFile == null -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.title_photo_required))
                    setMessage(getString(R.string.message_insert_photo_first))
                    setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                    create()
                    show()
                }
            }
            description.isEmpty() -> {
                binding.etDescriptionStory.error = getString(R.string.error_description_empty)
            }
            else -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.title_confirmation))
                    setMessage(getString(R.string.message_are_you_sure_upload_story))
                    setPositiveButton(getString(R.string.yes)) { _, _ ->

                        val file = reduceFileImage(getFile as File)

                        val descriptionBody = description.toRequestBody("text/plain".toMediaType())
                        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "photo",
                            file.name,
                            requestImageFile
                        )
                        mainViewModel.getUser().observe(this@AddStoryActivity) { user ->
                            addStoryViewModel.uploadStory(
                                user.token,
                                imageMultiPart,
                                descriptionBody
                            )
                        }

                        addStoryViewModel.uploadSuccess.observe(this@AddStoryActivity) {
                            it.getContentIfNotHandled()?.let {
                                uploadSuccess()
                            }
                        }

                        addStoryViewModel.isLoading.observe(this@AddStoryActivity) {
                            it.getContentIfNotHandled()?.let { state ->
                                showLoading(state)
                            }
                        }

                        addStoryViewModel.isFailed.observe(this@AddStoryActivity) {
                            it.getContentIfNotHandled()?.let {
                                isFailed()
                            }
                        }
                    }
                    setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel()}
                    create()
                    show()
                }
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            val result = BitmapFactory.decodeFile(myFile.path)

            getFile = myFile

            binding.previewPhoto.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            getFile = myFile

            binding.previewPhoto.setImageURI(selectedImg)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    private fun uploadSuccess() {
        AlertDialog.Builder(this).apply {
            setCancelable(false)
            setTitle(getString(R.string.title_success))
            setMessage(getString(R.string.message_story_uploaded_successfully))
            setPositiveButton(getString(R.string.next)) { _, _ -> finish() }
            create()
            show()
        }
    }

    private fun isFailed() {
        AlertDialog.Builder(this).apply {
            setCancelable(false)
            setTitle(getString(R.string.title_failure))
            setMessage(getString(R.string.message_there_is_problem))
            setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}