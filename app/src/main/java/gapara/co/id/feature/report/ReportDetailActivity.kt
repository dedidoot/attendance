package gapara.co.id.feature.report

import android.content.Context
import android.content.Intent
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.model.IncidentModel
import gapara.co.id.core.model.ReportModel
import gapara.co.id.databinding.ActivityReportDetailBinding
import gapara.co.id.feature.incident.CreateIncidentActivity
import kotlinx.android.synthetic.main.activity_report_detail.*

class ReportDetailActivity :
    MvvmActivity<ActivityReportDetailBinding, ReportViewModel>(ReportViewModel::class),
    CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_report_detail

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.processIntent(intent)
        registerObserver()
        setupAppBar()
        setupCreateAsIncidentButton()
    }

    private fun registerObserver() {
        viewModel.reportModel.observe(this, {
            it?.apply {
                setupTime(this)
            }
        })
    }

    private fun setupTime(model: ReportModel?) {
       model?.createdAt?.apply {
           val date =  TimeHelper.convertDateText(TimeHelper.getDateServer(this), TimeHelper.FORMAT_DATE_TEXT) +" "+
                   TimeHelper.getHourServer(this)
           timeTextView.text = date
        }
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.setClearImage()
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Report Detail")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_down)
    }

    private fun setupCreateAsIncidentButton() {
        createAsIncidentButton.setOnClickListener {
            val emergencyModel = viewModel.reportModel.value
            val incidentModel = IncidentModel(title = emergencyModel?.title, content = emergencyModel?.content)
            startActivity(CreateIncidentActivity.onNewIntent(this, incidentModel))
        }
    }

    companion object {

        fun onNewIntent(context: Context, model: ReportModel?): Intent {
            val intent = Intent(context, ReportDetailActivity::class.java)
            intent.putExtra(ReportViewModel.EXTRA_REPORT_MODEL, model)
            return intent
        }
    }

}