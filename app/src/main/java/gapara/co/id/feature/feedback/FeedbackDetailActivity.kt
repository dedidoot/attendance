package gapara.co.id.feature.feedback

import android.content.Context
import android.content.Intent
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.model.FeedbackModel
import gapara.co.id.databinding.ActivityFeedbackDetailBinding
import kotlinx.android.synthetic.main.activity_feedback_detail.*

class FeedbackDetailActivity :
    MvvmActivity<ActivityFeedbackDetailBinding, FeedbackViewModel>(FeedbackViewModel::class),
    CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_feedback_detail

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.processIntent(intent)
        registerObserver()
        setupAppBar()
    }

    private fun registerObserver() {
        viewModel.feedbackModel.observe(this, {
            it?.apply {
                setupTime(this)
            }
        })
    }

    private fun setupTime(model: FeedbackModel?) {
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
        detailAppBarView.setTitleBar("Feedback Detail")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_down)
    }

    companion object {

        fun onNewIntent(context: Context, model: FeedbackModel?): Intent {
            val intent = Intent(context, FeedbackDetailActivity::class.java)
            intent.putExtra(FeedbackViewModel.EXTRA_FEEDBACK_MODEL, model)
            return intent
        }
    }

}