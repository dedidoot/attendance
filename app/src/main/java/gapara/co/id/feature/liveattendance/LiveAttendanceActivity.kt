package gapara.co.id.feature.liveattendance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gapara.co.id.R
import gapara.co.id.core.api.*
import gapara.co.id.core.api.response.BaseResponse
import gapara.co.id.core.api.response.CurrentScheduleUsersResponse
import gapara.co.id.core.api.response.UserCurrentScheduleEntity
import gapara.co.id.core.api.response.UserResponse
import gapara.co.id.core.base.*
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.core.model.GuardsModel
import gapara.co.id.core.model.Urls
import gapara.co.id.core.model.UserModel
import gapara.co.id.feature.component.CameraBottomSheet
import gapara.co.id.feature.component.card.CardView
import gapara.co.id.feature.component.card.ItemV2CardView
import gapara.co.id.feature.initial.AbsentDialog
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_live_attendance.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class LiveAttendanceActivity : AppCompatActivity(), CoroutineDeclare {

    var userIdPresentAndAbsent = ""
    var usersModel = ArrayList<UserModel>()
    private var replacementPopupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_attendance)
        setupAppBar()
        setupReplacementDropDown()
        val date = TimeHelper.convertToFormatDateSever(
            Calendar.getInstance().time,
            TimeHelper.FORMAT_DATE_FULL_DAY
        )
        dateTextView.text = date

        getScheduleUsers()
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.hideRightImage()
        detailAppBarView.setTitleBar("Live Attendance")
    }

    fun getScheduleUsers() {
        loading.isVisible = true
        launch {
            val request = GetRequest(Urls.GET_SCHEDULE_USER)
            request.queries["schedule_id"] = BaseApplication.sessionManager?.scheduleId ?: ""
            val response= request.get<CurrentScheduleUsersResponse>()
            setupScheduleView(response?.data?.users)
            loading.isVisible = false
            getUser()
        }
    }

    private fun setupScheduleView(users: ArrayList<UserCurrentScheduleEntity>?) {
        launch {
            val context = this@LiveAttendanceActivity
            itemScheduleView.removeAllViews()

            val guardCardView = CardView(context)
            guardCardView.hideRightImage()
            guardCardView.setTitle("Users")

            users?.forEach { user ->
                val it = GuardsModel(id = user.id, status = user.status, presentPhoto = user.presence_photo, present_at = user.present_at, attachment = user.attachment, reason = user.reason, replacement = user.replacement, replaced = user.replaced)
                it.user = user.user
                guardCardView.setCardV2ItemView(getItemCardView(it))
            }

            itemScheduleView.addView(guardCardView)
        }
    }

    private fun getItemCardView(model: GuardsModel): ItemV2CardView {
        val itemCardView = ItemV2CardView(this)
        itemCardView.setTitleTv(model.user?.name)
        itemCardView.setRoundUrl(model.user?.avatar)
        itemCardView.setSubtitleTv(model.user?.role?.name+" • "+model.status)

        if (!model.presentPhoto.isNullOrBlank()) {
            itemCardView.setRight1Image(R.drawable.ic_thumb_image) {
                startActivity(DisplayImageActivity.onNewIntent(this, model.presentPhoto, model.reason))
            }
        }

        if (!model.attachment.isNullOrBlank()) {
            itemCardView.setRight2Image(R.drawable.ic_thumb_image) {
                startActivity(DisplayImageActivity.onNewIntent(this, model.attachment, model.reason))
            }
        }

        if (model.replacement != null) {
            itemCardView.setColorSubtitleTV(R.color.green1)
            itemCardView.setRight3Image(R.drawable.ic_blue_replacement) {
                startActivity(DisplayImageActivity.onNewIntent(this, model.presentPhoto, model.reason))
            }
            if (model.user?.role?.isGuard() == true) {
                itemCardView.setTriTitleBT("Set patrol") {
                    userIdPresentAndAbsent = model.id ?:""
                    patchAsPatrol()
                }
            } else if (model.user?.role?.isPatrol() == true) {
                itemCardView.setTriTitleBT("Remove patrol") {
                    userIdPresentAndAbsent = model.id ?:""
                    patchRemovePatrol()
                }
            }
        } else if (model.isPresent()) {
            itemCardView.setColorSubtitleTV(R.color.green1)
            if (model.user?.role?.isGuard() == true) {
                itemCardView.setTriTitleBT("Set patrol") {
                    userIdPresentAndAbsent = model.id ?:""
                    patchAsPatrol()
                }
            } else if (model.user?.role?.isPatrol() == true) {
                itemCardView.setTriTitleBT("Remove patrol") {
                    userIdPresentAndAbsent = model.id ?:""
                    patchRemovePatrol()
                }
            }
        } else if (model.isAbsent()) {
            itemCardView.setSubtitleTv(model.user?.role?.name+" • "+model.status)
            itemCardView.setColorSubtitleTV(R.color.red1)
            if (model.replaced == null) {
                itemCardView.setTriTitleBT("Replacement") {
                    userIdPresentAndAbsent = model.id ?:""
                    replacementPopupWindow?.clearAll()
                    val models = ArrayList<GeneralModel>()
                    usersModel.forEach {
                        models.add(GeneralModel(it.id, it.name))
                    }
                    replacementPopupWindow?.addItems(models)
                    replacementPopupWindow?.setEventListener { userReplacementId ->
                        CameraBottomSheet().showDialog(supportFragmentManager) {
                            postPresent(it, model.id, Urls.POST_REPLACEMENT_PRESENCE, "present", userReplacementId.id)
                        }
                    }
                    replacementPopupWindow?.showPopup(itemCardView)
                }
            }
        } else if (model.isPending()) {
            itemCardView.setLeftButton("Present", R.drawable.bg_green_button) {
                userIdPresentAndAbsent = model.id ?: ""
                val isSelfie = model.user?.role?.isSupervisor() == true
                CameraBottomSheet(isSelfie).showDialog(supportFragmentManager) {
                    postPresent(it, model.id, Urls.POST_SCHEDULE_PRESENCE, "present")
                }
            }

            itemCardView.setRightButton("Not Present", R.drawable.bg_red_button) {
                userIdPresentAndAbsent = model.id ?: ""
                showAbsentDialog()
            }
        }

        return itemCardView
    }

    fun postPresent(photoFile: File?, userScu : String?, url : String, status: String, userReplacementId:String?=null, reason:String?=null, attachment:File?=null) {
        loading.isVisible = true
        launch {
            val request = UploadRequest(url)
            val files = HashMap<String, File>()
            photoFile?.takeIf { it.exists() }?.apply {
                files["presence_photo"] = this
            }
            attachment?.takeIf { it.exists() }?.apply {
                files["attachment"] = this
            }

            val names = HashMap<String, String>()
            userScu.takeIf { !it.isNullOrBlank() }?.apply {
                names["id"] = this
            }
            userReplacementId.takeIf { !it.isNullOrBlank() }?.apply {
                names["user_id"] = this
            }
            reason.takeIf { !it.isNullOrBlank() }?.apply {
                names["reason"] = this
            }

            names["status"] = status

            val response= request.upload<BaseResponse>(files, names)

            if (response?.isSuccess() == true) {
                showLongToast(response.message ?: "Success presence")
                startActivity(onNewIntent(this@LiveAttendanceActivity))
                finish()
                overridePendingTransition(0, 0)
            } else {
                val message = getErrorMessageServer(response?.errors?.values?.toString()).takeIf { it.isNotBlank() }
                    ?: kotlin.run {
                    response?.message ?: "Failed presence"
                }

                showLongRedToast(message)
                loading.isVisible = false
            }
        }
    }

    fun patchAsPatrol() {
        loading.isVisible = true
        launch {
            val apiRequest = ApiRequest(Urls.PATCH_AS_PATROL+userIdPresentAndAbsent)
            apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            loading.isVisible = false
            startActivity(onNewIntent(this@LiveAttendanceActivity))
            finish()
            overridePendingTransition(0, 0)
        }
    }

    fun patchRemovePatrol() {
        loading.isVisible = true
        launch {
            val apiRequest = ApiRequest(Urls.PATCH_REMOVE_PATROL+userIdPresentAndAbsent)
            apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            loading.isVisible = false
            startActivity(onNewIntent(this@LiveAttendanceActivity))
            finish()
            overridePendingTransition(0, 0)
        }
    }

    private fun showAbsentDialog() {
        val dialog = AbsentDialog { photoFile, attachmentFile, replacementUserId, reason, typeAbsentId ->
            launch {
                try {
                    loading.isVisible = true
                    var compressedImageFile : File?=null
                    if (photoFile?.exists() == true) {
                         compressedImageFile =
                            Compressor.compress(this@LiveAttendanceActivity, photoFile) {
                                quality(
                                    RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS)
                                        .toInt()
                                )
                            }
                    }

                    delay(1000)

                    runOnUiThread {
                        if (typeAbsentId.equals("replacement", ignoreCase = true)) {
                            postPresent(compressedImageFile, userIdPresentAndAbsent, Urls.POST_REPLACEMENT_PRESENCE, "present", replacementUserId)
                        } else {
                            postPresent(compressedImageFile, userIdPresentAndAbsent, Urls.POST_SCHEDULE_PRESENCE, typeAbsentId, reason = reason, attachment = attachmentFile)
                        }
                    }
                } catch (exception: Exception) {
                    loading.isVisible = false
                    showAlert(
                        this@LiveAttendanceActivity,
                        "Camera capture file is corrupt, please try again!"
                    )
                }
            }
        }
        dialog.setUserReplacement(usersModel)
        dialog.showNow(supportFragmentManager, "${Random.nextInt()}")
    }

    fun getUser() {
        launch {
            val request = GetRequest(Urls.GET_USERS_LIST)
            request.queries["branch_id"] = BaseApplication.sessionManager?.branchId ?: ""
            val response= request.get<UserResponse>()
            if (response?.isSuccess() == true) {
                usersModel = response.data?.users?.items ?: arrayListOf()
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

    private fun setupReplacementDropDown() {
        replacementPopupWindow = PopupWindow(this)
    }

    companion object {

        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, LiveAttendanceActivity::class.java)
            return intent
        }
    }
}