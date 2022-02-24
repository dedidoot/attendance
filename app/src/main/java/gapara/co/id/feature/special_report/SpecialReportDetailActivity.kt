package gapara.co.id.feature.special_report

import android.content.Context
import android.content.Intent
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.model.MediaModel
import gapara.co.id.core.model.SpecialReportModel
import gapara.co.id.databinding.ActivitySpecialReportDetailBinding
import gapara.co.id.feature.component.PictureView
import kotlinx.android.synthetic.main.activity_special_report_detail.*
import kotlinx.coroutines.launch

class SpecialReportDetailActivity :
    MvvmActivity<ActivitySpecialReportDetailBinding, SpecialReportViewModel>(SpecialReportViewModel::class),
    CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_special_report_detail

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
                setupMedias(items)
            }
        })
    }

    private fun setupTime(model: SpecialReportModel?) {
       timeTextView.text = "${model?.createdAt ?: ""} at ${model?.branch?.id} | ${model?.branch?.name}"
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.setClearImage()
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Special Report Detail")
    }

    private fun setupMedias(medias: ArrayList<MediaModel>?) {
        launch {
            medias?.forEach {
                val pictureView = PictureView(this@SpecialReportDetailActivity)
                pictureView.setClick { startActivity(DisplayImageActivity.onNewIntent(this@SpecialReportDetailActivity, it.url)) }
                pictureView.setUrlImage(it.url)
                imagesView.addView(pictureView)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_down)
    }

    companion object {

        fun onNewIntent(context: Context, model: SpecialReportModel?): Intent {
            val intent = Intent(context, SpecialReportDetailActivity::class.java)
            intent.putExtra(SpecialReportViewModel.EXTRA_REPORT_MODEL, model)
            return intent
        }
    }

}