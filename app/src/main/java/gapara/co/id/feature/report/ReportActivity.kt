package gapara.co.id.feature.report

import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.log
import gapara.co.id.core.base.*
import gapara.co.id.core.base.TimeHelper.FORMAT_DATE_TEXT
import gapara.co.id.core.model.ReportModel
import gapara.co.id.databinding.ActivityReportBinding
import gapara.co.id.feature.component.ReportView
import gapara.co.id.feature.report.CreateReportActivity.Companion.createReportCode
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ReportActivity : MvvmActivity<ActivityReportBinding, ReportViewModel>(ReportViewModel::class), CoroutineDeclare {

    private val calendar = Calendar.getInstance()

    override val layoutResource: Int = R.layout.activity_report

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        showScrollView(false)
        setupAppBar()
        setupFromToDate()
        setupEmergencyList()
        setupLoadMore()
    }

    private fun setupLoadMore() {
        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                    scrollY > oldScrollY) {
                    log("load more")
                    loadReportList()
                }
            }
        })
    }

    private fun setupEmergencyList() {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(calendar.time)
        val realFromDate = TimeHelper.convertDateText(realFromServerDate, FORMAT_DATE_TEXT)
        fromDate.formatServer = realFromServerDate
        fromDate.setTitle(realFromDate)

        val next7Day = TimeHelper.getDate(calendar.get(Calendar.DAY_OF_MONTH) + 7)
        val realEndServerDate = TimeHelper.convertToFormatDateSever(next7Day)
        val realEndDate = TimeHelper.convertDateText(realEndServerDate, FORMAT_DATE_TEXT)
        toDate.formatServer = realEndServerDate
        toDate.setTitle(realEndDate)

        loadReportList()
    }

    private fun showScrollView(isShow: Boolean) {
        nestedScrollView.isVisible = isShow
    }

    private fun setupFromToDate() {
        fromDate.setClick {
            DialogCalender(this).setupDialog({
                fromDate.formatServer = it
                fromDate.setTitle(TimeHelper.convertDateText(it, FORMAT_DATE_TEXT))
                toDate.setTitle("-")
            }, DialogCalender.year_day_month2).showDialog()
        }
        toDate.setClick {
            DialogCalender(this).setupDialog({
                toDate.formatServer = it
                toDate.setTitle(TimeHelper.convertDateText(it, FORMAT_DATE_TEXT))
                resetEmergencyList()
            }, DialogCalender.year_day_month2).showDialog()
        }
    }

    private fun resetEmergencyList() {
        viewModel.reportListPage = 1
        rootItemView.removeAllViews()
        lineView.isVisible = false
        loadReportList()
    }

    private fun setupAppBar() {
        appBarView.setClickLeftImage {
            onBackPressed()
        }
        appBarView.setClickRightImage {}
        appBarView.setTitleBar("Report")
    }

    private fun registerObserver() {
        viewModel.reportResponse.observe(this, {
            showScrollView(true)
            it?.apply {
                setupItemView(this, rootItemView)
            }
        })
    }

    private fun setupItemView(items: ArrayList<ReportModel>, rootView : LinearLayout) {
        launch {
            val size = items.size
            if (viewModel.reportListPage == 1) {
                rootView.removeAllViews()
            }
            items.forEachIndexed { index, model ->
                val itemView = ReportView(this@ReportActivity)
                itemView.setTitleMaxLine(1)
                itemView.setDescriptionMaxLine(2)
                if (index >= 1) {
                    itemView.setMargin(top = resources?.getDimensionPixelSize(R.dimen._10sdp))
                }
                if (index == (size - 1)) {
                    itemView.setMargin(bottom = resources?.getDimensionPixelSize(R.dimen._10sdp))
                }
                itemView.setTitle(model.title)
                itemView.setTime(TimeHelper.timestampToText(model.createdAt))
                itemView.setUsername(model.creator?.name)
                itemView.setDescription(model.content)
                itemView.setClick {
                    openEmergencyDetail(model)
                }
                rootView.addView(itemView)
            }
            lineView.isVisible = rootView.childCount > 0
        }
    }

    fun openCreateReport() {
        startActivityForResult(CreateReportActivity.onNewIntent(this), createReportCode)
    }

    private fun openEmergencyDetail(model: ReportModel) {
        startActivity(ReportDetailActivity.onNewIntent(this, model))
        overridePendingTransition(R.anim.slide_up, R.anim.no_anim)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == createReportCode && resultCode == createReportCode) {
            resetEmergencyList()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun loadReportList() {
        viewModel.getReportList(fromDate.formatServer, toDate.formatServer)
    }

    companion object {

        fun onNewIntent(context: Context): Intent {
            return Intent(context, ReportActivity::class.java)
        }
    }
}