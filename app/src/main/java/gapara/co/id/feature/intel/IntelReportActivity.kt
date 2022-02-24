package gapara.co.id.feature.intel

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.inputmethod.EditorInfo
import gapara.co.id.BR
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.log
import gapara.co.id.core.base.*
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.databinding.ActivityIntelReportActivityBinding
import gapara.co.id.feature.component.PictureView
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_intel_report_activity.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class IntelReportActivity : MvvmActivity<ActivityIntelReportActivityBinding, IntelViewModel>(IntelViewModel::class), CoroutineDeclare {

    private var schedulePopupWindow: PopupWindow? = null
    private var dateDialog : DialogCalender? = null
    
    override val layoutResource: Int = R.layout.activity_intel_report_activity

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        setupView()
        viewModel.getCurrentSchedule()
    }

    private fun setupView() {
        setupAppBar()
        setupInput()
        setupTitleDate()
        setupCameraView()
        setupTypeDropDown()
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

    private fun setupTypeDropDown() {
        schedulePopupWindow = PopupWindow(this)
        scheduleDropDown.setTitle("-")
        scheduleDropDown.setRightImage(R.drawable.ic_black_drop_down)
        scheduleDropDown.setPaddingRightImage(resources.getDimensionPixelSize(R.dimen._3sdp))
        scheduleDropDown.setClick {
            schedulePopupWindow?.clearAll()

            val models = ArrayList<GeneralModel>()
            viewModel.currentScheduleResponse.value?.data?.schedules?.forEach {
                models.add(GeneralModel(it.id, it.shift?.name))
            }
            schedulePopupWindow?.addItems(models)
            schedulePopupWindow?.setEventListener {
                viewModel.updateScheduleId(it.id)
                scheduleDropDown.setTitle(it.name)

            }
            schedulePopupWindow?.showPopup(scheduleDropDown)
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
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Create Report")
    }

    private fun registerObserver() {
        viewModel.createReportApiResponse.observe(this, { response ->
            if (response?.isSuccess() == true) {
                showLongToast("Success create report")
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
                val compressedImageFile = Compressor.compress(this@IntelReportActivity, file) {
                    quality(RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt())
                }

                delay(1000)

                runOnUiThread {
                    hideCamera()
                    viewModel.uploadImage(compressedImageFile)

                    val pictureView = PictureView(this@IntelReportActivity)
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

        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, IntelReportActivity::class.java)
            return intent
        }
    }
}