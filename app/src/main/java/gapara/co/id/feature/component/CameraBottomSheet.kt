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
import gapara.co.id.core.base.*
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.bottom_sheet_camera.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class CameraBottomSheet(var isSelfie : Boolean = false) : BottomSheetDialogFragment(), CoroutineDeclare {

    private var behavior: BottomSheetBehavior<*>? = null
    private lateinit var dialog: BottomSheetDialog
    private lateinit var viewDialog: View
    private lateinit var callback: (File) -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(requireContext())
        viewDialog = inflate(requireContext(), R.layout.bottom_sheet_camera, null)
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
        setupCameraView()
    }

    override fun onStart() {
        super.onStart()
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupCameraView() {
        viewDialog.apply {
            capturePictureView.hideBottomDescription()
            capturePictureView.setEventFlash {
                capturePictureView.setupFlash()
            }
            capturePictureView.setEventCapture {
                setupUploadImage(it)
            }
            capturePictureView.setEventClose {
                capturePictureView.isVisible = false
                dismiss()
            }
        }
    }

    private fun showCamera() {
        if (!EasyPermissions.hasPermissions(requireContext(), *cameraPermission)) {
            EasyPermissions.requestPermissions(
                this,
                "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu",
                0,
                *cameraPermission
            )
            dialog.dismiss()
        } else {
            viewDialog.capturePictureView.isVisible = true

            if (isSelfie) {
                viewDialog.capturePictureView.setupSelfieCamera()
            }else{
                viewDialog.capturePictureView.setupMainCamera()
            }
        }
    }

    private fun hideCamera() {
        viewDialog.capturePictureView.isVisible = false
    }

    private fun setupUploadImage(newFile: File) {
        launch {
            try {
                val compressedImageFile = Compressor.compress(requireActivity(), newFile) {
                    quality(
                        RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt()
                    )
                }

                delay(1000)

                requireActivity().runOnUiThread {
                    hideCamera()
                    callback(compressedImageFile)
                    dismiss()
                }
            } catch (exception: Exception) {
                hideCamera()
                showAlert(requireActivity(), "Camera capture file is corrupt, please try again!")
                dismiss()
            }
        }
    }

    fun showDialog(supportFragmentManager: FragmentManager, callback: (File) -> Unit) {
        this.callback = callback
        showNow(supportFragmentManager, "")
        showCamera()
    }
}