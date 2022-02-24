package gapara.co.id.feature.announcement

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import gapara.co.id.BR
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.databinding.ActivityCreateAnnouncementBinding
import gapara.co.id.feature.incident.IncidentTypeDialog
import kotlinx.android.synthetic.main.activity_create_announcement.*
import kotlinx.coroutines.launch

class CreateAnnouncementActivity : MvvmActivity<ActivityCreateAnnouncementBinding, AnnouncementViewModel>(AnnouncementViewModel::class), CoroutineDeclare {

    private val currentScheduleDialog = IncidentTypeDialog {
        setupCurrentShift(it)
    }

    override val layoutResource: Int = R.layout.activity_create_announcement

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        currentScheduleDialog.title = "Schedules"
        registerObserver()
        viewModel.processIntent(intent)
        setupView()
        viewModel.getCurrentSchedule()
    }

    private fun setupView() {
        setupAppBar()
        setupInput()
    }

    private fun setupInput() {
        announcementInputView.setMinLines(3)
        announcementInputView.imeiOptions = EditorInfo.IME_ACTION_DONE
        announcementInputView.setMultiLineDone()
        announcementInputView.setHint("Description")
        titleInputView.setHint("Title")
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Create Announcement")
    }

    private fun registerObserver() {
        viewModel.currentScheduleResponse.observe(this, { response ->
            val datas = ArrayList<GeneralModel>()
            response?.data?.schedules?.forEach {
                datas.add(GeneralModel(it.id, "${it.shift?.name} (${TimeHelper.convertDateText(it.scheduleDate?.date, TimeHelper.FORMAT_DATE_FULL_DAY)})"))
            }
            currentScheduleDialog.setIncidentData(datas)
        })
        viewModel.createAnnouncementApiResponse.observe(this, { response ->
            if (response?.isSuccess() == true) {
                showLongToast(response.message ?: "Success created announcement")
                setResult(CREATE_SUCCESS_CODE)
                onBackPressed()
            } else {
                showLongRedToast(response?.message ?: "Failed create announcement")
            }
        })
    }

    fun onSave() {
        hideKeyboard(this)
        viewModel.onCreateAnnouncement()
    }

    private fun setupCurrentShift(it: ArrayList<GeneralModel>) {
        launch {
            currentScheduleView.removeAllViews()
            val parsingData = ArrayList<GeneralModel>()
            it.forEach { model ->
                if (model.isChecked == true) {
                    parsingData.add(model)
                    currentScheduleView.addView(getBorderTextView(model))
                }
            }
            viewModel.updateCurrentScheduleSelected(parsingData)
        }
    }

    fun addSchedules() {
        currentScheduleDialog.showNow(supportFragmentManager, "current_schedule")
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

    companion object {

        const val CREATE_SUCCESS_CODE = 29

        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, CreateAnnouncementActivity::class.java)
            return intent
        }
    }
}