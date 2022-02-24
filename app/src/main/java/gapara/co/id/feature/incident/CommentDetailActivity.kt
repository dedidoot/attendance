package gapara.co.id.feature.incident

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.log
import gapara.co.id.core.base.*
import gapara.co.id.core.model.CommentModel
import gapara.co.id.core.model.IncidentModel
import gapara.co.id.core.model.UserCommentator
import gapara.co.id.core.model.UserModel
import gapara.co.id.databinding.ActivityCommentDetailBinding
import gapara.co.id.feature.component.UserView
import kotlinx.android.synthetic.main.activity_comment_detail.*
import kotlinx.coroutines.launch
import java.util.*

class CommentDetailActivity :
    MvvmActivity<ActivityCommentDetailBinding, IncidentViewModel>(IncidentViewModel::class),
    CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_comment_detail

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.processIntent(intent)
        registerObserver()
        setupAppBar()
        setupInput()
        commentView.isVisible = BaseApplication.permissions?.isCommentIncident == true
    }

    private fun setupUserCommentView(comments: ArrayList<CommentModel>) {
        val context = this@CommentDetailActivity
        val dimenLeft = resources.getDimensionPixelSize(R.dimen._25sdp)
        val dimenRight = resources.getDimensionPixelSize(R.dimen._10sdp)
        val dimenBottom = resources.getDimensionPixelSize(R.dimen._10sdp)
        val widthFullScreen = widthScreenSize(context)
        val colorLine = pickColor(R.color.grey7)
        viewModel.showLoading()

        launch {
            userCommentView.removeAllViews()
            comments.forEach { parentCommentModel ->
                userCommentView.addView(getUserView(parentCommentModel))

                val rootUserLinearLayout = LinearLayout(context)
                rootUserLinearLayout.orientation = LinearLayout.VERTICAL
                rootUserLinearLayout.setPadding(dimenLeft, 0, dimenRight, dimenBottom)

                parentCommentModel.subComments?.forEach { subCommentModel ->
                    rootUserLinearLayout.addView(getUserView(CommentModel(subCommentModel.id, subCommentModel.comment, subCommentModel.createdAt )))
                }

                userCommentView.addView(rootUserLinearLayout)

                val lineView = View(context)
                lineView.minimumWidth = widthFullScreen
                lineView.minimumHeight = 1
                lineView.setBackgroundColor(colorLine)

                userCommentView.addView(lineView)
            }

            viewModel.showLoading(false)
        }
    }

    private fun getUserView(model : CommentModel): UserView {
        val rootUserView = UserView(this@CommentDetailActivity)
        rootUserView.setUrl(model.commentator?.avatar)
        val username = model.commentator?.name ?: ""
        val spannable = SpannableStringBuilder("$username ${model.comment}")

        spannable.setSpan(ForegroundColorSpan(pickColor(R.color.colorPrimaryDark)), 0, username.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, username.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(ForegroundColorSpan(pickColor(R.color.black)), username.length, spannable.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        rootUserView.setUsernameWithDescription(spannable, Integer.MAX_VALUE)
        rootUserView.hideRightImage()
        rootUserView.hideLineView()

        rootUserView.setTime(model.createdAt)
        rootUserView.setIsShowReply(BaseApplication.permissions?.isCommentIncident == true)
        rootUserView.setClickReply {
            showKeyboardFocusEditText(commentInputView.getInputEditText())
            viewModel.updateParentCommentId(model.id)
        }
        return rootUserView
    }

    private fun setupInput() {
        commentInputView.setMaxLines(3)
        commentInputView.imeiOptions = EditorInfo.IME_ACTION_DONE
        commentInputView.setMultiLineDone()

        val username = "${BaseApplication.sessionManager?.fullName}..."
        val text = "Add a comment as "
        val spannable = SpannableStringBuilder("$text$username")
        spannable.setSpan(ForegroundColorSpan(pickColor(R.color.black)), 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val indexBold = spannable.toString().indexOf(username)
        spannable.setSpan(StyleSpan(android.graphics.Typeface.BOLD), indexBold, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        commentInputView.setHintSpannable(spannable)
        profileImageView.loadUrl(BaseApplication.sessionManager?.avatar)
    }

    private fun registerObserver() {
        viewModel.incidentDetailModel.observe(this, {
            setupUserCommentView(it.comments ?: arrayListOf())
        })
        viewModel.postCreateIncidentCommentResponse.observe(this, {
            if (it?.isSuccess() == true) {
                showLongToast(it.message ?: "Success create comment")
                addCommentView()
            } else {
                showLongToast(it?.message ?: "Failed create comment")
            }
        })
    }

    private fun addCommentView() {
        val comment = CommentModel(comment = viewModel.commentText.value)
        comment.commentator = UserCommentator(name = BaseApplication.sessionManager?.fullName, avatar = BaseApplication.sessionManager?.avatar)

        val commentModel = viewModel.incidentDetailModel.value?.comments ?: arrayListOf()
        commentModel.add(comment)
        setupUserCommentView(commentModel)
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.setClearImage()
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Comments")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_down)
    }

    fun onTextCommentChanged(editable : Editable) {
        if (editable.isNotBlank()) {
            sendTextView.setTextColor(pickColor(R.color.colorPrimaryDark))
        } else {
            sendTextView.setTextColor(pickColor(R.color.grey1))
        }
    }

    fun onSendComment() {
        if (!viewModel.commentText.value.isNullOrBlank()) {
            hideKeyboard(this)
        }
        viewModel.postCreateIncidentComment()
        log("text ${viewModel.commentText.value}")
    }

    companion object {

        fun onNewIntent(context: Context, model: IncidentModel?): Intent {
            val intent = Intent(context, CommentDetailActivity::class.java)
            intent.putExtra(IncidentViewModel.EXTRA_INCIDENT_MODEL, model)
            return intent
        }
    }

}