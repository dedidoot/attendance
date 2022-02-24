package gapara.co.id.feature.initial

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.GSONManager
import gapara.co.id.core.base.*
import gapara.co.id.core.model.RemoteConfigModel
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.core.model.UserModel
import kotlinx.android.synthetic.main.dialog_absent.*
import java.io.File

class AbsentDialog(private val eventClick: (File?, File?, String, String, String) -> Unit) :
    DialogFragment(), CoroutineDeclare {

    private var typePopupWindow: PopupWindow? = null
    private var replacementPopupWindow: PopupWindow? = null
    private var users = ArrayList<UserModel>()

    private var attachmentFile : File? = null
    private var replacementUserId : String? = null
    private var typeAbsentId: String? = null

    override fun getTheme(): Int {
        return R.style.Dialog2
    }

    fun setUserReplacement(users : ArrayList<UserModel>?) {
        this.users = users ?: arrayListOf()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_absent, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupCameraView()
        setupTypeDropDown()
        setupReplacementDropDown()
        setupDescriptionInputView()
        addAttachmentImageView.setOnClickListener {
            GalleryHelper(requireContext()).showGallery()
        }
        takePhotoButton.setOnClickListener {
            showCamera()
        }
    }

    private fun setupDescriptionInputView() {
        reasonInputView.setMinLines(3)
        reasonInputView.imeiOptions = EditorInfo.IME_ACTION_DONE
        reasonInputView.setMultiLineDone()
        reasonInputView.setHint("Reason")
    }

    private fun setupTypeDropDown() {
        typePopupWindow = PopupWindow(requireContext())
        typeView.setTitle("-")
        typeView.setRightImage(R.drawable.ic_black_drop_down)
        typeView.setPaddingRightImage(resources.getDimensionPixelSize(R.dimen._3sdp))
        typeView.setClick {
            typePopupWindow?.clearAll()
            val typeAbsent = RemoteConfigHelper.get(RemoteConfigKey.TYPE_ABSENT)
            val model = GSONManager.fromJson(typeAbsent, RemoteConfigModel::class.java)
            typePopupWindow?.addItems(model.data ?: arrayListOf())
            typePopupWindow?.setEventListener {
                typeView.setTitle(it.name)
                typeAbsentId= it.id

                val isReplacement = it.id?.equals("replacement", ignoreCase = true) == true
                val isLeaveWithPermission = it.id?.equals("leave-with-permission", ignoreCase = true) == true
                val isSick = it.id?.equals("sick", ignoreCase = true) == true
                val isPaidLeave = it.id?.equals("paid-leave", ignoreCase = true) == true

                replacementTextView.isVisible = isReplacement
                replacementView.isVisible = isReplacement

                attachmentTextView.isVisible = isReplacement.not() || isSick
                attachmentView.isVisible = isReplacement.not() || isSick
                reasonTextView.isVisible = isReplacement.not() || isSick
                reasonInputView.isVisible = isReplacement.not() || isSick

                if (isReplacement) {
                    takePhotoButton.text = "Take a photo"
                    takePhotoButton.setOnClickListener {
                        showCamera()
                    }
                } else {
                    takePhotoButton.text = "Submit"
                    takePhotoButton.setOnClickListener {
                        eventClick(null, attachmentFile, replacementUserId ?: "", reasonInputView.inputValue, typeAbsentId ?: "")
                        dismiss()
                    }
                }
            }
            typePopupWindow?.showPopup(typeView)
        }
    }

    private fun setupReplacementDropDown() {
        replacementPopupWindow = PopupWindow(requireContext())
        replacementView.setTitle("-")
        replacementView.setRightImage(R.drawable.ic_black_drop_down)
        replacementView.setPaddingRightImage(resources.getDimensionPixelSize(R.dimen._3sdp))
        replacementView.setClick {
            replacementPopupWindow?.clearAll()
            val models = ArrayList<GeneralModel>()
            users.forEach {
                models.add(GeneralModel(it.id, it.name))
            }
            replacementPopupWindow?.addItems(models)
            replacementPopupWindow?.setEventListener {
                replacementView.setTitle(it.name)
                replacementUserId = it.id
            }
            replacementPopupWindow?.showPopup(replacementView)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            GalleryHelper.ARG_GALLERY -> {
                try {
                    if (data?.data != null) {
                        attachmentFile = FileUtil.from(requireContext(), data.data)
                        fileNameTextView.isVisible = attachmentFile?.name.isNullOrBlank() == false
                        fileNameTextView.text = attachmentFile?.name ?: "File name"
                    }
                } catch (e : Exception) {
                    BaseApplication.showToast("File not support, please another file!")
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setupCameraView() {
        absentCapturePictureView.hideBottomDescription()
        absentCapturePictureView.setEventFlash {
            absentCapturePictureView.setupFlash()
        }
        absentCapturePictureView.setEventCapture {
            hideCamera()
            eventClick(it, attachmentFile?: File(""), replacementUserId ?: "", reasonInputView.inputValue, typeAbsentId ?: "")
            dismiss()
        }
        absentCapturePictureView.setEventClose {
            hideCamera()
        }
    }

    private fun showCamera() {
        absentCapturePictureView.isVisible = true
        absentCapturePictureView.setupMainCamera()
    }

    private fun hideCamera() {
        absentCapturePictureView.isVisible = false
    }
}