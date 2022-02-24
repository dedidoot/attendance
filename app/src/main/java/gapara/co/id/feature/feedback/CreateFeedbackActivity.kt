package gapara.co.id.feature.feedback

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import gapara.co.id.BR
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.UploadRequest
import gapara.co.id.core.api.log
import gapara.co.id.core.api.response.Media2Response
import gapara.co.id.core.base.*
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.core.model.Urls
import gapara.co.id.databinding.ActivityCreateFeedbackBinding
import kotlinx.android.synthetic.main.activity_create_feedback.*
import kotlinx.coroutines.launch
import java.io.File

class CreateFeedbackActivity : MvvmActivity<ActivityCreateFeedbackBinding, FeedbackViewModel>(FeedbackViewModel::class), CoroutineDeclare {

    private var branchPopupWindow: PopupWindow? = null

    override val layoutResource: Int = R.layout.activity_create_feedback

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        viewModel.processIntent(intent)
        setupView()
        viewModel.getBranchList()
    }

    private fun setupView() {
        setupAppBar()
        setupInput()
        setupTitleDate()
        setupBranchDropDown()
    }

    private fun setupBranchDropDown() {
        branchPopupWindow = PopupWindow(this)
        branchPopupWindow?.setEventListener {
            viewModel.branchId.value = it.id
            branchDropDown.setTitle(it.name)
        }
        branchDropDown.setTitle("-")
        branchDropDown.setRightImage(R.drawable.ic_black_drop_down)
        branchDropDown.setPaddingRightImage(resources.getDimensionPixelSize(R.dimen._3sdp))
        branchDropDown.setClick {
            branchPopupWindow?.showPopup(branchDropDown)
        }
    }

    private fun setupTitleDate() {
        timeTextView.text = TimeHelper.convertDateText(viewModel.dateToday.value, TimeHelper.FORMAT_DATE_TEXT)
    }

    private fun setupInput() {
        emergencyDetailInputView.setMinLines(3)
        emergencyDetailInputView.imeiOptions = EditorInfo.IME_ACTION_DONE
        emergencyDetailInputView.setMultiLineDone()
        emergencyDetailInputView.setHint("Feedback")
        titleInputView.setHint("Name")
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Create Feedback")
    }

    private fun registerObserver() {
        viewModel.createFeedbackApiResponse.observe(this, {
            if (it?.isSuccess() == true) {
                showLongToast("Success create feedback")
                startActivity(onNewIntent(this))
                finish()
                overridePendingTransition(0, 0)
                //setResult(createFeedbackCode)
                //onBackPressed()
            } else {
                showLongToast(it?.message ?: "Failed create feedback")
            }
        })
        viewModel.branchResponse.observe(this, {
            branchPopupWindow?.clearAll()
            branchPopupWindow?.addItems(it?.data?.branches ?: arrayListOf())
        })
    }

    fun onSave() {
        hideKeyboard(this)
        viewModel.onCreateFeedback()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            GalleryHelper.ARG_GALLERY -> {
                try {
                    if (data?.data != null) {
                        val attachmentFile = FileUtil.from(this, data.data)
                        uploadAttachment(attachmentFile)
                    }
                } catch (e : Exception) {
                    BaseApplication.showToast("File not support, please another file!")
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun uploadAttachment(attachmentFile: File?) {
        viewModel.showLoading()
        launch {
            val request = UploadRequest(Urls.UPLOAD_IMAGE_FEEDBACK)
            val files = HashMap<String, File>()
            attachmentFile?.takeIf { it.exists() }?.apply {
                files["image"] = this
            }

            val names = HashMap<String, String>()
            val response= request.upload<Media2Response>(files, names)
            log("response $response ${response?.message}")
            if (response?.isSuccess() == true) {
                viewModel.filesId.add(response.data?.image?.id ?: "")
                filesView.addView(getTitleTextView(attachmentFile?.name ?: ""))
                showLongToast(response.message ?: "Success uploaded")
            } else {
                val message = getErrorMessageServer(response?.errors?.values?.toString()).takeIf { it.isNotBlank() }
                    ?: kotlin.run {
                        response?.message ?: "Failed uploaded"
                    }

                showLongRedToast(message)
            }
            viewModel.showLoading(false)
        }
    }

    private fun getTitleTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.typeface = pickFont(R.font.font_bold)
        textView.setTextColor(pickColor(R.color.black))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, pickSize(R.dimen._6sdp).toFloat())
        val paddingLefRight = pickSize(R.dimen._8sdp)
        val paddingTop = pickSize(R.dimen._8sdp)
        val paddingBottom = pickSize(R.dimen._2sdp)
        textView.setPadding(paddingLefRight, paddingTop, paddingLefRight, paddingBottom)
        return textView
    }

    fun onAttachment() {
        GalleryHelper(this).showGallery()
    }

    companion object {

        const val createFeedbackCode = 323

        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, CreateFeedbackActivity::class.java)
            return intent
        }
    }
}