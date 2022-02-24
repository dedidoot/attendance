package gapara.co.id.feature.component

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import gapara.co.id.R
import gapara.co.id.core.base.MediaHelper
import gapara.co.id.core.base.camera.convertBitmap
import gapara.co.id.core.base.loadUrl
import gapara.co.id.core.base.widthScreenSize
import kotlinx.android.synthetic.main.view_follow_up.view.*
import java.io.File

class FollowUpView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    init {
        inflate(context, R.layout.view_follow_up, this)
    }

    fun setTitle(title: String?) {
        titleFollowUp.text = title ?: "-"
    }

    fun setTime(time: String?) {
        timeTextView.text = time ?: "-"
    }

    fun setImageUrl(url: String?, callback : () -> Unit) {
        if (url?.contains("http", ignoreCase = true) == true) {
            imageFollowUp.loadUrl(url)
        } else {
            showImageCapture(url)
        }

        imageFollowUp.setOnClickListener {
            callback()
        }
    }

    private fun showImageCapture(filePath: String?) {
        try {
            setImageBitmap(File(filePath ?: ""))
        } catch (exception: Exception) {
            imageFollowUp.setImageResource(R.drawable.bg_grey_radius_4)
        }
    }

    private fun setImageBitmap(file: File) {
        val bitmap = MediaHelper.decodeScaledBitmapFromSdCard(file.absolutePath, widthScreenSize(context), 100)
        val compressBitmap = convertBitmap(context, bitmap)
        imageFollowUp.setImageBitmap(compressBitmap)
    }
}