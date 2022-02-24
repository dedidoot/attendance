package gapara.co.id.feature.brief

import android.content.Context
import android.content.Intent
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import gapara.co.id.databinding.ActivityBriefDetailBinding
import gapara.co.id.feature.component.ShiftTabLayoutView
import gapara.co.id.feature.component.UserView
import kotlinx.android.synthetic.main.activity_brief_detail.*
import kotlinx.coroutines.launch

class BriefDetailActivity :
    MvvmActivity<ActivityBriefDetailBinding, BriefViewModel>(BriefViewModel::class),
    CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_brief_detail

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.processIntent(intent)
        showScrollView(false)
        registerObserver()
        setupAppBar()
        setupBriefDetailSuperVisor()
    }

    private fun showScrollView(isShow: Boolean) {
        scrollView.isVisible = isShow
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.setClearImage()
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Brief Detail")
    }

    private fun registerObserver() {
        viewModel.date.observe(this, {
            it?.apply {
                viewModel.getScheduleDate(it)
            }
        })
        viewModel.scheduleModel.observe(this, {
            showScrollView(true)
            it?.apply {
                setupShiftTabView(this)
            }
        })
        viewModel.acceptBriefApiResponse.observe(this, {
            if (it?.isSuccess() == true) {
                showLongToast(it.message ?: "Success accept brief")
                setResult(ACCEPT_SUCCESS_CODE)
                onBackPressed()
            } else {
                showLongToast(it?.message ?: "Failed accept brief")
            }
        })
        viewModel.briefItemModel.observe(this, {
            it?.apply {
                setupBriefDetailView(this)
            }
        })
    }

    private fun setupBriefDetailView(model: BriefItemModel) {
        contentBriefTextView.text = "${model.title}\n${model.content}"
        scheduleDateTextView.text = model.createdAt
        launch {
            userView.removeAllViews()
            model.schedule?.users?.forEach {
                val status = if (model.readAt.isNullOrBlank()) { GuardsModel.PENDING } else { GuardsModel.APPROVED }
                userView.addView(getUserView(GuardsModel(status = status, user = UserModel(it.user?.id, name = it.user?.name, avatar = it.user?.avatar))))
            }
        }
        showScrollView(true)
    }

    private fun setupShiftTabView(model: ScheduleShiftModel) {
        scheduleDateTextView.text = TimeHelper.convertDateText(model.date, TimeHelper.FORMAT_DATE_TEXT)
        launch {
            shiftTabView.removeAllViews()
            model.shifts?.forEachIndexed { index, model ->
                val itemView = ShiftTabLayoutView(this@BriefDetailActivity)
                if (index == 0) {
                    itemView.setTabBackground(R.drawable.bg_primary_button)
                    setupBriefView(model)
                } else {
                    itemView.setTabBackground(0)
                }
                itemView.setClick {
                    resetBackgroundTabShift()
                    itemView.setTabBackground(R.drawable.bg_primary_button)
                    setupBriefView(model)
                }
                itemView.setTitle(model.shift?.name)
                shiftTabView.addView(itemView)
            }
        }
    }

    private fun setupBriefView(model: ScheduleListShiftModel) {
        contentBriefTextView.text = model.brief?.content ?: ""
        launch {
            userView.removeAllViews()
            model.guards?.forEach {
                userView.addView(getUserView(it))
            }
            model.patrols?.forEach {
                userView.addView(getUserView(it))
            }
        }
    }

    private fun getUserView(model: GuardsModel): UserView {
        val itemView = UserView(this@BriefDetailActivity)
        itemView.setUrl(model.user?.avatar)
        itemView.setUsername(model.user?.name)
        if (model.isPending()) {
            itemView.setAlphaImage(0.5.toFloat())
            itemView.setRightImage(R.drawable.ic_grey_clock)
            itemView.setTextColor(R.color.blue3)
        } else {
            itemView.setRightImage(R.drawable.ic_green_checklist)
            itemView.setTextColor(R.color.blue2)
        }
        return itemView
    }

    private fun resetBackgroundTabShift() {
        val count = shiftTabView.childCount
        for (index in 0 until count) {
            if (shiftTabView.getChildAt(index) is ShiftTabLayoutView) {
                (shiftTabView.getChildAt(index) as ShiftTabLayoutView).setTabBackground(0)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_down)
    }

    fun onAccept() {
        viewModel.getBriefAccept()
    }

    private fun setupBriefDetailSuperVisor() {
        val userId = BaseApplication.sessionManager?.userId
        //acceptButton.isVisible = isSupervisor() && viewModel.briefItemModel.value?.spvs?.find { it.id == userId }?.briefReadAt.isNullOrBlank()
    }

    companion object {

        const val ACCEPT_SUCCESS_CODE = 3561

        fun onNewIntentWithBriefItemModel(context: Context, model: BriefItemModel?): Intent {
            val intent = Intent(context, BriefDetailActivity::class.java)
            intent.putExtra(BriefViewModel.EXTRA_BRIEF_ITEM_MODEL, model)
            return intent
        }
    }

}