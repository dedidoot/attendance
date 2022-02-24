package gapara.co.id.feature.lost_found

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.inputmethod.EditorInfo
import gapara.co.id.BR
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.GSONManager
import gapara.co.id.core.api.log
import gapara.co.id.core.base.*
import gapara.co.id.core.model.RemoteConfigModel
import gapara.co.id.databinding.ActivityCreateLostFoundBinding
import gapara.co.id.feature.component.PictureView
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_create_lost_found.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.*

class CreateLostFoundActivity : MvvmActivity<ActivityCreateLostFoundBinding, LostFoundViewModel>(LostFoundViewModel::class), CoroutineDeclare {

    private var typePopupWindow: PopupWindow? = null
    private var dateDialog : DialogCalender? = null
    
    override val layoutResource: Int = R.layout.activity_create_lost_found

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        setupView()
    }

    private fun setupView() {
        setupAppBar()
        setupInput()
        setupTitleDate()
        setupCameraView()
        setupTypeDropDown()
        checkLostFoundCategory()
        setupDate()
    }

    private fun setupDate() {
        dateDialog = DialogCalender(this)
        dateTimeView.setTitle("-")
        dateDialog?.setupDialog({
            showTimerDialog(it)
        }, DialogCalender.year_day_month2)?.setDisablePreviousDate()

        dateTimeView.setClick {
            dateDialog?.showDialog()
        }
    }

    private fun showTimerDialog(it: String) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            var realHours = "$hour"
            if (hour.toString().length == 1) {
                realHours = "0$hour"
            }
            var realMinutes = "$minute"
            if (minute.toString().length == 1) {
                realMinutes = "0$minute"
            }
            viewModel.updateDateTime("$it $realHours:$realMinutes:00")
            dateTimeView.setTitle(TimeHelper.convertDateText(it, TimeHelper.FORMAT_DATE_TEXT)+" $realHours:$realMinutes:00")
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun checkLostFoundCategory() {
        val isShowLostFoundType = RemoteConfigHelper.getBoolean(RemoteConfigKey.IS_SHOW_LOST_FOUND_TYPE)
        typeTextView.isVisible = isShowLostFoundType
        typeDropDown.isVisible = isShowLostFoundType
        if (!isShowLostFoundType) {
            viewModel.updateType(DEFAULT_TYPE)
        }
    }

    private fun setupTypeDropDown() {
        typePopupWindow = PopupWindow(this)
        typeDropDown.setTitle("-")
        typeDropDown.setRightImage(R.drawable.ic_black_drop_down)
        typeDropDown.setPaddingRightImage(resources.getDimensionPixelSize(R.dimen._3sdp))
        typeDropDown.setClick {
            typePopupWindow?.clearAll()
            val absentCategory = RemoteConfigHelper.get(RemoteConfigKey.LOST_FOUND_CATEGORY)
            val model = GSONManager.fromJson(absentCategory, RemoteConfigModel::class.java)
            typePopupWindow?.addItems(model.data ?: arrayListOf())
            typePopupWindow?.setEventListener {
                viewModel.updateType(it.name)
                typeDropDown.setTitle(it.name)

                val isTypeFound = viewModel.isTypeFound().not()
                locationTV.isVisible = isTypeFound
                locationInputView.isVisible = isTypeFound
                dateTV.isVisible = isTypeFound
                dateTimeView.isVisible = isTypeFound
            }
            typePopupWindow?.showPopup(typeDropDown)
        }
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
        locationInputView.setHint("Example : Stuff under the table")
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Lost Found Report")
    }

    private fun registerObserver() {
        viewModel.createReportApiResponse.observe(this, { response ->
            if (response?.isSuccess() == true) {
                showLongToast("Success create report")
                setResult(createLostFoundReportCode)
                onBackPressed()
            } else {
                val message = getErrorMessageServer(response.errors?.values?.toString()).takeIf { it.isNotBlank() } ?: kotlin.run { response?.message ?: "Failed create report" }
                showLongRedToast(message)
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

    fun onSave() {
        viewModel.imagesUploadedId.clear()
        for (index in 0 until imagesView.childCount) {
            val pictureView = imagesView.getChildAt(index) as PictureView
            pictureView.uniqueId.takeIf { !it.isNullOrBlank() }?.apply {
                viewModel.imagesUploadedId.add(this)
            }
        }
        hideKeyboard(this)
        viewModel.onCreateLostFoundReport()
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
        /*if (viewModel.typeName.value.isNullOrBlank()) {
            showLongRedToast("Please choose type first!")
            return
        }*/
        launch {
            try {
                viewModel.showLoading()
                val compressedImageFile = Compressor.compress(this@CreateLostFoundActivity, file) {
                    quality(RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt())
                }

                delay(1000)

                runOnUiThread {
                    hideCamera()
                    viewModel.uploadImage(compressedImageFile)

                    val pictureView = PictureView(this@CreateLostFoundActivity)
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

        const val createLostFoundReportCode = 323
        const val DEFAULT_TYPE = "found"

        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, CreateLostFoundActivity::class.java)
            return intent
        }
    }
}