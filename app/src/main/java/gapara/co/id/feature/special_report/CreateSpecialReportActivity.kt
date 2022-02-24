package gapara.co.id.feature.special_report

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import gapara.co.id.BR
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.log
import gapara.co.id.core.base.*
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.databinding.ActivityCreateSpecialReportBinding
import gapara.co.id.feature.component.PictureView
import gapara.co.id.feature.incident.IncidentTypeDialog
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_create_special_report.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class CreateSpecialReportActivity : MvvmActivity<ActivityCreateSpecialReportBinding, SpecialReportViewModel>(SpecialReportViewModel::class), CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_create_special_report

    override val bindingVariable: Int = BR.viewModel

    private val incidentTypeDialog = IncidentTypeDialog {
        setupIncidentTypeSelected(it)
    }

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        viewModel.processIntent(intent)
        setupView()
        viewModel.getIncidentCategory()
    }

    private fun setupIncidentTypeSelected(it: ArrayList<GeneralModel>) {
        launch {
            incidentTypeView.removeAllViews()
            val parsingData = ArrayList<GeneralModel>()
            it.forEach { model ->
                if (model.isChecked == true) {
                    parsingData.add(model)
                    incidentTypeView.addView(getBorderTextView(model))
                }
            }
            viewModel.updateIncidentCategoriesSelected(parsingData)
        }
    }

    private fun getBorderTextView(model: GeneralModel): TextView {
        val size10 = pickSize(R.dimen._10sdp)
        val size5 = pickSize(R.dimen._5sdp)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.marginEnd = size10
        val textView = TextView(this)
        textView.layoutParams = layoutParams
        textView.text = model.name
        textView.setPadding(size10, size5 , size10, size5)
        textView.setTextColor(pickColor(R.color.black))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size5.toFloat())
        textView.typeface = pickFont(R.font.font_regular)
        textView.setBackgroundResource(R.drawable.bg_black_radius_4)
        return textView
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
        titleInputView.setHint("Incident name")
        remarksInputView.setHint("Remarks")
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Create Special Report")
    }

    private fun registerObserver() {
        viewModel.createReportApiResponse.observe(this, {
            if (it?.isSuccess() == true) {
                showLongToast(it.message ?: "Success create special report")
                setResult(createSpecialReportCode)
                onBackPressed()
            } else {
                showLongRedToast(getErrorMessageServer(it.errors?.values?.toString()))
            }
        })
        viewModel.incidentCategoryResponse.observe(this, {
            it?.apply {
                incidentTypeDialog.setIncidentData(this)
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

    fun onSave() {
        viewModel.imagesUploadedId.clear()
        for (index in 0 until imagesView.childCount) {
            val pictureView = imagesView.getChildAt(index) as PictureView
            pictureView.uniqueId.takeIf { !it.isNullOrBlank() }?.apply {
                viewModel.imagesUploadedId.add(this)
            }
        }
        hideKeyboard(this)
        viewModel.onCreateSpecialReport()
    }

    fun addIncidentType() {
        incidentTypeDialog.showNow(supportFragmentManager, "IncidentTypeDialog")
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
                val compressedImageFile = Compressor.compress(this@CreateSpecialReportActivity, file) {
                    quality(RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt())
                }

                delay(1000)

                runOnUiThread {
                    hideCamera()
                    viewModel.uploadImage(compressedImageFile)

                    val pictureView = PictureView(this@CreateSpecialReportActivity)
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

        const val createSpecialReportCode = 323

        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, CreateSpecialReportActivity::class.java)
            return intent
        }
    }
}