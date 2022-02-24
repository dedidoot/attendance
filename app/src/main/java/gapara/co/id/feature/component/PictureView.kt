package gapara.co.id.feature.component

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import gapara.co.id.R
import gapara.co.id.core.base.isVisible
import gapara.co.id.core.base.loadGlide
import gapara.co.id.core.base.loadUrl
import kotlinx.android.synthetic.main.view_picture.view.*

class PictureView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    var uniqueId : String? = ""

    init {
        inflate(context, R.layout.view_picture, this)
    }

    fun setUriImage(uri: Uri?) {
        uri?.apply {
            pictureImageView.setImageURI(this)
        }
    }

    fun setUrlImage(url: String?) {
        loadingView.isVisible = true
        pictureImageView.loadGlide(url) {
            loadingView.isVisible = false
        }
    }

    fun setClick(callback: () -> Unit) {
        pictureView.setOnClickListener {
            callback()
        }
    }

    fun setClearClick(callback: () -> Unit) {
        clearImageView.isVisible = true
        clearImageView.setOnClickListener {
            callback()
        }
    }

    fun setPaddingRightImage(size : Int) {
        pictureView.setPadding(size, size, size, size)
    }

    fun setMargin(left : Int? = null, right : Int? = null, top : Int? = null, bottom : Int? = null) {
        val params = pictureView.layoutParams as LayoutParams
        left?.apply {
            params.marginStart = this
        }
        right?.apply {
            params.marginEnd = this
        }
        top?.apply {
            params.topMargin = this
        }
        bottom?.apply {
            params.bottomMargin = this
        }
        pictureView.layoutParams = params
    }
}