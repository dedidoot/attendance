package gapara.co.id.core.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gapara.co.id.R
import gapara.co.id.core.api.log
import gapara.co.id.core.base.camera.convertBitmap
import kotlinx.android.synthetic.main.activity_display_image.*
import java.io.File

class DisplayImageActivity : AppCompatActivity() {

    private var linkImg: String = ""
    private var text: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)

        linkImg = intent?.getStringExtra(EXTRA_LINK_URL) ?: ""
        text = intent?.getStringExtra(EXTRA_TEXT) ?: ""

        if (linkImg.isEmpty()) {
            log("image empty")
            finish()
            return
        }

        setContentView(R.layout.activity_display_image)

        if (linkImg.contains("http", ignoreCase = true)) {
            displayImageView.loadGlide(linkImg) {
                progressBar.isVisible = false
                if (!it) {
                    showAlert(this, "File not found")
                }
            }
        } else {
            showImageCapture(linkImg)
            progressBar.isVisible = false
        }

        textTV.isVisible = text.isNotBlank()
        textTV.text = text
    }

    private fun showImageCapture(filePath: String?) {
        try {
            setImageBitmap(File(filePath ?: ""))
        } catch (exception: Exception) {
            displayImageView.setImageResource(R.drawable.bg_grey_radius_4)
        }
    }

    private fun setImageBitmap(file: File) {
        val bitmap = MediaHelper.decodeScaledBitmapFromSdCard(file.absolutePath, widthScreenSize(this), 100)
        val compressBitmap = convertBitmap(this, bitmap)
        displayImageView.setImageBitmap(compressBitmap)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

    companion object {
        fun onNewIntent(context: Context, url: String?, text:String?=null) : Intent {
            val intent = Intent(context, DisplayImageActivity::class.java)
            intent.putExtra(EXTRA_LINK_URL, url)
            intent.putExtra(EXTRA_TEXT, text)
            return intent
        }

        var EXTRA_LINK_URL = "link_image"
        var EXTRA_TEXT = "text"
    }
}