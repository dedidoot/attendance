package gapara.co.id.feature.brief

import android.content.Context
import android.content.Intent
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.base.TimeHelper.FORMAT_DATE_TEXT
import gapara.co.id.core.model.BriefItemModel
import gapara.co.id.databinding.ActivityBriefHistoryBinding
import gapara.co.id.feature.component.ItemDateView
import kotlinx.android.synthetic.main.activity_brief_history.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class BriefHistoryActivity : MvvmActivity<ActivityBriefHistoryBinding, BriefViewModel>(BriefViewModel::class), CoroutineDeclare {

    private val calendar = Calendar.getInstance()

    override val layoutResource: Int = R.layout.activity_brief_history

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        showScrollView(false)
        setupAppBar()
        setupFromToDate()
        setupBriefHistory()
        createBriefButton.isVisible = isChief()
    }

    private fun setupBriefHistory() {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(calendar.time)
        val realFromDate = TimeHelper.convertDateText(realFromServerDate, FORMAT_DATE_TEXT)
        fromDate.formatServer = realFromServerDate
        fromDate.setTitle(realFromDate)

        val next7Day = TimeHelper.getDate(calendar.get(Calendar.DAY_OF_MONTH) + 7)
        val realEndServerDate = TimeHelper.convertToFormatDateSever(next7Day)
        val realEndDate = TimeHelper.convertDateText(realEndServerDate, FORMAT_DATE_TEXT)
        toDate.formatServer = realEndServerDate
        toDate.setTitle(realEndDate)

        viewModel.getBriefHistory(realFromServerDate, realEndServerDate)
    }

    private fun showScrollView(isShow: Boolean) {
        scrollView.isVisible = isShow
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
                viewModel.getBriefHistory(fromDate.formatServer, toDate.formatServer)
                rootDateView.removeAllViews()
            }, DialogCalender.year_day_month2).showDialog()
        }
    }

    private fun setupAppBar() {
        briefAppBarView.setClickLeftImage {
            onBackPressed()
        }
        briefAppBarView.setClickRightImage {}
        briefAppBarView.setTitleBar("Brief History")
    }

    private fun registerObserver() {
        viewModel.briefModel.observe(this, {
            showScrollView(true)
            it?.apply {
                setupItemDateView(this.briefs ?: arrayListOf())
            }
        })
    }

    private fun setupItemDateView(briefs: ArrayList<BriefItemModel>) {
        launch {
            rootDateView.removeAllViews()
            val size = briefs.size - 1
            lineView.isVisible = briefs.size > 0
            briefs.forEachIndexed { index, model ->
                val itemView = ItemDateView(this@BriefHistoryActivity)
                itemView.setTitle(model.title)
                itemView.setSubtitle(model.schedule?.shift?.name, TimeHelper.convertDateText(model.schedule?.scheduleDate?.date, TimeHelper.FORMAT_DATE_FULL_DAY))
                itemView.disableEllipsize()
                itemView.setRightImage(R.drawable.ic_black_arrow_right)
                if (index >= 1) {
                    itemView.setMargin(top = resources?.getDimensionPixelSize(R.dimen._10sdp))
                }
                if (index == size) {
                    itemView.setMargin(bottom = resources?.getDimensionPixelSize(R.dimen._10sdp))
                }
                itemView.setClick {
                    openBriefDetail(model)
                }
                rootDateView.addView(itemView)
            }
        }
    }

    private fun openBriefDetail(model: BriefItemModel) {
        startActivityForResult(BriefDetailActivity.onNewIntentWithBriefItemModel(this, model), BriefDetailActivity.ACCEPT_SUCCESS_CODE)
        overridePendingTransition(R.anim.slide_up, R.anim.no_anim)
    }

    fun openCreateBrief() {
        val todayServerDate = TimeHelper.convertToFormatDateSever(calendar.time)
        startActivityForResult(CreateBriefActivity.onNewIntent(this, todayServerDate), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            viewModel.getBriefHistory(fromDate.formatServer, toDate.formatServer)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {

        fun onNewIntent(context: Context): Intent {
            return Intent(context, BriefHistoryActivity::class.java)
        }
    }

}