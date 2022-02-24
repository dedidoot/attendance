package gapara.co.id.feature.brief

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
import gapara.co.id.databinding.ActivityCreateBriefBinding
import gapara.co.id.feature.incident.IncidentTypeDialog
import kotlinx.android.synthetic.main.activity_create_brief.*
import kotlinx.coroutines.launch

class CreateBriefActivity :
    MvvmActivity<ActivityCreateBriefBinding, BriefViewModel>(BriefViewModel::class),
    CoroutineDeclare {

    private val currentScheduleDialog = IncidentTypeDialog {
        setupCurrentShift(it)
    }

    override val layoutResource: Int = R.layout.activity_create_brief

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
        setupBriefInput()
    }

    private fun setupBriefInput() {
        briefInputView.setMinLines(3)
        briefInputView.imeiOptions = EditorInfo.IME_ACTION_DONE
        briefInputView.setMultiLineDone()
        briefInputView.setHint("Description")
    }

    fun addSchedules() {
        currentScheduleDialog.showNow(supportFragmentManager, "current_schedule")
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Create Brief")
    }

    private fun registerObserver() {
        viewModel.currentScheduleResponse.observe(this, { response ->
            val datas = ArrayList<GeneralModel>()
            response?.data?.schedules?.forEach {
                datas.add(GeneralModel(it.id, "${it.shift?.name} (${TimeHelper.convertDateText(it.scheduleDate?.date, TimeHelper.FORMAT_DATE_FULL_DAY)})"))
            }
            currentScheduleDialog.setIncidentData(datas)
        })
        viewModel.createBriefApiResponse.observe(this, { response ->
            if (response?.isSuccess() == true) {
                showLongToast(response.message ?: "Success created brief")
                setResult(RESULT_OK)
                finish()
            } else {
                showLongRedToast(response?.message ?: "Failed create brief")
            }
        })
    }

    fun onSaveBrief() {
        hideKeyboard(this)
        viewModel.onSaveBrief()
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

        fun onNewIntent(context: Context, date: String): Intent {
            val intent = Intent(context, CreateBriefActivity::class.java)
            intent.putExtra(BriefViewModel.EXTRA_DATE, date)
            return intent
        }
    }
}