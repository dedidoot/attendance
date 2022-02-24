package gapara.co.id.feature.initial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.GetRequest
import gapara.co.id.core.api.response.CheckPointResponse
import gapara.co.id.core.base.*
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.core.model.Urls
import kotlinx.android.synthetic.main.dialog_present.*
import kotlinx.coroutines.launch
import java.io.File

class PresentDialog(private val eventClick: (File, String) -> Unit) :
    DialogFragment(), CoroutineDeclare {

    private var checkPointResponse : CheckPointResponse? = null
    private var locationPopupWindow: PopupWindow? = null

    private var locationId : String? = null

    override fun getTheme(): Int {
        return R.style.Dialog2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_present, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupCameraView()
        if (checkPointResponse?.data.isNullOrEmpty()) {
            getLocationCheckPointList()
        }
        setupLocationDropDown()
        takePhotoButton.setOnClickListener {
            showCamera()
        }
    }

    private fun setupLocationDropDown() {
        locationPopupWindow = PopupWindow(requireContext())
        locationView.setTitle("-")
        locationView.setRightImage(R.drawable.ic_black_drop_down)
        locationView.setPaddingRightImage(resources.getDimensionPixelSize(R.dimen._3sdp))
        locationView.setClick {
            locationPopupWindow?.clearAll()

            val model = ArrayList<GeneralModel>()
            checkPointResponse?.data?.forEach {
                model.add(GeneralModel(id = it.id, name = it.name))
            }

            locationPopupWindow?.addItems(model)
            locationPopupWindow?.setEventListener {
                locationView.setTitle(it.name)
                locationId = it.id
            }
            locationPopupWindow?.showPopup(locationView)
        }
    }

    private fun setupCameraView() {
        presentCapturePictureView.hideBottomDescription()
        presentCapturePictureView.setEventFlash {
            presentCapturePictureView.setupFlash()
        }
        presentCapturePictureView.setEventCapture {
            hideCamera()
            if (locationId == null) {
                showAlert(context, "Location cannot empty")
                return@setEventCapture
            }
            eventClick(it, locationId ?: "")
            locationId = null
            dismiss()
        }
        presentCapturePictureView.setEventClose {
            hideCamera()
        }
    }

    private fun showCamera() {
        presentCapturePictureView.isVisible = true
        presentCapturePictureView.setupMainCamera()
    }

    private fun hideCamera() {
        presentCapturePictureView.isVisible = false
    }

    fun getLocationCheckPointList() {
        launch {
            val request = GetRequest(Urls.GET_SUPERVISOR_LOCATION_CHECK_POINT)
            val response= request.get<CheckPointResponse>()
            checkPointResponse = response
        }
    }
}