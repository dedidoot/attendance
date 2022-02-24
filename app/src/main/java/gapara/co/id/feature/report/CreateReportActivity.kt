package gapara.co.id.feature.report

import android.content.Context
import android.content.Intent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import gapara.co.id.BR
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.databinding.ActivityCreateReportBinding
import kotlinx.android.synthetic.main.activity_create_report.*

class CreateReportActivity : MvvmActivity<ActivityCreateReportBinding, ReportViewModel>(ReportViewModel::class), CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_create_report

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
        detailAppBarView.setTitleBar("Create Report")
    }

    private fun registerObserver() {
        viewModel.createReportApiResponse.observe(this, {
            showAlert(this, it?.message ?: "Failed create report", R.color.green2, Toast.LENGTH_LONG)
            if (it?.isSuccess() == true) {
                setResult(createReportCode)
                onBackPressed()
            }
        })
    }

    fun onSave() {
        hideKeyboard(this)
        viewModel.onCreateReport()
    }

    companion object {

        const val createReportCode = 323

        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, CreateReportActivity::class.java)
            return intent
        }
    }
}