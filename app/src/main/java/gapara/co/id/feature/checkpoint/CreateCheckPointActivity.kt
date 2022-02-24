package gapara.co.id.feature.checkpoint

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.inputmethod.EditorInfo
import gapara.co.id.BR
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.log
import gapara.co.id.core.base.*
import gapara.co.id.databinding.ActivityCreateCheckPointBinding
import gapara.co.id.feature.component.CameraBottomSheet
import gapara.co.id.feature.component.PictureView
import kotlinx.android.synthetic.main.activity_create_check_point.*
import pub.devrel.easypermissions.EasyPermissions

class CreateCheckPointActivity : MvvmActivity<ActivityCreateCheckPointBinding, CheckPointViewModel>(CheckPointViewModel::class), CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_create_check_point

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
        detailAppBarView.setTitleBar("Create Checkpoint Report")
    }

    private fun registerObserver() {
        viewModel.createCheckPointApiResponse.observe(this, {
            showLongToast(it?.message ?: "Failed create check point report")
            if (it?.isSuccess() == true) {
                setResult(createCheckPointCode)
                onBackPressed()
            }
        })
        viewModel.mediaResponse.observe(this, {
            if (it?.isSuccess() == true) {
                if (imagesView.childCount > 0) {
                    (imagesView.getChildAt(imagesView.childCount - 1) as PictureView).uniqueId = it.data?.image?.id
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
        hideKeyboard(this)
        if (!EasyPermissions.hasPermissions(this, *cameraPermission)) {
            EasyPermissions.requestPermissions(this, "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu", 0, *cameraPermission)
        } else {
            CameraBottomSheet().showDialog(supportFragmentManager) {
                viewModel.uploadImage(it)

                val pictureView = PictureView(this)
                pictureView.setClearClick {
                    imagesView.removeView(pictureView)
                }
                pictureView.setUriImage(Uri.fromFile(it))
                imagesView.addView(pictureView)
            }
        }
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
        viewModel.onCreateCheckPointReport()
    }

    companion object {

        const val createCheckPointCode = 323

        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, CreateCheckPointActivity::class.java)
            return intent
        }
    }
}