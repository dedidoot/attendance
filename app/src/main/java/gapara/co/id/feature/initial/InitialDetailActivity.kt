package gapara.co.id.feature.initial

import android.content.Context
import android.content.Intent
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import gapara.co.id.databinding.ActivityInitialDetailBinding
import gapara.co.id.feature.component.card.CardView
import gapara.co.id.feature.component.card.ItemCardView
import kotlinx.android.synthetic.main.activity_initial_detail.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InitialDetailActivity :
    MvvmActivity<ActivityInitialDetailBinding, InitialViewModel>(InitialViewModel::class),
    CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_initial_detail

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.processIntent(intent)
        registerObserver()
        setupAppBar()
    }

    private fun registerObserver() {
        viewModel.reportModel.observe(this, {
            it?.apply {
                setupTime(this)
                setupScheduleView(this.scheduleShift)
            }
        })
    }

    private fun setupTime(model: ReportModel?) {
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
        detailAppBarView.setTitleBar("Report Detail")
    }

    private fun setupScheduleView(model: ScheduleShiftReportModel?) {
        launch {
            val context = this@InitialDetailActivity
            itemScheduleView.removeAllViews()

            if (!model?.guards.isNullOrEmpty()) {
                val guardCardView = CardView(context)
                guardCardView.hideRightImage()
                guardCardView.setTitle("Guard List")
                model?.guards?.forEach { guardModel ->
                    guardCardView.setCardItemView(getItemCardView(guardModel))
                }
                itemScheduleView.addView(guardCardView)
            }

            if (!model?.patrols.isNullOrEmpty()) {
                val patrolCardView = CardView(context)
                patrolCardView.hideRightImage()
                patrolCardView.setTitle("Patrol List")
                model?.patrols?.forEach { patrolModel ->
                    patrolCardView.setCardItemView(getItemCardView(patrolModel))
                }
                itemScheduleView.addView(patrolCardView)
            }
        }
    }

    private fun getItemCardView(patrolModel: GuardReportModel): ItemCardView {
        val itemCardView = ItemCardView(this)
        itemCardView.setRoundText(patrolModel.user?.name ?: "Dummy")
        itemCardView.setRoundUrl(patrolModel.user?.avatar ?: "dummy")
        itemCardView.hideLeftButton()
        itemCardView.hideRightButton()
        itemCardView.hideRight2ImageView()
        return itemCardView
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_down)
    }

    companion object {

        fun onNewIntent(context: Context, model: ReportModel?): Intent {
            val intent = Intent(context, InitialDetailActivity::class.java)
            intent.putExtra(InitialViewModel.EXTRA_INITIAL_MODEL, model)
            return intent
        }
    }
}