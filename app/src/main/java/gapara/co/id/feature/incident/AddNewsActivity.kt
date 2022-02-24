package gapara.co.id.feature.incident

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.inputmethod.EditorInfo
import gapara.co.id.BR
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.databinding.ActivityAddNewsActivityBinding
import gapara.co.id.feature.component.PictureView
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_add_news_activity.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class AddNewsActivity : MvvmActivity<ActivityAddNewsActivityBinding, AddNewsViewModel>(AddNewsViewModel::class), CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_add_news_activity

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.processIntent(intent)
        registerObserver()
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
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Create News Incident")
    }

    private fun registerObserver() {
        viewModel.createReportApiResponse.observe(this, { response ->
            if (response?.isSuccess() == true) {
                showLongToast("Success News Incident")
                onBackPressed()
            } else {
                val message = getErrorMessageServer(response.errors?.values?.toString()).takeIf { it.isNotBlank() } ?: kotlin.run { response?.message ?: "Failed create report" }
                showLongRedToast(message)
            }
        })
        viewModel.incidentId.observe(this, {
            if (it.isNullOrBlank()) {
                showLongRedToast("Incident not found")
                finish()
            }
        })
    }

    fun onSave() {
        viewModel.attachments.clear()
        for (index in 0 until imagesView.childCount) {
            val pictureView = imagesView.getChildAt(index) as PictureView
            pictureView.uniqueId.takeIf { !it.isNullOrBlank() }?.apply {
                viewModel.attachments.add(File(this))
            }
        }
        hideKeyboard(this)
        viewModel.onCreateIntelReport()
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

    fun onUploadImage() {
        showCamera()
    }

    private fun showCamera() {
        hideKeyboard(this)
        if (!EasyPermissions.hasPermissions(this, *cameraPermission)) {
            EasyPermissions.requestPermissions(this, "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu", 0, *cameraPermission)
        } else {
            capturePictureView.isVisible = true
            button.isVisible = false
            capturePictureView.setupMainCamera()
            button.isVisible = false
        }
    }

    private fun hideCamera() {
        button.isVisible = true
        capturePictureView.isVisible = false
        button.isVisible = true
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
                val compressedImageFile = Compressor.compress(this@AddNewsActivity, file) {
                    quality(RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt())
                }

                delay(1000)

                runOnUiThread {
                    hideCamera()

                    val pictureView = PictureView(this@AddNewsActivity)
                    pictureView.uniqueId = compressedImageFile.absolutePath
                    pictureView.setClearClick {
                        imagesView.removeView(pictureView)
                    }
                    pictureView.setUriImage(Uri.fromFile(compressedImageFile))
                    imagesView.addView(pictureView)
                }
            } catch (exception : Exception) {
                viewModel.showLoading(false)
                hideCamera()
                showLongRedToast("Camera capture file is corrupt, please try again!")
            }
        }
    }

    companion object {

        fun onNewIntent(context: Context, incidentId: String?): Intent {
            val intent = Intent(context, AddNewsActivity::class.java)
            intent.putExtra(AddNewsViewModel.EXTRA_INCIDENT_ID, incidentId)
            return intent
        }
    }
}