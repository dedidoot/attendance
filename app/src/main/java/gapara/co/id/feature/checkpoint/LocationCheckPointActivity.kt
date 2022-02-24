package gapara.co.id.feature.checkpoint

import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.response.CheckPointListV2Entity
import gapara.co.id.core.base.*
import gapara.co.id.core.base.location.RequestLocation
import gapara.co.id.databinding.ActivityLocationCheckPointBinding
import gapara.co.id.feature.component.card.CardView
import kotlinx.android.synthetic.main.activity_location_check_point.*
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import kotlin.collections.ArrayList

@Deprecated("move to LocationCheckPointDetailActivity")
class LocationCheckPointActivity : MvvmActivity<ActivityLocationCheckPointBinding, CheckPointViewModel>(CheckPointViewModel::class), CoroutineDeclare {

    override val layoutResource: Int = R.layout.activity_location_check_point

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserver()
        showScrollView(false)
        setupAppBar()
        viewModel.getLocationCheckPointList()
        setupCheckPermission { RequestLocation(this) }
    }

    private fun setupCheckPermission(grantedCallback : () -> Unit) {
        if (!EasyPermissions.hasPermissions(this, *fullPermission)) {
            EasyPermissions.requestPermissions(this, "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu", 0, *fullPermission)
        } else { grantedCallback() }
    }

    private fun showScrollView(isShow: Boolean) {
        nestedScrollView.isVisible = isShow
    }

    private fun setupAppBar() {
        appBarView.setClickLeftImage {
            onBackPressed()
        }
        appBarView.setTitleBar("Checkpoint")
        appBarView.setClickRight2Image { startActivity(CheckPointActivity.onNewIntent(this)) }
    }

    private fun registerObserver() {
        viewModel.checkPointResponse.observe(this, {
            showScrollView(true)
            doneButton.isVisible = !it?.items.isNullOrEmpty()
            it?.apply {
                setupItemView(this.items, rootItemView)
            }
        })
        viewModel.uploadLocationResponse.observe(this, {
            if (it?.isSuccess() == true) {
                viewModel.putMakeCheckPoint()
            } else {
                showLongToast(it?.message ?: "Failed upload location")
            }
        })
        viewModel.makeCheckPointResponse.observe(this, {
            if (it?.isSuccess() == true) {
                showLongToast(it.message ?: "Success make check point location")
                viewModel.getLocationCheckPointList()
            } else {
                showLongToast(it?.message ?: "Failed make check point location")
            }
        })
    }

    private fun setupItemView(items: ArrayList<CheckPointListV2Entity>?, rootView: LinearLayout) {
        launch {
            rootView.removeAllViews()
            items?.forEach { model ->
                val itemView = CardView(this@LocationCheckPointActivity)
                itemView.setTitle(model.name)
                itemView.setRightImage(R.drawable.ic_white_arrow_right)
                itemView.setCardViewClick {
                    startActivity(LocationCheckPointDetailActivity.onNewIntent(this@LocationCheckPointActivity, model.id))
                }
                rootView.addView(itemView)
            }
        }
    }

    fun onDone() {
        BaseDialogView(this)
            .setMessage("Apakah anda yakin ingin menyelesaikan tugas hari ini?")
            .setPositiveString("Lanjutkan")
            .setNegativeString("Tidak")
            .showNegativeButton()
            .setOnClickNegative {  }
            .setOnClickPositive {
                startActivityForResult(CreateCheckPointActivity.onNewIntent(this), CreateCheckPointActivity.createCheckPointCode) }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CreateCheckPointActivity.createCheckPointCode && resultCode == CreateCheckPointActivity.createCheckPointCode) {
            onBackPressed()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    companion object {

        fun onNewIntent(context: Context): Intent {
            return Intent(context, LocationCheckPointActivity::class.java)
        }
    }
}