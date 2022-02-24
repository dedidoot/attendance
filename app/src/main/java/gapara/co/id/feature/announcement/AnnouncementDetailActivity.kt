package gapara.co.id.feature.announcement

import android.content.Context
import android.content.Intent
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.response.CurrentScheduleEntity
import gapara.co.id.core.api.response.UserCurrentScheduleEntity
import gapara.co.id.core.base.*
import gapara.co.id.core.model.AnnouncementItemModel
import gapara.co.id.databinding.ActivityAnnouncementDetailBinding
import gapara.co.id.feature.component.UserView
import kotlinx.android.synthetic.main.activity_announcement_detail.*
import kotlin.collections.ArrayList

class AnnouncementDetailActivity :
    MvvmActivity<ActivityAnnouncementDetailBinding, AnnouncementViewModel>(AnnouncementViewModel::class),
    CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_announcement_detail

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.processIntent(intent)
        registerObserver()
        setupAppBar()
    }

    private fun registerObserver() {
        viewModel.announcementItem.observe(this, {
            it?.apply {
                timeTextView.text = this.createdAt
                setupUserView(this.schedules ?: arrayListOf())
            }
        })
        viewModel.acceptAnnouncementApiResponse.observe(this, {
            if (it?.isSuccess() == true) {
                showLongToast(it.message ?: "Success accept announcement")
                setResult(SUCCESS_ACCEPT_CODE)
                onBackPressed()
            } else {
                showLongToast(it?.message ?: "Failed accept announcement")
            }
        })
        viewModel.announcementReadResponse.observe(this, {
            if (it?.isSuccess() == true) {
                showLongToast(it.message ?: "Success read announcement")
                setResult(SUCCESS_ACCEPT_CODE)
                onBackPressed()
            } else {
                showLongToast(it?.message ?: "Failed read announcement")
            }
        })
    }

    private fun setupUserView(model: ArrayList<CurrentScheduleEntity>) {
        userView.removeAllViews()
        model.forEach { schedule ->
            schedule.users?.forEach { user ->
                userView.addView(getUserView(user, schedule))
            }
        }
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.setClearImage()
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Announcement Detail")
    }

    private fun getUserView(model: UserCurrentScheduleEntity, schedule: CurrentScheduleEntity): UserView {
        val itemView = UserView(this@AnnouncementDetailActivity)
        itemView.setUrl(model.user?.avatar)

        val name = model.user?.name
        val shiftName = TimeHelper.convertDateText(schedule.scheduleDate?.date, TimeHelper.FORMAT_DATE_FULL_DAY)+" (${schedule.shift?.name})"

        val spannable = SpannableStringBuilder(name+"\n"+shiftName)
        var color = pickColor(R.color.black)

        if (!schedule.announcement_read_at.isNullOrBlank()) {
            itemView.setRightImage(R.drawable.ic_green_checklist)
            itemView.setTextColor(R.color.blue2)
        } else {
            itemView.setAlphaImage(0.5.toFloat())
            itemView.setRightImage(R.drawable.ic_grey_checklist)
            itemView.setTextColor(R.color.blue3)
            color = pickColor(R.color.grey1)
        }

        spannable.setSpan(ForegroundColorSpan(color), spannable.indexOf(shiftName), spannable.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemView.setUsernameWithDescription(spannable)
        return itemView
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_down)
    }

    fun onAccept() {
        viewModel.patchAnnouncementRead()
    }

    companion object {

        const val SUCCESS_ACCEPT_CODE = 314

        fun onNewIntent(context: Context, model: AnnouncementItemModel?): Intent {
            val intent = Intent(context, AnnouncementDetailActivity::class.java)
            intent.putExtra(AnnouncementViewModel.EXTRA_ANNOUNCEMENT_MODEL, model)
            return intent
        }
    }
}