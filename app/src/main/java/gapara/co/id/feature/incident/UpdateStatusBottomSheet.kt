package gapara.co.id.feature.incident

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.View.inflate
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.core.model.StatusModel
import kotlinx.android.synthetic.main.bottom_sheet_update_status.view.*

class UpdateStatusBottomSheet : BottomSheetDialogFragment(), CoroutineDeclare {

    private var behavior: BottomSheetBehavior<*>? = null
    private lateinit var dialog: BottomSheetDialog
    private lateinit var viewDialog: View
    private lateinit var callback: ( String) -> Unit
    private var statusPopupWindow: PopupWindow? = null
    private var statusId = ""
    private var status = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(requireContext())
        viewDialog = inflate(requireContext(), R.layout.bottom_sheet_update_status, null)
        dialog.setContentView(viewDialog)
        behavior = BottomSheetBehavior.from(viewDialog.parent as View)

        behavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, newState: Float) {
            }

            override fun onStateChanged(p0: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior?.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        })
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupInput()
        setupButton()
    }

    private fun setupButton() {
        viewDialog.apply {
            actionSaveButton.setOnClickListener {
                loadingView.isVisible = true
                actionSaveButton.isVisible = false
                callback(statusId)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupInput() {
        val tempStatus = StatusModel(status)
        viewDialog.apply {
            statusPopupWindow = PopupWindow(requireContext())
            statusPopupWindow?.setEventListener {
                statusId = it.id ?: ""
                statusDropDown.setTitle(it.name)
            }
            statusDropDown.setTitle(tempStatus.getRealStatus())
            statusDropDown.setRightImage(R.drawable.ic_black_drop_down)
            statusDropDown.setPaddingRightImage(resources.getDimensionPixelSize(R.dimen._3sdp))
            statusDropDown.setClick {
                statusPopupWindow?.showPopup(statusDropDown)
            }

            statusPopupWindow?.clearAll()
            val dataDropDown = ArrayList<GeneralModel>()
            if (tempStatus.isOpen()) {
                dataDropDown.add(GeneralModel(id = StatusModel.PENDING, name = "Open"))
                dataDropDown.add(GeneralModel(id = StatusModel.REQUEST_COMPLETE, name = "On Progress"))
            } else if (tempStatus.isRequestComplete()) {
                dataDropDown.add(GeneralModel(id = StatusModel.REQUEST_COMPLETE, name = "On Progress"))
            }
            dataDropDown.add(GeneralModel(id = StatusModel.DONE, name = "Done"))
            statusPopupWindow?.addItems(dataDropDown)
        }
    }

    fun showDialog(supportFragmentManager: FragmentManager, callback: (String) -> Unit) {
        this.callback = callback
        showNow(supportFragmentManager, "")
    }

    fun setStatus(status : String?) {
        this.status = status ?: ""
    }

    fun setFailed() {
        viewDialog.loadingView.isVisible = false
        viewDialog.actionSaveButton.isVisible = true
    }
}