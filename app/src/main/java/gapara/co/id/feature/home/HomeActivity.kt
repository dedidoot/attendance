package gapara.co.id.feature.home

import android.content.Intent
import android.widget.LinearLayout
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.ApiRequest
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.response.BaseResponse
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import gapara.co.id.databinding.ActivityHomeBinding
import gapara.co.id.feature.announcement.AnnouncementDetailActivity
import gapara.co.id.feature.announcement.AnnouncementDialog
import gapara.co.id.feature.announcement.AnnouncementHistoryActivity
import gapara.co.id.feature.brief.BriefDetailActivity
import gapara.co.id.feature.brief.BriefHistoryActivity
import gapara.co.id.feature.checkpoint.LocationCheckPointDetailActivity
import gapara.co.id.feature.component.ReportView
import gapara.co.id.feature.emergency.EmergencyReportActivity
import gapara.co.id.feature.feedback.CreateFeedbackActivity
import gapara.co.id.feature.incident.IncidentActivity
import gapara.co.id.feature.incident.IncidentDetailActivity
import gapara.co.id.feature.initial.InitialActivity
import gapara.co.id.feature.initial.InitialViewModel
import gapara.co.id.feature.intel.IntelListReportActivity
import gapara.co.id.feature.intel.IntelReportActivity
import gapara.co.id.feature.liveattendance.LiveAttendanceActivity
import gapara.co.id.feature.log_book.CreateLogBookActivity
import gapara.co.id.feature.lost_found.CreateLostFoundActivity
import gapara.co.id.feature.report.ReportActivity
import gapara.co.id.feature.report.ReportDetailActivity
import gapara.co.id.feature.schedule.ScheduleActivity
import gapara.co.id.feature.special_report.SpecialReportActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.merge_home_main_menu.*
import kotlinx.coroutines.launch

class HomeActivity : MvvmActivity<ActivityHomeBinding, HomeViewModel>(HomeViewModel::class),
    CoroutineDeclare, SwipeRefreshLayout.OnRefreshListener {

    private val maxItems = 2

    override val layoutResource: Int = R.layout.activity_home

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        setupSwipeRefreshLayout()
        setupAppBar()
        setupClickMenu()
        setupProfile()
        checkVersion(this)
    }

    private fun setupProfile() {
        val session = BaseApplication.sessionManager
        profileImageView.loadUrl(session?.avatar)
        nameTextView.text = session?.fullName ?: "-"
        positionTextView.text = session?.role ?: "-"
        welcomeTextView.text = "Welcome"
    }

    private fun registerObserver() {
        setupChiefObserve()
        setupSupervisorObserve()
        logoutObserve()
        currentScheduleObserve()
        permissionsObserve()
        intelReportObserve()
    }

    private fun intelReportObserve() {
        viewModel.intelReports.observe(this, {
            it?.apply {
                title4DataTextView.isVisible = data?.reports?.items?.isNotEmpty() == true
                root4DataView.isVisible = data?.reports?.items?.isNotEmpty() == true
                root4DataView.removeAllViews()
                title4DataTextView.text = "Intel report"
                data?.reports?.items?.forEachIndexed { index, model ->
                    if (index > maxItems) {
                        return@forEachIndexed
                    }

                    val reportModel = ReportModel(model.id, model.title, model.content, model.createdAt, model.creator)
                    root4DataView.addView(getReportView(index, model.id, model.title, model.createdAt, model.creator?.name, model.content, false) {
                        startActivity(ReportDetailActivity.onNewIntent(this@HomeActivity, reportModel))
                        overridePendingTransition(R.anim.slide_up, R.anim.no_anim)
                    })
                }
            }
        })
    }

    private fun permissionsObserve() {
        viewModel.permissionsResponse.observe(this, {
            it?.apply {
                setupUserRoleMenu()
            }
        })
    }

    private fun currentScheduleObserve() {
        viewModel.currentScheduleResponse.observe(this, {
            if (!it?.data?.schedules?.firstOrNull()?.real_start.isNullOrBlank()) {
                checkInOutButton.text = "Check out"
                checkInOutButton.setBackgroundResource(R.drawable.bg_red_button)
                checkInOutButton.setOnClickListener {
                    BaseDialogView(this).setMessage("Are you sure want to check out?")
                        .showNegativeButton()
                        .setOnClickPositive { patchCheckOut() }
                        .show()
                }
            } else {
                checkInOutButton.text = "Check in"
                checkInOutButton.setBackgroundResource(R.drawable.bg_primary_button)
                checkInOutButton.setOnClickListener {
                    patchCheckIn()
                }
            }
        })
    }

    private fun patchCheckIn() {
        homeSwipeRefreshLayout.isRefreshing = true
        launch {
            val apiRequest = ApiRequest(Urls.PATCH_CHECK_IN + BaseApplication.sessionManager?.scheduleId)
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            if (response?.isSuccess() == true) {
                showLongToast(response.message ?: "Success check in")
            } else {
                showLongRedToast(response?.message ?: "Failed check in")
            }
            startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
            finish()
            overridePendingTransition(0, 0)
        }
    }

    private fun patchCheckOut() {
        homeSwipeRefreshLayout.isRefreshing = true
        launch {
            val apiRequest = ApiRequest(Urls.PATCH_CHECK_OUT + BaseApplication.sessionManager?.scheduleId)
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            if (response?.isSuccess() == true) {
                showLongToast(response.message ?: "Success check out")
            } else {
                showLongRedToast(response?.message ?: "Failed check out")
            }
            startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
            finish()
            overridePendingTransition(0, 0)
        }
    }

    private fun logoutObserve() {
        viewModel.logoutModel.observe(this, {
            homeSwipeRefreshLayout.isRefreshing = false
            if (it?.isSuccess() == true) {
                BaseApplication.baseApplication?.logout()
            } else {
                showLongRedToast("Failed logout, please try again!")
            }
        })
    }

    private fun setupChiefObserve() {
        viewModel.announcementModel.observe(this, {
            it?.apply {
                title1DataTextView.isVisible = !items.isNullOrEmpty()
                title1DataTextView.text = "Announcement history"
                setupItemView(items ?: arrayListOf(), root1DataView)
            }
        })
        viewModel.findingListReport.observe(this, {
            it?.apply {
                title2DataTextView.isVisible =  this.isNotEmpty()
                root2DataView.isVisible = this.isNotEmpty()
                title2DataTextView.text = "Finding report"
                root2DataView.removeAllViews()

                this.forEachIndexed { index, model ->
                    if (index > maxItems) {
                        return@forEachIndexed
                    }

                    root2DataView.addView(getReportView(index, model.id, model.title, model.createdAt, model.creator?.name, model.content, model.isEmergency == true) {
                        startActivity(ReportDetailActivity.onNewIntent(this@HomeActivity, model))
                        overridePendingTransition(R.anim.slide_up, R.anim.no_anim)
                    })
                }
            }
        })
        viewModel.incidentListReport.observe(this, {
            it?.apply {
                title3DataTextView.isVisible = items?.isNotEmpty() == true
                root3DataView.isVisible = items?.isNotEmpty() == true
                root3DataView.removeAllViews()
                title3DataTextView.text = "Incident report"
                items?.forEachIndexed { index, model ->
                    if (index > maxItems) {
                        return@forEachIndexed
                    }

                    root3DataView.addView(getReportView(index + 1, model.id, model.title, model.createdAt, model.creator?.name, model.content, model.isRed()) {
                        startActivityForResult(IncidentDetailActivity.onNewIntent(this@HomeActivity, model), IncidentDetailActivity.INCIDENT_REQUEST_CODE)
                        overridePendingTransition(R.anim.slide_up, R.anim.no_anim)
                    })
                }
            }
        })
    }

    private fun setupSupervisorObserve() {
        viewModel.announcementUnread.observe(this, {
            title1DataTextView.isVisible = !it?.id.isNullOrBlank()
            root1DataView.isVisible = !it?.id.isNullOrBlank()
            it?.apply {
                title1DataTextView.text = "Announcement"
                setupItemView(arrayListOf(this), root1DataView)
                setupAnnouncementDialog(this)
            }
        })
        viewModel.briefItemModel.observe(this, {
            title2DataTextView.isVisible = !it.isNullOrEmpty()
            title2DataTextView.text = "Today brief"
            root2DataView.isVisible = !it.isNullOrEmpty()
            setupBriefItemView(it)
        })

        viewModel.announcementAcceptResponse.observe(this, {
            showAlert(this, it?.message ?: "Failed announcement accept", R.color.green2, Toast.LENGTH_LONG)
            onRefresh()
        })
    }

    private fun setupBriefItemView(models: ArrayList<BriefItemModel>?) {
        launch {
            root2DataView.removeAllViews()
            models?.forEachIndexed { index, model ->
                if (index > maxItems) {
                    return@forEachIndexed
                }

                root2DataView.addView(getReportView(index, model.id, model.title, model.createdAt, model.creator?.name, model.content, false) {
                    startActivity(BriefDetailActivity.onNewIntentWithBriefItemModel(this@HomeActivity, model))
                    overridePendingTransition(R.anim.slide_up, R.anim.no_anim)
                })
            }
        }
    }

    private val announcementDialog = AnnouncementDialog {
        viewModel.patchAnnouncementRead(it.id)
    }

    private fun setupAnnouncementDialog(model: AnnouncementItemModel) {
        if (!isChief() && !model.id.isNullOrBlank()) {
            if (!announcementDialog.isVisible) {
                announcementDialog.setUniqueId(model.id)
                    .setTitle(model.title)
                    .setDescription(model.content).setLevel(model.level)
                announcementDialog.showNow(supportFragmentManager, "")
            }
        } else if (announcementDialog.isVisible) {
            announcementDialog.dismiss()
        }
    }

    private fun getReportView(index: Int, id: String?, title : String?, createdAt : String?, username : String?, content : String?, isEmergency : Boolean, callback : (String?) -> Unit): ReportView {
        val itemView = ReportView(this@HomeActivity)
        itemView.setTitleMaxLine(1)
        itemView.setDescriptionMaxLine(2)
        if (index >= 1) {
            itemView.setMargin(top = resources?.getDimensionPixelSize(R.dimen._10sdp))
        }
        itemView.setTitle(title)

        itemView.setTime(createdAt)
        itemView.setUsername(username)
        itemView.setDescription(content)
        if (isEmergency) {
            itemView.setRightImage(R.drawable.ic_red_urgent)
        }
        itemView.setClick {
            callback(id)
        }
        return itemView
    }

    private fun setupItemView(items: ArrayList<AnnouncementItemModel>, rootView : LinearLayout) {
        launch {
            rootView.removeAllViews()
            items.forEachIndexed { index, model ->
                if (index > maxItems) {
                    return@forEachIndexed
                }

                rootView.addView(getReportView(index, model.id, model.title, model.createdAt, model.creator?.name, model.content, false) {
                    startActivityForResult(AnnouncementDetailActivity.onNewIntent(this@HomeActivity, model), AnnouncementDetailActivity.SUCCESS_ACCEPT_CODE)
                    overridePendingTransition(R.anim.slide_up, R.anim.no_anim)
                })
            }
        }
    }

    private fun getPermissionsData() : ArrayList<GeneralModel> {
        return viewModel.permissionsResponse.value?.data?.permissions ?: arrayListOf()
    }

    private fun setupUserRoleMenu() {
        val permissions = Permissions(getPermissionsData())
        BaseApplication.permissions = permissions

        liveAttendanceMenuView.isVisible = permissions.isDoPresence

        createIntelReportMenuView.isVisible = permissions.isCreateIntelReport
        listIntelReportMenuView.isVisible = permissions.isViewIntelReport
        briefMenuView.isVisible = permissions.isViewBrief
        scheduleMenuView.isVisible = permissions.isViewSchedule
        announceMenuView.isVisible = permissions.isViewAnnouncement
        checkpointMenuView.isVisible = permissions.isViewCheckPoint
        createLostFoundMenuView.isVisible = permissions.isCreateLostAndFoundReport
        logBookMenuView.isVisible = permissions.isCreateLogBookReport
        incidentMenuView.isVisible = permissions.isViewIncident || permissions.isViewIncidentReport

        if (permissions.isViewFindingReport) {
            viewModel.getFindingReport()
        }

        if (permissions.isViewIncidentReport) {
            viewModel.getIncidentReport()
        }

        if (permissions.isViewIntelReport) {
            viewModel.getListReportIntel()
        }

        if (permissions.isViewSchedule) {
            viewModel.getCurrentSchedule {

                checkInOutButton.isVisible = (permissions.isCheckIn || permissions.isCheckOut) && viewModel.currentScheduleResponse.value?.data?.schedules?.firstOrNull()?.users?.find { it.user?.id == BaseApplication.sessionManager?.userId } != null

                if (permissions.isViewAnnouncement) {
                    viewModel.getAnnouncement()
                }
            }
        } else if (permissions.isViewAnnouncement) {
            viewModel.getAnnouncement()
        }

        if (permissions.isViewBriefReport) {
            viewModel.getBriefListToday()
        }
    }

    private fun setupClickMenu() {
        liveAttendanceMenuView.setClickMenu {
            startActivity(LiveAttendanceActivity.onNewIntent(this))
        }
        briefMenuView.setClickMenu {
            startActivity(BriefHistoryActivity.onNewIntent(this))
        }
        initialMenuView.setClickMenu {
            startActivity(InitialActivity.onNewIntent(this, InitialViewModel.EXTRA_INITIAL))
        }
        scheduleMenuView.setClickMenu {
            startActivity(ScheduleActivity.onNewIntent(this))
        }
        announceMenuView.setClickMenu {
            startActivity(AnnouncementHistoryActivity.onNewIntent(this))
        }
        incidentMenuView.setClickMenu {
            startActivity(IncidentActivity.onNewIntent(this))
        }
        emergencyMenuView.setClickMenu {
            startActivity(EmergencyReportActivity.onNewIntent(this))
        }
        checkpointMenuView.setClickMenu {
            startActivity(LocationCheckPointDetailActivity.onNewIntent(this, ""))
        }
        createIntelReportMenuView.setClickMenu {
            startActivity(IntelReportActivity.onNewIntent(this))
        }
        listIntelReportMenuView.setClickMenu {
            startActivity(IntelListReportActivity.onNewIntent(this))
        }
        feedbackMenuView.setClickMenu {
            startActivity(CreateFeedbackActivity.onNewIntent(this))
        }
        reportMenuView.setClickMenu {
            startActivity(ReportActivity.onNewIntent(this))
        }
        specialReportMenuView.setClickMenu {
            startActivity(SpecialReportActivity.onNewIntent(this))
        }
        dropOffMenuView.setClickMenu {
            startActivity(ReportActivity.onNewIntent(this))
        }
        createLostFoundMenuView.setClickMenu {
            startActivity(CreateLostFoundActivity.onNewIntent(this))
        }
        logBookMenuView.setClickMenu {
            startActivity(CreateLogBookActivity.onNewIntent(this))
        }
        homeBottomBar.setClickAccount {
            homeSwipeRefreshLayout.isRefreshing = true
            viewModel.postLogout()
        }
    }

    private fun setupAppBar() {
        if (isIntel()) {
            homeAppBarView.hideRightImage()
        }
        homeAppBarView.setClickRightImage {  }
    }

    private fun setupSwipeRefreshLayout() {
        homeSwipeRefreshLayout.setThemePrimary()
        homeSwipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        viewModel.getPermissions()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AnnouncementDetailActivity.SUCCESS_ACCEPT_CODE && resultCode == AnnouncementDetailActivity.SUCCESS_ACCEPT_CODE) {
            onRefresh()
        } else if (requestCode == IncidentDetailActivity.INCIDENT_REQUEST_CODE && resultCode == IncidentDetailActivity.INCIDENT_RESULT_CODE) {
            onRefresh()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}