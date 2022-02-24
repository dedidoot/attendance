package gapara.co.id.feature.checkpoint

import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import com.google.android.gms.maps.model.LatLng
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.response.CurrentShiftModel
import gapara.co.id.core.base.*
import gapara.co.id.core.model.ReportModel
import gapara.co.id.databinding.ActivityCheckPointDetailBinding
import gapara.co.id.feature.component.card.CardView
import gapara.co.id.feature.component.card.ItemCardView
import gapara.co.id.feature.map.MapsActivity
import kotlinx.android.synthetic.main.activity_check_point_detail.*
import kotlinx.coroutines.launch

class CheckPointDetailActivity :
    MvvmActivity<ActivityCheckPointDetailBinding, CheckPointViewModel>(CheckPointViewModel::class),
    CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_check_point_detail

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.processIntent(intent)
        registerObserver()
        setupAppBar()
    }

    private fun registerObserver() {
        viewModel.reportDetail.observe(this, {
            it?.apply {
                setupTime(this)
                setupItemView(scheduleShift?.checkPoints, rootItemView)
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
        detailAppBarView.setTitleBar("CheckPoint Detail")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_down)
    }

    private fun setupItemView(items: ArrayList<CurrentShiftModel>?, rootView: LinearLayout) {
        launch {
            rootView.removeAllViews()
            val itemView = CardView(this@CheckPointDetailActivity)
            itemView.setTitle("Check Point List")
            itemView.hideRightImage()
            items?.forEach { model ->
                itemView.setCardItemView(getItemCardView(model))
            }
            rootView.addView(itemView)
        }
    }

    private fun getItemCardView(model: CurrentShiftModel): ItemCardView {
        val itemCardView = ItemCardView(this)
        itemCardView.hideLeftButton()
        itemCardView.hideRightButton()

        if (model.isPending()) {
            itemCardView.setShowRight2Button(false)
            itemCardView.hideRight1ImageView()
            itemCardView.hideRight2ImageView()
        } else if (model.isChecked()) {
            itemCardView.setShowRight2Button(false)

            itemCardView.setRight1Image(R.drawable.ic_green_checklist)
            itemCardView.setRight2Image(R.drawable.ic_thumb_image)
            itemCardView.setRight3Image(R.drawable.ic_location)

            itemCardView.setLoadRight2Image(model.attachments?.firstOrNull()?.url)
            itemCardView.setRight2ImageClick { startActivity(DisplayImageActivity.onNewIntent(this, it)) }

            itemCardView.setRight3ImageClick {
                val location = LatLng(model.latitude?.toDoubleOrNull() ?: 0.0, model.longitude?.toDoubleOrNull() ?: 0.0)
                startActivity(MapsActivity.onNewIntent(this, location))
            }

            itemCardView.setShowRight3ImageView(true)
        } else {
            itemCardView.hideRight1ImageView()
            itemCardView.hideRight2ImageView()
            itemCardView.setShowRight3ImageView(false)
        }
        itemCardView.setWidthRoundImageView(1)
        itemCardView.setRoundText(model.name)
        TimeHelper.getHourServer(model.checkedAt).takeIf { !it.isNullOrBlank() && it != "null" }?.apply {
            itemCardView.setRound2Text(this)
        }
        return itemCardView
    }

    companion object {

        fun onNewIntent(context: Context, model: ReportModel?): Intent {
            val intent = Intent(context, CheckPointDetailActivity::class.java)
            intent.putExtra(CheckPointViewModel.EXTRA_CHECK_POINT_DETAIL, model)
            return intent
        }
    }

}