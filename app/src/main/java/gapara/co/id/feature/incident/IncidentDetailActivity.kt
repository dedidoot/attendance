package gapara.co.id.feature.incident

import android.content.Context
import android.content.Intent
import android.widget.Toast
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import gapara.co.id.databinding.ActivityIncidentDetailBinding
import gapara.co.id.feature.component.CameraBottomSheet
import gapara.co.id.feature.component.FollowUpView
import gapara.co.id.feature.component.PictureView
import gapara.co.id.feature.component.card.CardView
import gapara.co.id.feature.component.card.ItemCardView
import kotlinx.android.synthetic.main.activity_incident_detail.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.collections.ArrayList

class IncidentDetailActivity :
    MvvmActivity<ActivityIncidentDetailBinding, IncidentViewModel>(IncidentViewModel::class),
    CoroutineDeclare {

    private var isNeedRefreshPageBefore = false

    override val layoutResource: Int = R.layout.activity_incident_detail

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.processIntent(intent)
        registerObserver()
        setupAppBar()
    }

    private fun registerObserver() {
        viewModel.incidentDetailModel.observe(this, {
            it?.apply {
                setupCreatedAt(this.createdAt)
                setupDueDate(this)
                //setupApprovalUserView(this.approvals)
                setupProvesImage(this.images)
                statusTextView.text = StatusModel(this.status).getRealStatus()
                setupFollowUp(this.followUps)
                setupUrgency(this)
                setupApproveButton(this)
                commentBtn.text = "Comments (${this.comments?.size ?: 0})"
            }
        })

        viewModel.incidentRequestCompleteResponse.observe(this, {
            showLongToast(it?.message ?: "Success request to complete")
            val parsingModel = viewModel.incidentDetailModel.value
            parsingModel?.status = IncidentModel.REQUEST_COMPLETE
            setupApproveButton(parsingModel)
        })
        viewModel.uploadSupervisorIncidentProofResponse.observe(this, {
            if (it?.isSuccess() == true) {
                viewModel.getIncidentRequestComplete()
            } else {
                showAlert(this, it?.message ?: "Failed upload incident proof")
            }
        })
        viewModel.incidentApproveForChiefApiResponse.observe(this, {
            if (it?.isSuccess() == true) {
                showLongToast(it.message ?: "Success approve incident report")
                val parsingModel = viewModel.incidentDetailModel.value
                parsingModel?.status = IncidentModel.REQUEST_COMPLETE
                setupApproveButton(parsingModel)
            } else {
                showLongToast(it?.message ?: "Failed approve incident report")
            }
        })
        viewModel.incidentApproveForClientApiResponse.observe(this, {
            if (it?.isSuccess() == true) {
                showLongToast(it.message ?: "Success approve incident report")
            } else {
                showLongToast(it?.message ?: "Failed accept incident report")
            }
        })
    }

    private fun setupUrgency(model: IncidentModel) {
        if (model.isRed()) {
            urgencyImageView.setImageResource(R.drawable.ic_red_urgent)
        }
    }

    private fun setupFollowUp(followUps: ArrayList<FollowUpModel>?) {
        setShowFollowUpView(followUps?.isNotEmpty() == true)
        followUps?.forEach {
            followUpView.addView(getFollowUpItemView(it))
        }
    }

    private fun setShowFollowUpView(isShow : Boolean) {
        followUpView.isVisible = BaseApplication.permissions?.isCreateFollowUpIncident == true
        actionFollowUpTextView.isVisible = isShow
    }

    private fun getFollowUpItemView(it: FollowUpModel): FollowUpView {
        val itemView = FollowUpView(this)
        itemView.setTitle(it.description)
        itemView.setTime(TimeHelper.convertDateText(it.action_at, TimeHelper.FORMAT_DATE_FULL_TEXT, TimeHelper.FORMAT_DATE_FULL))
        itemView.setImageUrl(it.image) {
            startActivity(DisplayImageActivity.onNewIntent(this, it.image))
        }
        return itemView
    }

    private fun setupProvesImage(proves: ArrayList<MediaModel>?) {
        launch {
            proves?.forEach {
                val pictureView = PictureView(this@IncidentDetailActivity)
                pictureView.setClick { startActivity(DisplayImageActivity.onNewIntent(this@IncidentDetailActivity, it.image)) }
                pictureView.setUrlImage(it.image)
                imagesView.addView(pictureView)
            }
        }
    }

    private fun setupCreatedAt(createdAt: String?) {
        timeTextView.isVisible = !createdAt.isNullOrBlank()
        timeTextView.text = createdAt
    }

    private fun setupDueDate(model: IncidentModel?) {
        dueDateTextView.setDueDateColor(model?.deadline, model?.isPending(), model?.isComplete(), model?.isRequestComplete())

        model?.deadline?.apply {
           val date = "Deadline\n"+ TimeHelper.convertDateText(this, TimeHelper.FORMAT_DATE_FULL_TEXT, TimeHelper.FORMAT_DATE_FULL)
           dueDateTextView.text = date
        }
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.setClearImage()
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Incident Detail")
    }

    override fun onBackPressed() {
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_down)
        if (isNeedRefreshPageBefore) {
            setResult(INCIDENT_RESULT_CODE)
        }
        super.onBackPressed()
    }

    fun onFollowUpButton() {
        val dialog = FollowUpBottomSheet()
        dialog.showDialog(supportFragmentManager) { file, description ->
            setProcessFollowUp(file, description) {
                if (it) {
                    dialog.dismiss()
                } else {
                    dialog.setFailed()
                }
            }
        }
    }

    fun onUpdateStatus() {
        val dialog = UpdateStatusBottomSheet()
        dialog.setStatus(viewModel.incidentDetailModel.value?.status)
        dialog.showDialog(supportFragmentManager) { statusId ->
            setProcessStatusUpdate(statusId) {
                if (it) {
                    isNeedRefreshPageBefore = true
                    viewModel.incidentDetailModel.value?.status = statusId
                    statusTextView.text = StatusModel(statusId).getRealStatus()
                    dialog.dismiss()
                } else {
                    dialog.setFailed()
                }
            }
        }
    }

    private fun setProcessFollowUp(file: File?, description: String, callback : (Boolean) -> Unit) {
        val incident2ViewModel = Incident2ViewModel()
        incident2ViewModel.postIncidentFollowUpResponse.observe(this, {
            callback(it?.isSuccess() == true)
            if (it?.isSuccess() == true) {
                showAlert(this,  "Success incident follow up", R.color.green2, Toast.LENGTH_LONG)
                val date = TimeHelper.getToday(TimeHelper.FORMAT_DATE_FULL_TEXT)
                followUpView.addView(getFollowUpItemView(FollowUpModel(description, file?.absolutePath, date)), 0)
                setShowFollowUpView(true)
                isNeedRefreshPageBefore = true
            } else {
                showLongRedToast(it?.message ?: "Failed incident follow up!")
            }
        })
        incident2ViewModel.postIncidentFollowUp(file, viewModel.incidentDetailModel.value?.id, description)
    }

    private fun setProcessStatusUpdate(statusId: String, callback : (Boolean) -> Unit) {
        val incident2ViewModel = Incident2ViewModel()
        incident2ViewModel.postStatusUpdateResponse.observe(this, {
            callback(it?.isSuccess() == true)
            if (it?.isSuccess() == true) {
                showAlert(this, "Status updated", R.color.green2, Toast.LENGTH_LONG)
            } else {
                showLongRedToast(it?.message ?: "Failed status update!")
            }
        })

        if (statusId.isNotBlank() && viewModel.isStatusIdValid(statusId)) {
            incident2ViewModel.postStatusUpdate(viewModel.incidentDetailModel.value?.id, statusId)
        } else {
            callback(false)
        }
    }

    private fun setupApprovalUserView(approvals: ArrayList<ApprovalModel>?) {
        launch {
            scheduleViews.removeAllViews()
            val cardView = CardView(this@IncidentDetailActivity)
            if (viewModel.incidentDetailModel.value?.comments.isNullOrEmpty()) {
                cardView.setRightImage(R.drawable.ic_comment_white_dot)
            }
            cardView.setTitle("Approval")
            cardView.setRightImageClick { openCommentDetail() }
            approvals?.forEach {
                cardView.setCardItemView(getItemCardView(it))
            }
            delay(500)
            scheduleViews.addView(cardView)
        }
    }

    private fun getItemCardView(model: ApprovalModel?): ItemCardView {
        val itemCardView = ItemCardView(this)
        itemCardView.setRoundText(model?.approval?.name)
        itemCardView.setRoundUrl(model?.approval?.avatar)
        itemCardView.hideLeftButton()
        itemCardView.hideRightButton()
        itemCardView.hideRight2ImageView()
        if (model?.isPending() == true) {
            itemCardView.setAlphaItemCardView()
            itemCardView.hideRight1ImageView()
        }
        return itemCardView
    }

    private fun openCommentDetail() {
        startActivity(CommentDetailActivity.onNewIntent(this, viewModel.incidentDetailModel.value))
        overridePendingTransition(R.anim.slide_up, R.anim.no_anim)
    }

    private fun setupApproveButton(model: IncidentModel?) {
        if (model?.isRequestComplete() == true) {
            approveButton.text = "Request Complete"
        }

        val newModel = model?.approvals?.find { it.approval?.id == BaseApplication.sessionManager?.userId }

        if (newModel?.id.isNullOrBlank() && (isChief() || isSupervisor() || isClient())) {
            approveButton.isVisible = true
        } else {
            approveButton.isVisible = !newModel?.id.isNullOrBlank() && model?.isRequestComplete() == true && newModel?.isPending() == true
        }

        if (newModel?.isApproved() == true) {
            statusIncidentTextView.text = "Waiting for Client's Approval"
            val splitDate = model.dueDate?.split(" ")
            statusDateIncidentTextView.text = "${TimeHelper.convertDateText(splitDate?.firstOrNull(), TimeHelper.FORMAT_DATE_TEXT)} ${splitDate?.lastOrNull()}"
        } else {
            statusIncidentTextView.isVisible = false
            statusDateIncidentTextView.isVisible = false
        }

        var countPending = 0
        model?.approvals?.forEach {
            if (it.isPending()) {
                countPending += 1
            }
        }

        if (countPending == 0) {
            approveButton.isVisible = false
            statusIncidentTextView.isVisible = false
            statusDateIncidentTextView.isVisible = false
        }
    }

    fun onRequestToComplete() {
        if (isSupervisor()) {
            CameraBottomSheet().showDialog(supportFragmentManager) {
                viewModel.uploadIncidentProof(it)
            }
        } else if (isChief()) {
            viewModel.incidentApproveForChief()
        } else if (isClient()) {
            viewModel.incidentApproveForClient()
        }
    }

    fun onCommentDetail() {
        openCommentDetail()
    }

    companion object {

        const val INCIDENT_REQUEST_CODE = 131
        const val INCIDENT_RESULT_CODE = 132

        fun onNewIntent(context: Context, model: IncidentModel?): Intent {
            val intent = Intent(context, IncidentDetailActivity::class.java)
            intent.putExtra(IncidentViewModel.EXTRA_INCIDENT_MODEL, model)
            return intent
        }
    }
}