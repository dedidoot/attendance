package gapara.co.id.feature.component

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import gapara.co.id.R
import gapara.co.id.core.base.isInvisible
import gapara.co.id.core.base.isVisible
import gapara.co.id.core.base.showAlert
import kotlinx.android.synthetic.main.view_app_bar.view.*

class AppBarView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    init {
        inflate(context, R.layout.view_app_bar, this)
    }

    fun setLeftImage(icon : Int) {
        leftImageView.isVisible = true
        leftImageView.setImageResource(icon)
    }

    fun setRightImage(icon : Int) {
        rightImageView.isVisible = true
        rightImageView.setImageResource(icon)
    }

    fun setCenterImage(icon : Int) {
        centerImageView.isVisible = true
        centerImageView.setImageResource(icon)
    }

    fun setTitleBar(title : String) {
        centerImageView.isInvisible = true
        titleBar.isVisible = true
        titleBar.text = title
    }

    fun setClickLeftImage(callback : () -> Unit) {
        leftImageView.isVisible = true
        leftImageView.setOnClickListener {
            callback()
        }
    }

    fun hideRightImage() {
        rightImageView.isVisible = false
    }

    fun setClickRightImage(callback : () -> Unit) {
        rightImageView.isVisible = true
        rightImageView.setOnClickListener {
            showAlert(context, "On progress development")
            callback()
        }
    }

    fun setClickRight2Image(callback : () -> Unit) {
        right2ImageView.isVisible = true
        right2ImageView.setOnClickListener {
            callback()
        }
    }

    fun setClearImage() {
        setLeftImage(R.drawable.ic_white_clear)
        val size = resources.getDimensionPixelSize(R.dimen._16sdp)
        leftImageView.setPadding(size, size, size, size)
    }
}