package gapara.co.id.feature.log_book

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.PostRequest
import gapara.co.id.core.api.UploadRequest
import gapara.co.id.core.api.log
import gapara.co.id.core.api.response.BaseResponse
import gapara.co.id.core.api.response.Media2Response
import gapara.co.id.core.base.*
import gapara.co.id.core.model.PostCreateLostReport
import gapara.co.id.core.model.Urls
import gapara.co.id.feature.component.PictureView
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_log_book.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.HashMap

class CreateLogBookActivity : AppCompatActivity(), CoroutineDeclare {

    private var imagesUploadedId = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_book)
        setupAppBar()
        setupInput()
        setupCameraView()
    }

    private fun setupAppBar() {
        appBarView.setClickLeftImage {
            onBackPressed()
        }
        appBarView.setTitleBar("Log Book")
        appBarView.setClickLeftImage { onBackPressed() }
    }

    private fun setupInput() {
        descInputView.setMinLines(4)
        descInputView.imeiOptions = EditorInfo.IME_ACTION_DONE
        descInputView.setMultiLineDone()
        descInputView.setHint("Description")
        titleInputView.setHint("Title")
    }

    private fun setupCameraView() {
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

    fun onCamera(view: View) {
        hideKeyboard(this)
        showCamera()
    }

    private fun showCamera() {
        if (!EasyPermissions.hasPermissions(this, *cameraPermission)) {
            EasyPermissions.requestPermissions(
                this,
                "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu",
                0,
                *cameraPermission
            )
        } else {
            capturePictureView.isVisible = true
            capturePictureView.setupMainCamera()
        }
    }

    private fun hideCamera() {
        capturePictureView.isVisible = false
    }

    private fun setupUploadImage(file: File) {
        launch {
            try {
                val compressedImageFile = Compressor.compress(this@CreateLogBookActivity, file) {
                    quality(
                        RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt()
                    )
                }

                delay(1000)

                runOnUiThread {
                    hideCamera()
                    uploadImage(compressedImageFile)

                    val pictureView = PictureView(this@CreateLogBookActivity)
                    pictureView.setClearClick {
                        imagesView.removeView(pictureView)
                    }
                    pictureView.setUriImage(Uri.fromFile(compressedImageFile))
                    imagesView.addView(pictureView)
                }
            } catch (exception: Exception) {
                hideCamera()
                showAlert(
                    this@CreateLogBookActivity,
                    "Camera capture file is corrupt, please try again!"
                )
            }
        }
    }

    fun onSave(view: View) {
        if (titleInputView.inputValue.isEmpty()) {
            showAlert(this, "Title cannot empty")
            return
        }
        if (descInputView.inputValue.isEmpty()) {
            showAlert(this, "Description cannot empty")
            return
        }

        imagesUploadedId.clear()
        for (index in 0 until imagesView.childCount) {
            val pictureView = imagesView.getChildAt(index) as PictureView
            pictureView.uniqueId.takeIf { !it.isNullOrBlank() }?.apply {
                imagesUploadedId.add(this)
            }
        }
        hideKeyboard(this)

        loading.isVisible = true

        launch {
            val model = PostCreateLostReport(
                schedule_id = BaseApplication.sessionManager?.scheduleId,
                title = titleInputView.inputValue,
                content = descInputView.inputValue,
                images = imagesUploadedId,
            )
            val response = PostRequest<PostCreateLostReport>(Urls.POST_LOG_BOOK_CREATE).post<BaseResponse>(model)
            loading.isVisible = false

            if (response?.isSuccess() == true) {
                showLongToast(response.message ?: "Success create report")
                onBackPressed()
            } else {
                val message = getErrorMessageServer(response?.errors?.values?.toString()).takeIf { it.isNotBlank() } ?: kotlin.run { response?.message ?: "Failed upload" }
                showLongRedToast(message)
            }
        }
    }

    private fun uploadImage(file: File) {
        loading.isVisible = true
        launch {
            val request = UploadRequest(Urls.UPLOAD_IMAGE_REPORT)
            val files = HashMap<String, File>()
            files["image"] = file
            val names = HashMap<String, String>()
            names["section"] = "documentation"
            names["description"] = "Upload from create log book"

            val response = request.upload<Media2Response>(files, names)
            if (response?.isSuccess() == true) {
                if (imagesView.childCount > 0) {
                    (imagesView.getChildAt(imagesView.childCount - 1) as PictureView).uniqueId = response.data?.image?.id
                }
            } else {
                setupFailedUpload()
            }
            loading.isVisible = false
        }
    }

    private fun setupFailedUpload() {
        showLongRedToast("Sorry failed upload image, please try again!")
        if (imagesView.childCount > 0) {
            try {
                val lastImageView = (imagesView.getChildAt(imagesView.childCount - 1) as PictureView)
                if (lastImageView.uniqueId.isNullOrBlank()) {
                    imagesView.removeView(lastImageView)
                }
            } catch (exception : Exception) {
                log("exception $exception")
            }
        }
    }

    companion object{
        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, CreateLogBookActivity::class.java)
            return intent
        }
    }
}