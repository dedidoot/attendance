package gapara.co.id.feature.emergency

import android.content.Context
import android.content.Intent
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.model.EmergencyModel
import gapara.co.id.core.model.IncidentModel
import gapara.co.id.core.model.MediaModel
import gapara.co.id.databinding.ActivityEmergencyDetailBinding
import gapara.co.id.feature.component.PictureView
import gapara.co.id.feature.incident.CreateIncidentActivity
import kotlinx.android.synthetic.main.activity_emergency_detail.*
import kotlinx.coroutines.launch

@Deprecated("move to report detail activity")
class EmergencyDetailActivity :
    MvvmActivity<ActivityEmergencyDetailBinding, EmergencyReportViewModel>(EmergencyReportViewModel::class),
    CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_emergency_detail

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.processIntent(intent)
        registerObserver()
        setupAppBar()
        setupCreateAsIncidentButton()
    }

    private fun setupCreateAsIncidentButton() {
        createAsIncidentButton.setOnClickListener {
            val emergencyModel = viewModel.emergencyModel.value
            val incidentModel = IncidentModel(title = emergencyModel?.title, content = emergencyModel?.content)
            startActivity(CreateIncidentActivity.onNewIntent(this, incidentModel))
        }
        createAsIncidentButton.isVisible = isChief()
    }

    private fun registerObserver() {
        viewModel.emergencyModel.observe(this, {
            it?.apply {
                setupTime(this)
                setupMedias(this.medias)
            }
        })
    }

    private fun setupMedias(medias: ArrayList<MediaModel>?) {
        launch {
            medias?.forEach {
                val pictureView = PictureView(this@EmergencyDetailActivity)
                pictureView.setClick { startActivity(DisplayImageActivity.onNewIntent(this@EmergencyDetailActivity, it.url)) }
                pictureView.setUrlImage(it.url)
                imagesView.addView(pictureView)
            }
        }
    }

    private fun setupTime(model: EmergencyModel?) {
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_down)
    }

    companion object {

        fun onNewIntent(context: Context, model: EmergencyModel?): Intent {
            val intent = Intent(context, EmergencyDetailActivity::class.java)
            intent.putExtra(EmergencyReportViewModel.EXTRA_EMERGENCY_MODEL, model)
            return intent
        }
    }

}