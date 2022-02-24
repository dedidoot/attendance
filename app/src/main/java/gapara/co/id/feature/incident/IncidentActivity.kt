package gapara.co.id.feature.incident

import android.content.Context
import android.content.Intent
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.base.*
import gapara.co.id.core.model.IncidentModel
import gapara.co.id.databinding.ActivityIncidentBinding
import gapara.co.id.feature.incident.CreateIncidentActivity.Companion.SUCCESS_POST_INCIDENT_CODE
import kotlinx.android.synthetic.main.activity_incident.*

class IncidentActivity : MvvmActivity<ActivityIncidentBinding, IncidentViewModel>(IncidentViewModel::class) {

    private var incidentProgressAdapter : IncidentAdapter? = null
    private var incidentCompleteAdapter : IncidentAdapter? = null

    override val layoutResource: Int = R.layout.activity_incident

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        setupAppBar()
        setupTabView()
        setupProgressAdapter()
        setupCompleteAdapter()
        viewModel.getIncidentProgress()
        createIncidentFab.isVisible = BaseApplication.permissions?.isCreateIncidentReport == true
    }

    private fun setupProgressAdapter() {
        incidentProgressAdapter = IncidentAdapter(this, arrayListOf()) {
            viewModel.getIncidentProgress()
        }
        incidentProgressAdapter?.setupEventListener {
            openIncidentDetail(it)
        }
        progressRecyclerView.adapter = incidentProgressAdapter
    }

    private fun setupCompleteAdapter () {
        incidentCompleteAdapter = IncidentAdapter(this, arrayListOf()) {
            viewModel.getIncidentComplete()
        }
        incidentCompleteAdapter?.setupEventListener {
            openIncidentDetail(it)
        }
        completeRecyclerView.adapter = incidentCompleteAdapter
    }

    private fun openIncidentDetail(it: IncidentModel) {
        startActivityForResult(IncidentDetailActivity.onNewIntent(this, it), IncidentDetailActivity.INCIDENT_REQUEST_CODE)
        overridePendingTransition(R.anim.slide_up, R.anim.no_anim)
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.setClearImage()
        detailAppBarView.setClickRightImage {  }
        detailAppBarView.setTitleBar("Incident")
    }

    private fun registerObserver() {
        viewModel.incidentProgressResponse.observe(this, {
            incidentProgressAdapter?.needToLoadMore = !it.isNullOrEmpty()
            incidentProgressAdapter?.addItems(it ?: arrayListOf())
        })

        viewModel.incidentCompleteResponse.observe(this, {
            incidentCompleteAdapter?.needToLoadMore = !it.isNullOrEmpty()
            incidentCompleteAdapter?.addItems(it ?: arrayListOf())
        })
    }

    private fun setupTabView() {
        onProgressTabView.setTitle("ON PROGRESS")
        onProgressTabView.setWidthMatchParentTab()
        onProgressTabView.setTabBackground(R.drawable.bg_primary_button)
        onProgressTabView.setClick {
            onProgressSelected()
        }

        completeTabView.setTitle("COMPLETE")
        completeTabView.setWidthMatchParentTab()
        completeTabView.setTabBackground(0)
        completeTabView.setClick {
            completeTabView.setTabBackground(R.drawable.bg_primary_button)
            onProgressTabView.setTabBackground(0)
            progressRecyclerView.isVisible = false
            completeRecyclerView.isVisible = true
            if (incidentCompleteAdapter?.itemCount == 0) {
                viewModel.getIncidentComplete()
            }
        }
    }

    private fun onProgressSelected() {
        onProgressTabView.setTabBackground(R.drawable.bg_primary_button)
        completeTabView.setTabBackground(0)
        progressRecyclerView.isVisible = true
        completeRecyclerView.isVisible = false
    }

    fun onCreateIncident() {
        startActivityForResult(CreateIncidentActivity.onNewIntent(this), SUCCESS_POST_INCIDENT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SUCCESS_POST_INCIDENT_CODE && resultCode == SUCCESS_POST_INCIDENT_CODE) {
            checkRefreshData()
        } else if (requestCode == IncidentDetailActivity.INCIDENT_REQUEST_CODE && resultCode == IncidentDetailActivity.INCIDENT_RESULT_CODE) {
            checkRefreshData()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkRefreshData() {
        incidentProgressAdapter?.clearAll()
        viewModel.resetProgressData()
        onProgressSelected()
    }

    companion object {
        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, IncidentActivity::class.java)
            return intent
        }
    }
}