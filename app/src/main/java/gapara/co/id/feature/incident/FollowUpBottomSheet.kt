package gapara.co.id.feature.incident

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.View.inflate
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.core.base.camera.convertBitmap
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.bottom_sheet_follow_up.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class FollowUpBottomSheet : BottomSheetDialogFragment(), CoroutineDeclare {

    private var behavior: BottomSheetBehavior<*>? = null
    private lateinit var dialog: BottomSheetDialog
    private lateinit var viewDialog: View
    private lateinit var callback: (File?, String) -> Unit
    private var file: File? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(requireContext())
        viewDialog = inflate(requireContext(), R.layout.bottom_sheet_follow_up, null)
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
        setupInput()
        setupButton()
    }

    override fun onStart() {
        super.onStart()
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupButton() {
        viewDialog.apply {
            actionSaveButton.setOnClickListener {
                loadingView.isVisible = true
                actionSaveButton.isVisible = false
                callback(file, actionInputView.inputValue)
            }
            takePictureButton.setOnClickListener {
                showCamera()
            }
        }
    }

    private fun setupInput() {
        viewDialog.apply {
            actionInputView.setMinLines(3)
            actionInputView.imeiOptions = EditorInfo.IME_ACTION_DONE
            actionInputView.setMultiLineDone()
            actionInputView.setHint("Description")
        }
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
        } else {
            viewDialog.capturePictureView.isVisible = true
            viewDialog.capturePictureView.setupMainCamera()
        }
    }

    private fun hideCamera() {
        viewDialog.capturePictureView.isVisible = false
    }

    private fun setupUploadImage(file: File) {
        launch {
            try {
                val compressedImageFile = Compressor.compress(requireActivity(), file) {
                    quality(
                        RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt()
                    )
                }

                delay(1000)

                requireActivity().runOnUiThread {
                    hideCamera()
                    showImageCapture(compressedImageFile)
                }
            } catch (exception: Exception) {
                hideCamera()
                showAlert(requireActivity(), "Camera capture file is corrupt, please try again!")
            }
        }
    }

    private fun showImageCapture(file: File) {
        this.file = file
        try {
            setImageBitmap(file)
        } catch (exception: Exception) {
            viewDialog.image.setImageResource(R.drawable.bg_grey_radius_4)
        }
    }

    private fun setImageBitmap(file: File) {
        val bitmap = MediaHelper.decodeScaledBitmapFromSdCard(file.absolutePath, widthScreenSize(requireContext()), 100)
        val compressBitmap = convertBitmap(requireContext(), bitmap)
        viewDialog.image.setImageBitmap(compressBitmap)
    }

    fun showDialog(supportFragmentManager: FragmentManager, callback: (File?, String) -> Unit) {
        this.callback = callback
        showNow(supportFragmentManager, "")
    }

    fun setFailed() {
        viewDialog.loadingView.isVisible = false
        viewDialog.actionSaveButton.isVisible = true
    }
}