package gapara.co.id.feature.initial

import android.content.Context
import android.content.Intent
import android.view.inputmethod.EditorInfo
import gapara.co.id.BR
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.model.GuardsModel
import gapara.co.id.databinding.ActivityCreateInitialBinding
import gapara.co.id.feature.component.card.CardView
import gapara.co.id.feature.component.card.ItemCardView
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_create_initial.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

@Deprecated("v2 move to live attendance activity")
class CreateInitialActivity : MvvmActivity<ActivityCreateInitialBinding, InitialViewModel>(InitialViewModel::class), CoroutineDeclare {

    private var presentDialog : PresentDialog? = null

    override val layoutResource: Int = R.layout.activity_create_initial

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        viewModel.processIntent(intent)
        setupView()
        viewModel.getScheduleDate()
    }

    private fun setupView() {
        setupPresentDialog()
        setupAppBar()
        setupInput()
        setupTitleDate()
    }

    private fun setupPresentDialog() {
        presentDialog = PresentDialog { photoFile, locationId ->
            viewModel.presentLocationId.value = locationId
            setupUploadImage(photoFile)
        }
        presentDialog?.getLocationCheckPointList()
    }

    private fun setupTitleDate() {
        timeTextView.text = TimeHelper.convertDateText(viewModel.dateToday.value, TimeHelper.FORMAT_DATE_TEXT)
    }

    private fun setupInput() {
        emergencyDetailInputView.setMinLines(3)
        emergencyDetailInputView.imeiOptions = EditorInfo.IME_ACTION_DONE
        emergencyDetailInputView.setMultiLineDone()
        emergencyDetailInputView.setHint("Description")
        titleInputView.setHint("Title")
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Create Initial Report")
    }

    private fun registerObserver() {
        viewModel.createInitialApiResponse.observe(this, {
            showLongToast(it?.message ?: "Failed create initial report")
            if (it?.isSuccess() == true) {
                setResult(createInitialCode)
                onBackPressed()
            }
        })
        viewModel.presentApiResponse.observe(this, {
            showLongToast(it?.message ?: "Failed present upload")
            if (it?.isSuccess() == true) {
                viewModel.putAsPatrol()
            }
        })
        viewModel.absentApiResponse.observe(this, {
            showLongToast(it?.message ?: "Failed absent upload")
            if (it?.isSuccess() == true) {
                viewModel.getScheduleDate()
            }
        })

        viewModel.scheduleListShiftModel.observe(this, {
            viewModel.getScheduleListAvailableUser()
            setupScheduleView()
        })

        viewModel.putAsPatrolApiResponse.observe(this, {
            if (it?.isSuccess() == true) {
                viewModel.getScheduleDate()
            } else {
                showLongToast(it?.message ?: "Failed set as patrol")
            }
        })
    }

    private fun setupScheduleView() {
        launch {
            val context = this@CreateInitialActivity
            itemScheduleView.removeAllViews()

            if (!viewModel.scheduleListShiftModel.value?.guards.isNullOrEmpty()) {
                val guardCardView = CardView(context)
                guardCardView.hideRightImage()
                guardCardView.setTitle("Guard List")
                viewModel.scheduleListShiftModel.value?.guards?.forEach {
                    guardCardView.setCardItemView(getItemCardView(it))
                    if (!it.replacement?.id.isNullOrBlank()) {
                        val replacementModel = it.replacement
                        guardCardView.setCardItemView(getItemCardView(GuardsModel(replacementModel?.id, replacementModel?.location, replacementModel?.status, replacementModel?.user, presentPhoto = replacementModel?.presentPhoto, attachment = replacementModel?.attachment, isFromReplacementLocal = true)))
                    }
                }
                itemScheduleView.addView(guardCardView)
            }

            if (!viewModel.scheduleListShiftModel.value?.patrols.isNullOrEmpty()) {
                val patrolCardView = CardView(context)
                patrolCardView.hideRightImage()
                patrolCardView.setTitle("Patrol Guard List")
                viewModel.scheduleListShiftModel.value?.patrols?.forEach {
                    patrolCardView.setCardItemView(getItemCardView(it))
                    if (!it.replacement?.id.isNullOrBlank()) {
                        val replacementModel = it.replacement
                        patrolCardView.setCardItemView(getItemCardView(GuardsModel(replacementModel?.id, replacementModel?.location, replacementModel?.status, replacementModel?.user, presentPhoto = replacementModel?.presentPhoto, attachment = replacementModel?.attachment, isFromReplacementLocal = true)))
                    }
                }
                itemScheduleView.addView(patrolCardView)
            }
        }
    }

    private fun getItemCardView(model: GuardsModel): ItemCardView {
        val itemCardView = ItemCardView(this)
        itemCardView.setRoundText(model.user?.name)
        itemCardView.setRoundUrl(model.user?.avatar)
        itemCardView.setRound2Text(model.reason)
        itemCardView.setRight2ImageClick { url ->
            startActivity(DisplayImageActivity.onNewIntent(this, url))
        }

        if (!model.presentPhoto.isNullOrBlank()) {
            itemCardView.setLoadRight2Image(model.presentPhoto)
        } else if (!model.attachment.isNullOrBlank()) {
            itemCardView.setLoadRight2Image(model.attachment)
        } else {
            itemCardView.hideRight2ImageView()
        }

        if (model.isFromReplacementLocal == true) {
            itemCardView.setRight1Image(R.drawable.ic_blue_replacement)
            itemCardView.hideRightButton()
            itemCardView.hideLeftButton()
            itemCardView.setLoadRight2Image(model.presentPhoto)
        } else if (model.isPresent()) {
            itemCardView.setRight1Image(R.drawable.ic_green_checklist)
            itemCardView.hideRightButton()
            itemCardView.hideLeftButton()
        } else if (model.isAbsent()) {
            itemCardView.setRight1Image(R.drawable.ic_red_clear)
            itemCardView.hideRightButton()
            itemCardView.hideLeftButton()
        } else if (model.isPending()) {
            itemCardView.hideRight1ImageView()
            itemCardView.setBackgroundLeftButton(R.drawable.bg_green_button)
            itemCardView.setLeftButtonText("Present")
            itemCardView.setLeftButtonClick {
                presentDialog?.showNow(supportFragmentManager, "${Random.nextInt()}")
                viewModel.userIdPresentAndAbsent.value = model.id
            }
            itemCardView.setBackgroundRightButton(R.drawable.bg_red_button)
            itemCardView.setRightButtonText("Not Present")
            itemCardView.setRightButtonClick {
                viewModel.userIdPresentAndAbsent.value = model.id
                showAbsentDialog()
            }
        }

        return itemCardView
    }

    private fun showAbsentDialog() {
        val dialog = AbsentDialog { photoFile, attachmentFile, replacementUserId, description, reason ->
            //setupPostAbsent(photoFile, attachmentFile, replacementUserId, description, reason)
        }
        dialog.setUserReplacement(viewModel.scheduleAvailableResponse.value)
        dialog.showNow(supportFragmentManager, "${Random.nextInt()}")
    }

    private fun setupPostAbsent(photoFile: File, attachmentFile: File, replacementUserId: String, description: String, reason: String) {
        launch {
            try {
                viewModel.showLoading()
                val compressAttachmentFile = Compressor.compress(this@CreateInitialActivity, attachmentFile) {
                    quality(RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt())
                }

                delay(1000)

                val compressPhotoFile = Compressor.compress(this@CreateInitialActivity, photoFile) {
                    quality(RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt())
                }

                delay(1000)

                viewModel.postAbsent(compressPhotoFile, compressAttachmentFile, replacementUserId, description, reason)
            } catch (exception : Exception) {
                viewModel.showLoading(false)
                exception.printStackTrace()
                BaseApplication.showToast("Image not support, please try another image!")
            }
        }
    }

    fun onSave() {
        hideKeyboard(this)
        viewModel.onCreateInitial()
    }

    private fun setupUploadImage(file: File) {
        launch {
            try {
                viewModel.showLoading()
                val compressedImageFile = Compressor.compress(this@CreateInitialActivity, file) {
                    quality(RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt())
                }

                delay(1000)

                runOnUiThread {
                    viewModel.postPresent(compressedImageFile)
                }
            }  catch (exception : Exception) {
                viewModel.showLoading(false)
                showAlert(this@CreateInitialActivity, "Camera capture file is corrupt, please try again!")
                presentDialog?.dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments = supportFragmentManager.fragments

        for (fragment in fragments) {
            fragment?.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {

        const val createInitialCode = 323

        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, CreateInitialActivity::class.java)
            return intent
        }
    }
}