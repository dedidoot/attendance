package gapara.co.id.feature.checkpoint

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import gapara.co.id.R
import gapara.co.id.core.api.*
import gapara.co.id.core.api.response.*
import gapara.co.id.core.base.*
import gapara.co.id.core.base.BaseApplication.Companion.showToast
import gapara.co.id.core.model.Urls
import gapara.co.id.feature.component.CameraBottomSheet
import gapara.co.id.feature.component.card.ItemV2CardView
import kotlinx.android.synthetic.main.activity_location_check_point_detail.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class LocationCheckPointDetailActivity : AppCompatActivity(), CoroutineDeclare {

    private var locationId = ""
    private var checkPointId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_check_point_detail)

        intent?.getStringExtra(EXTRA_LOCATION_ID).takeIf { !it.isNullOrBlank() }?.let {
            locationId = it
        } ?: kotlin.run {
            //showLongRedToast("Location id not found")
        }

        setupAppBar()
        getLocationList()
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Check point")
    }

    private fun getLocationList() {
        loading.isVisible = true
        launch {
            val request = GetRequest(Urls.GET_CHECK_POINT_TASK + BaseApplication.sessionManager?.scheduleId)
            request.queries["location_id"] = locationId
            val response= request.get<CheckPointV2Response>()
            setupCheckPointView(response?.data?.tasks?.items)
            if (response?.data?.tasks?.items.isNullOrEmpty()) {
                showToast(response?.message ?: "No data")
            } else {
                addView.isVisible = true
            }
            loading.isVisible = false
        }
    }

    private fun setupCheckPointView(items: ArrayList<TaskEntity>?) {
        launch {
            locationsView.removeAllViews()
            items?.forEach {
                val itemView = getItemCardView(it)
                locationsView.addView(itemView)
            }
        }
    }

    private fun getItemCardView(model: TaskEntity): ItemV2CardView {
        val itemCardView = ItemV2CardView(this)
        itemCardView.setIsShowRound(false)
        itemCardView.setTitleTv(model.check_point?.name)
        itemCardView.setSubtitleTv(model.check_point?.location?.name+"\n"+model.check_point?.location?.description)

        if (!model.check_point_photo.isNullOrBlank()) {
            itemCardView.setRight1Image(R.drawable.ic_thumb_image) {
                startActivity(DisplayImageActivity.onNewIntent(this, model.check_point_photo))
            }
        }

        if (model.status?.equals("pending", ignoreCase = true) == true) {
            itemCardView.setLeftButton("Check Point", R.drawable.bg_green_button) {
                checkPointId = model.check_point_id ?: ""
                CameraBottomSheet().showDialog(supportFragmentManager) {
                    uploadCheckPoint(it)
                }
            }
        } else if (model.status?.equals("checked", ignoreCase = true) == true) {
            itemCardView.setRight2Image(R.drawable.ic_location)
        }

        return itemCardView
    }

    private fun uploadCheckPoint(photoFile: File) {
        launch {
            try {
                loading.isVisible = true

                val request = UploadRequest(Urls.UPLOAD_CHECK_POINT + checkPointId)
                val files = HashMap<String, File>()
                files["photo"] = photoFile

                val names = HashMap<String, String>()
                val response = request.upload<BaseResponse>(files, names)

                if (response?.isSuccess() == true) {
                    showLongToast("Success check point")
                    startActivity(onNewIntent(this@LocationCheckPointDetailActivity, locationId))
                    finish()
                    overridePendingTransition(0, 0)
                } else {
                    val message =
                        getErrorMessageServer(response?.errors?.values?.toString()).takeIf { it.isNotBlank() }
                            ?: kotlin.run {
                                response?.message ?: "Failed check point"
                            }

                    showLongRedToast(message)
                    loading.isVisible = false
                }
            } catch (exception: Exception) {
                loading.isVisible = false
                log("why $exception ${exception.message}")
                showAlert(
                    this@LocationCheckPointDetailActivity,
                    "Camera capture file is corrupt, please try again!"
                )
            }
        }
    }

    fun onCreateReport(view : View) {
        startActivity(CreateCheckPointActivity.onNewIntent(this))
    }

    companion object {

        const val EXTRA_LOCATION_ID = "extra_location_id"

        fun onNewIntent(context: Context, locationId: String?): Intent {
            val intent = Intent(context, LocationCheckPointDetailActivity::class.java)
            intent.putExtra(EXTRA_LOCATION_ID, locationId)
            return intent
        }
    }
}