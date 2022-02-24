package gapara.co.id.feature.emergency

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import gapara.co.id.BR
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.log
import gapara.co.id.core.base.*
import gapara.co.id.databinding.ActivityCreateEmergencyBinding
import gapara.co.id.feature.component.PictureView
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_create_emergency.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class CreateEmergencyActivity : MvvmActivity<ActivityCreateEmergencyBinding, EmergencyReportViewModel>(EmergencyReportViewModel::class), CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_create_emergency

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        viewModel.processIntent(intent)
        setupView()
    }

    private fun setupView() {
        setupAppBar()
        setupInput()
        setupTitleDate()
        setupCameraView()
    }

    private fun setupTitleDate() {
        timeTextView.text = TimeHelper.convertDateText(viewModel.dateToday.value, TimeHelper.FORMAT_DATE_TEXT)
    }

    private fun setupInput() {
        emergencyDetailInputView.setMinLines(3)
        emergencyDetailInputView.imeiOptions = EditorInfo.IME_ACTION_DONE
        emergencyDetailInputView.setMultiLineDone()
        emergencyDetailInputView.setHint("Description")
        titleInputView.setHint("Title")
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Create Emergency Report")
    }

    private fun registerObserver() {
        viewModel.createEmergencyApiResponse.observe(this, {
            showAlert(this, it?.message ?: "Failed create emergency report", R.color.green2, Toast.LENGTH_LONG)
            if (it?.isSuccess() == true) {
                setResult(createEmergencyCode)
                onBackPressed()
            }
        })
        viewModel.mediaResponse.observe(this, {
            if (it?.isSuccess() == true) {
                if (imagesView.childCount > 0) {
                    (imagesView.getChildAt(imagesView.childCount - 1) as PictureView).uniqueId = it.mediaModel?.id
                }
            } else {
                setupFailedUpload()
            }
        })
    }

    private fun setupFailedUpload() {
        showLongRedToast("Sorry failed upload image, please try again!")
        if (imagesView.childCount > 0) {
            try {
                val lastImageView = (imagesView.getChildAt(imagesView.childCount - 1) as PictureView)
                if (lastImageView.uniqueId.isNullOrBlank()) {
                    imagesView.removeView(lastImageView)
                }
            } catch (exception : Exception) {
                log("exception $exception")
            }
        }
    }

    fun onUploadImage() {
        showCamera()
    }

    fun onSave() {
        viewModel.imagesUploadedId.clear()
        for (index in 0 until imagesView.childCount) {
            val pictureView = imagesView.getChildAt(index) as PictureView
            pictureView.uniqueId.takeIf { !it.isNullOrBlank() }?.apply {
                viewModel.imagesUploadedId.add(this)
            }
        }

        hideKeyboard(this)
        viewModel.onCreateEmergency()
    }

    private fun setupCameraView() {
        capturePictureView.hideBottomDescription()
        capturePictureView.setEventFlash {
            capturePictureView.setupFlash()
        }
        capturePictureView.setEventCapture {
            setupUploadImage(it)
        }
        capturePictureView.setEventClose {
            hideCamera()
        }
    }

    private fun showCamera() {
        if (!EasyPermissions.hasPermissions(this, *cameraPermission)) {
            EasyPermissions.requestPermissions(this, "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu", 0, *cameraPermission)
        } else {
            capturePictureView.isVisible = true
            button.isVisible = false
            capturePictureView.setupMainCamera()
        }
    }

    private fun hideCamera() {
        button.isVisible = true
        capturePictureView.isVisible = false
    }

    override fun onBackPressed() {
        if (capturePictureView.isVisible) {
            hideCamera()
        } else {
            super.onBackPressed()
        }
    }

    private fun setupUploadImage(file: File) {
        launch {
            try {
                viewModel.showLoading()
                val compressedImageFile = Compressor.compress(this@CreateEmergencyActivity, file) {
                    quality(RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt())
                }

                delay(1000)

                runOnUiThread {
                    hideCamera()
                    viewModel.uploadImage(compressedImageFile)

                    val pictureView = PictureView(this@CreateEmergencyActivity)
                    pictureView.setClearClick {
                        imagesView.removeView(pictureView)
                    }
                    pictureView.setUriImage(Uri.fromFile(compressedImageFile))
                    imagesView.addView(pictureView)
                }
            } catch (exception : Exception) {
                viewModel.showLoading(false)
                hideCamera()
                showAlert(this@CreateEmergencyActivity, "Camera capture file is corrupt, please try again!")
            }
        }
    }

    companion object {

        const val createEmergencyCode = 323

        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, CreateEmergencyActivity::class.java)
            return intent
        }
    }
}