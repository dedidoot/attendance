package gapara.co.id.feature.component

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
import gapara.co.id.core.api.GetRequest
import gapara.co.id.core.api.response.UserResponse
import gapara.co.id.core.base.BaseApplication
import gapara.co.id.core.base.EditTextDelayHelper
import gapara.co.id.core.base.isVisible
import gapara.co.id.core.model.Urls
import gapara.co.id.feature.component.card.ItemV2CardView
import kotlinx.android.synthetic.main.bottom_sheet_user.view.*
import kotlinx.coroutines.launch

class UserBottomSheet(var nameRole : String?=null) : BottomSheetDialogFragment(), CoroutineDeclare {

    private var behavior: BottomSheetBehavior<*>? = null
    private lateinit var dialog: BottomSheetDialog
    private lateinit var viewDialog: View
    private lateinit var callback: (String, String) -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(requireContext())
        viewDialog = inflate(requireContext(), R.layout.bottom_sheet_user, null)
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
        setupView()
    }

    override fun onStart() {
        super.onStart()
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupView() {
        viewDialog.apply {
            EditTextDelayHelper.run(editText = searchInputView.getInputEditText()) {
                getUserPic(it)
            }
        }
    }

    fun showDialog(supportFragmentManager: FragmentManager, callback: (String, String) -> Unit) {
        this.callback = callback
        showNow(supportFragmentManager, "")
    }

    private fun getUserPic(userText : String) {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_USERS_LIST)
            //request.queries["branch_id"] = BaseApplication.sessionManager?.branchId ?: ""
            request.queries["q"] = userText
            nameRole.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["role"] = this
            }
            val response= request.get<UserResponse>()
            viewDialog.userView.removeAllViews()
            if (response?.isSuccess() == true) {
                response.data?.users?.items?.forEach {
                    val userItem = ItemV2CardView(requireContext())
                    userItem.setRoundUrl(it.avatar)
                    userItem.setTitleTv(it.name)
                    userItem.setSubtitleTv(it.role?.name)

                    userItem.setRight1Image(R.drawable.ic_black_arrow_right) {
                        callback(it.id ?:"", it.name ?: "")
                        dismiss()
                    }
                    viewDialog.userView.addView(userItem)
                }
            }
            showLoading(false)
        }
    }

    private fun showLoading(isShow:Boolean = true) {
        viewDialog.apply {
            loadingView.isVisible = isShow
        }
    }
}