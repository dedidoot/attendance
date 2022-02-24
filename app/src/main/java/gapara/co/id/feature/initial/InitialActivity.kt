package gapara.co.id.feature.initial

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
import gapara.co.id.databinding.ActivityInitialBinding
import gapara.co.id.feature.component.ReportView
import gapara.co.id.feature.initial.CreateInitialActivity.Companion.createInitialCode
import kotlinx.android.synthetic.main.activity_initial.*
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList

class InitialActivity : MvvmActivity<ActivityInitialBinding, InitialViewModel>(InitialViewModel::class), CoroutineDeclare {

    private val calendar = Calendar.getInstance()

    override val layoutResource: Int = R.layout.activity_initial

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        viewModel.processIntent(intent)
        showScrollView(false)
        setupAppBar()
        setupFromToDate()
        setupInitialList()
        setupLoadMore()
        addView.isVisible = isSupervisor()
    }

    private fun setupLoadMore() {
        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                    scrollY > oldScrollY) {
                    log("load more")
                    loadInitialList()
                }
            }
        })
    }

    private fun setupInitialList() {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(calendar.time)
        val realFromDate = TimeHelper.convertDateText(realFromServerDate, FORMAT_DATE_TEXT)
        fromDate.formatServer = realFromServerDate
        fromDate.setTitle(realFromDate)

        val next7Day = TimeHelper.getDate(calendar.get(Calendar.DAY_OF_MONTH) + 7)
        val realEndServerDate = TimeHelper.convertToFormatDateSever(next7Day)
        val realEndDate = TimeHelper.convertDateText(realEndServerDate, FORMAT_DATE_TEXT)
        toDate.formatServer = realEndServerDate
        toDate.setTitle(realEndDate)

        loadInitialList()
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
                resetInitialList()
            }, DialogCalender.year_day_month2).showDialog()
        }
    }

    private fun resetInitialList() {
        viewModel.initialListPage = 1
        rootItemView.removeAllViews()
        lineView.isVisible = false
        loadInitialList()
    }

    private fun setupAppBar() {
        appBarView.setClickLeftImage {
            onBackPressed()
        }
        appBarView.setClickRightImage {}
        if (viewModel.isFromInitial()) {
            appBarView.setTitleBar("Initial Report")
        } else if (viewModel.isFromIntel()) {
            appBarView.setTitleBar("Intel Report")
        } else {
            appBarView.setTitleBar("Initial Report")
        }
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
            if (viewModel.initialListPage == 1) {
                rootView.removeAllViews()
            }
            items.forEachIndexed { index, model ->
                val itemView = ReportView(this@InitialActivity)
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
                    openInitialDetail(model)
                }
                rootView.addView(itemView)
            }
            lineView.isVisible = rootView.childCount > 0
        }
    }

    fun openCreateInitial() {
        if (!EasyPermissions.hasPermissions(this, *cameraPermission)) {
            EasyPermissions.requestPermissions(this, "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu", 0, *cameraPermission)
        } else {
            startActivityForResult(CreateInitialActivity.onNewIntent(this), createInitialCode)
        }
    }

    private fun openInitialDetail(model: ReportModel) {
        startActivity(InitialDetailActivity.onNewIntent(this, model))
        overridePendingTransition(R.anim.slide_up, R.anim.no_anim)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == createInitialCode && resultCode == createInitialCode) {
            resetInitialList()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun loadInitialList() {
        viewModel.getInitialList(fromDate.formatServer, toDate.formatServer)
    }

    companion object {

        fun onNewIntent(context: Context, extraFrom : String): Intent {
            val intent = Intent(context, InitialActivity::class.java)
            intent.putExtra(InitialViewModel.EXTRA_FROM, extraFrom)
            return intent
        }
    }
}