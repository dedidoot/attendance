package gapara.co.id.feature.feedback

import android.content.Intent
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.GetRequest
import gapara.co.id.core.api.PostRequest
import gapara.co.id.core.api.response.*
import gapara.co.id.core.base.BaseViewModel
import gapara.co.id.core.base.TimeHelper
import gapara.co.id.core.base.mutableLiveDataOf
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class FeedbackViewModel : BaseViewModel() , CoroutineDeclare {

    val feedbackResponse = mutableLiveDataOf<ArrayList<FeedbackModel>>()
    val feedbackModel = mutableLiveDataOf<FeedbackModel>()
    val dateToday = mutableLiveDataOf<String>()
    val feedbackTitle = mutableLiveDataOf<String>()
    val feedbackDescription = mutableLiveDataOf<String>()
    val createFeedbackApiResponse = mutableLiveDataOf<BaseResponse>()
    val phone = mutableLiveDataOf<String>()
    val email = mutableLiveDataOf<String>()
    var feedbackListPage = 1
    var filesId = ArrayList<String>()
    val branchResponse = mutableLiveDataOf<BranchResponse>()
    val branchId = mutableLiveDataOf<String>()

    init {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(Calendar.getInstance().time)
        dateToday.value = realFromServerDate
    }

    fun processIntent(intent: Intent?) {
        feedbackModel.value = intent?.getParcelableExtra(EXTRA_FEEDBACK_MODEL)
    }

    fun getFeedbackList(startDate : String? = null, endDate : String? = null) {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_CLIENT_FEEDBACK_LIST)
            startDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["start_date"] = this
            }
            endDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["end_date"] = this
            }
            request.queries["page"] = "$feedbackListPage"
            val response= request.get<FeedbackResponse>()
            if (response?.isSuccess() == true) {
                feedbackResponse.value = response.data?.feedback?.items
            }
            if (response?.data?.feedback?.items.isNullOrEmpty()) {
                showToast("No data")
            } else {
                feedbackListPage += 1
            }
            showLoading(false)
        }
    }

    fun onCreateFeedback() {
        if (feedbackTitle.value.isNullOrBlank()) {
            showToast("Name cannot empty")
            return
        }

        if (feedbackDescription.value.isNullOrBlank()) {
            showToast("Description cannot empty")
            return
        }

        showLoading()
        launch {
            val model = FeedBackEntity(feedbackTitle.value, feedbackDescription.value, phone.value, email.value, filesId, branchId.value)
            val response = PostRequest<FeedBackEntity>(Urls.POST_CREATE_FEEDBACK).post<BaseResponse>(model)
            createFeedbackApiResponse.value = response
            showLoading(false)
        }
    }

    fun getBranchList() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_BRANCH_LIST)
            val response = request.get<BranchResponse>()
            branchResponse.value = response
            showLoading(false)
        }
    }

    companion object {
        const val EXTRA_FEEDBACK_MODEL = "extra_feedback_model"
    }
}