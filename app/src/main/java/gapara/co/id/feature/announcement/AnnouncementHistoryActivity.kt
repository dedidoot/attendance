package gapara.co.id.feature.announcement

import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.base.TimeHelper.FORMAT_DATE_TEXT
import gapara.co.id.core.model.AnnouncementItemModel
import gapara.co.id.databinding.ActivityAnnouncementHistoryBinding
import gapara.co.id.feature.component.ReportView
import kotlinx.android.synthetic.main.activity_announcement_history.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class AnnouncementHistoryActivity : MvvmActivity<ActivityAnnouncementHistoryBinding, AnnouncementViewModel>(AnnouncementViewModel::class), CoroutineDeclare {

    private val calendar = Calendar.getInstance()
    private val createAnnouncementCode = CreateAnnouncementActivity.CREATE_SUCCESS_CODE

    override val layoutResource: Int = R.layout.activity_announcement_history

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        showScrollView(false)
        setupAppBar()
        setupFromToDate()
        setupAnnouncementHistory()
        addAnnouncementView.isVisible = isChief()
    }

    private fun setupAnnouncementHistory() {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(calendar.time)
        val realFromDate = TimeHelper.convertDateText(realFromServerDate, FORMAT_DATE_TEXT)
        fromDate.setTitle(realFromDate)

        val next7Day = TimeHelper.getDate(calendar.get(Calendar.DAY_OF_MONTH) + 7)
        val realEndServerDate = TimeHelper.convertToFormatDateSever(next7Day)
        val realEndDate = TimeHelper.convertDateText(realEndServerDate, FORMAT_DATE_TEXT)
        toDate.setTitle(realEndDate)

        viewModel.getAnnouncement(realFromServerDate, realEndServerDate)
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
                viewModel.getAnnouncement(fromDate.formatServer, toDate.formatServer)
                rootItemView.removeAllViews()
            }, DialogCalender.year_day_month2).showDialog()
        }
    }

    private fun setupAppBar() {
        announcementAppBarView.setClickLeftImage {
            onBackPressed()
        }
        announcementAppBarView.setClickRightImage {}
        announcementAppBarView.setTitleBar("Announcement History")
    }

    private fun registerObserver() {
        viewModel.announcementModel.observe(this, {
            showScrollView(true)
            it?.apply {
                setupItemView(items ?: arrayListOf(), rootItemView)
            }
        })
    }

    private fun setupItemView(items: ArrayList<AnnouncementItemModel>, rootView : LinearLayout) {
        launch {
            rootView.removeAllViews()
            val size = items.size
            items.forEachIndexed { index, model ->
                val itemView = ReportView(this@AnnouncementHistoryActivity)
                itemView.setTitleMaxLine(1)
                itemView.setDescriptionMaxLine(2)
                if (index >= 1) {
                    itemView.setMargin(top = resources?.getDimensionPixelSize(R.dimen._10sdp))
                }
                if (index == (size - 1)) {
                    itemView.setMargin(bottom = resources?.getDimensionPixelSize(R.dimen._10sdp))
                }
                itemView.setTitle(model.title)
                itemView.setTime(model.createdAt)
                itemView.setUsername(model.creator?.name)
                itemView.setDescription(model.content)
                itemView.setClick {
                    openAnnouncementDetail(model)
                }
                rootView.addView(itemView)
            }
        }
    }

    private fun openAnnouncementDetail(model: AnnouncementItemModel) {
        startActivity(AnnouncementDetailActivity.onNewIntent(this, model))
        overridePendingTransition(R.anim.slide_up, R.anim.no_anim)
    }

    fun openCreateAnnouncement() {
        startActivityForResult(CreateAnnouncementActivity.onNewIntent(this), createAnnouncementCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == createAnnouncementCode && resultCode == createAnnouncementCode) {
            viewModel.getAnnouncement(fromDate.formatServer, toDate.formatServer)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {

        fun onNewIntent(context: Context): Intent {
            return Intent(context, AnnouncementHistoryActivity::class.java)
        }
    }
}