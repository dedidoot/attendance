package gapara.co.id.feature.incident

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
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
import gapara.co.id.core.model.IncidentModel
import gapara.co.id.databinding.ActivityCreateIncidentBinding
import gapara.co.id.feature.component.CameraBottomSheet
import gapara.co.id.feature.component.PictureView
import gapara.co.id.feature.component.UserBottomSheet
import kotlinx.android.synthetic.main.activity_create_incident.*
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList


class CreateIncidentActivity : MvvmActivity<ActivityCreateIncidentBinding, IncidentViewModel>(
    IncidentViewModel::class
), CoroutineDeclare {

    private var emergencyPopupWindow: PopupWindow? = null
    private var importantLevelPopupWindow: PopupWindow? = null
    private var shiftPopupWindow: PopupWindow? = null
    private var dateDialog : DialogCalender? = null
    private val incidentTypeDialog = IncidentTypeDialog {
        setupIncidentTypeSelected(it)
    }

    override val layoutResource: Int = R.layout.activity_create_incident

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        incidentTypeDialog.title = "Categories"
        registerObserver()
        viewModel.processIntent(intent)
        setupView()
        viewModel.getIncidentCategory()
        viewModel.getEmergency()
        viewModel.getCurrentSchedule()
    }

    private fun setupView() {
        setupAppBar()
        setupDate()
        setupInput()
        setupPicDropDown()
        setupClientDropDown()
        setupEmergencyDropDown()
        setupImportantLevelDropDown()
        setupShiftDropDown()
        setupResetTV()
    }

    private fun setupResetTV() {
        val content = SpannableString("Reset")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        resetTV.text = content
    }

    private fun setupShiftDropDown() {
        shiftPopupWindow = PopupWindow(this)
        shiftPopupWindow?.setEventListener {
            viewModel.scheduleShiftId.value = it.id
            shiftDropDown.setTitle(it.name)
        }
        shiftDropDown.setTitle("-")
        shiftDropDown.setRightImage(R.drawable.ic_black_drop_down)
        shiftDropDown.setPaddingRightImage(resources.getDimensionPixelSize(R.dimen._3sdp))
        shiftDropDown.setClick {
            shiftPopupWindow?.showPopup(shiftDropDown)
        }
        shiftDropDown.isVisible = true
        shiftTV.isVisible = true
        viewModel.scheduleShiftId.value = BaseApplication.sessionManager?.scheduleId
    }

    private fun setupImportantLevelDropDown() {
        importantLevelPopupWindow = PopupWindow(this)
        importantLevelPopupWindow?.setEventListener {
            viewModel.urgencyType.value = it.id
            importantLevelDropDown.setTitle(it.name)
        }
        importantLevelDropDown.setTitle("-")
        importantLevelDropDown.setRightImage(R.drawable.ic_black_drop_down)
        importantLevelDropDown.setPaddingRightImage(resources.getDimensionPixelSize(R.dimen._3sdp))
        importantLevelDropDown.setClick {
            importantLevelPopupWindow?.showPopup(importantLevelDropDown)
        }

        importantLevelPopupWindow?.clearAll()
        val dataDropDown = ArrayList<GeneralModel>()
        dataDropDown.add(GeneralModel(id = IncidentViewModel.URGENCY_YELLOW, name = "Yellow"))
        dataDropDown.add(GeneralModel(id = IncidentViewModel.URGENCY_RED, name = "Red (Urgent)"))
        importantLevelPopupWindow?.addItems(dataDropDown)
    }

    private fun setupEmergencyDropDown() {
        emergencyPopupWindow = PopupWindow(this)
        emergencyPopupWindow?.setEventListener {
            viewModel.updateEmergencyId(it.id)
            emergencyDropDown.setTitle(it.name)
        }
        emergencyDropDown.setTitle("-")
        emergencyDropDown.setRightImage(R.drawable.ic_black_drop_down)
        emergencyDropDown.setPaddingRightImage(resources.getDimensionPixelSize(R.dimen._3sdp))
        emergencyDropDown.setClick {
            emergencyPopupWindow?.showPopup(emergencyDropDown)
        }
    }

    private fun setupInput() {
        locationInputView.setMinLines(2)
        locationInputView.setHint("Lokasi")

        incidentInputView.setMinLines(3)
        incidentInputView.imeiOptions = EditorInfo.IME_ACTION_DONE
        incidentInputView.setMultiLineDone()
        incidentInputView.setHint("Detail")

        titleInputView.setHint("Title")
    }

    private fun setupPicDropDown() {
        picDropDown.setTitle("-")
        picDropDown.setRightImage(R.drawable.ic_black_drop_down)
        picDropDown.setPaddingRightImage(resources.getDimensionPixelSize(R.dimen._3sdp))
        picDropDown.setClick {
            UserBottomSheet("supervisor").showDialog(supportFragmentManager){ selectedUserId, selectedUserName ->
                picDropDown.setTitle(selectedUserName)
                viewModel.updatePicId(selectedUserId)
            }
        }
    }

    private fun setupClientDropDown() {
        clientDropDown.setTitle("-")
        clientDropDown.setRightImage(R.drawable.ic_black_drop_down)
        clientDropDown.setPaddingRightImage(resources.getDimensionPixelSize(R.dimen._3sdp))
        clientDropDown.setClick {
            UserBottomSheet("client").showDialog(supportFragmentManager){ selectedUserId, selectedUserName ->
                clientDropDown.setTitle(selectedUserName)
                viewModel.updateClientId(selectedUserId)
            }
        }
    }

    private fun setupDate() {
        dateDialog = DialogCalender(this)
        incidentDateView.setTitle("-")
        dateDialog?.setupDialog({
            showTimerDialog(it)
        }, DialogCalender.year_day_month2)?.setDisablePreviousDate()

        incidentDateView.setClick {
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
            viewModel.updateDueDate("$it $realHours:$realMinutes:00")
            incidentDateView.setTitle(
                TimeHelper.convertDateText(
                    it,
                    TimeHelper.FORMAT_DATE_TEXT
                ) + " $realHours:$realMinutes:00"
            )
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Create Incident Report")
    }

    private fun registerObserver() {
        viewModel.incidentCategoryResponse.observe(this, {
            it?.apply {
                incidentTypeDialog.setIncidentData(this)
            }
        })
        viewModel.emergencyResponse.observe(this, { response ->
            response?.apply {
                emergencyPopupWindow?.clearAll()
                val dataDropDown = ArrayList<GeneralModel>()
                forEach {
                    dataDropDown.add(GeneralModel(id = it.id, name = it.title))
                }
                emergencyPopupWindow?.addItems(dataDropDown)
            }
        })
        viewModel.postCreateIncidentResponse.observe(this, { response ->
            if (response?.isSuccess() == true) {
                showLongToast(response.message ?: "Incident success created")
                setResult(SUCCESS_POST_INCIDENT_CODE)
                onBackPressed()
            } else {
                showLongRedToast(response?.message ?: "Incident failed created")
            }
        })
        viewModel.currentScheduleResponse.observe(this, { response ->
            shiftPopupWindow?.clearAll()
            val dataDropDown = ArrayList<GeneralModel>()
            response?.data?.schedules?.forEach {
                dataDropDown.add(GeneralModel(id = it.id, name = "${it.shift?.name} ${it.scheduleDate?.date}"))
            }
            shiftPopupWindow?.addItems(dataDropDown)
        })
        viewModel.mediaResponse.observe(this, {
            if (it?.isSuccess() == true) {
                if (imagesView.childCount > 0) {
                    (imagesView.getChildAt(imagesView.childCount - 1) as PictureView).uniqueId =
                        it.data?.image?.id
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
            } catch (exception: Exception) {
                log("exception $exception")
            }
        }
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
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.marginEnd = size10
        val textView = TextView(this)
        textView.layoutParams = layoutParams
        textView.text = model.name
        textView.setPadding(size10, size5, size10, size5)
        textView.setTextColor(pickColor(R.color.black))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size5.toFloat())
        textView.typeface = pickFont(R.font.font_regular)
        textView.setBackgroundResource(R.drawable.bg_black_radius_4)
        return textView
    }

    fun addIncidentType() {
        incidentTypeDialog.showNow(supportFragmentManager, "IncidentTypeDialog")
    }

    fun onUploadImage() {
        hideKeyboard(this)
        if (!EasyPermissions.hasPermissions(this, *cameraPermission)) {
            EasyPermissions.requestPermissions(
                this,
                "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu",
                0,
                *cameraPermission
            )
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

    fun addInvolves() {
        hideKeyboard(this)
        UserBottomSheet().showDialog(supportFragmentManager){ selectedUserId, selectedUserName ->
            addInvolvesView(selectedUserId, selectedUserName)
        }
    }

    fun onResetInvolves() {
        viewModel.clearInvolves()
        involvesTypeView.removeAllViews()
        resetTV.isVisible =false
    }

    private fun addInvolvesView(selectedUserId: String, selectedUserName: String) {
        launch {
            resetTV.isVisible = true
            involvesTypeView.addView(
                getBorderTextView(
                    GeneralModel(
                        id = selectedUserId,
                        name = selectedUserName
                    )
                )
            )
            viewModel.updateInvolves(selectedUserId)
        }
    }

    fun onSave() {
        hideKeyboard(this)

        viewModel.imagesUploadedId.clear()
        for (index in 0 until imagesView.childCount) {
            val pictureView = imagesView.getChildAt(index) as PictureView
            pictureView.uniqueId.takeIf { !it.isNullOrBlank() }?.apply {
                viewModel.imagesUploadedId.add(this)
            }
        }

        viewModel.postCreateIncident()
    }

    companion object {

        const val SUCCESS_POST_INCIDENT_CODE = 319

        fun onNewIntent(context: Context, incidentModel: IncidentModel? = null): Intent {
            val intent = Intent(context, CreateIncidentActivity::class.java)
            intent.putExtra(IncidentViewModel.EXTRA_INCIDENT_MODEL, incidentModel)
            return intent
        }
    }
}