package gapara.co.id.feature.component

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.setPadding
import gapara.co.id.R
import gapara.co.id.core.base.isVisible
import kotlinx.android.synthetic.main.view_item_date.view.*

class ItemDateView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    var formatServer : String? = ""

    init {
        inflate(context, R.layout.view_item_date, this)
    }

    fun setTitle(title: String?) {
        titleTextView.text = title ?: "-"
    }

    fun setSubtitle(text1: String?, text2: String?) {
        subtitle1TextView.text = text1 ?: "-"
        subtitle2TextView.text = text2 ?: "-"
        subtitleView.isVisible = true
    }

    fun setRightImage(icon: Int?) {
        rightImageView.setImageResource(icon ?: 0)
    }

    fun setClick(callback: () -> Unit) {
        dateView.setOnClickListener {
            callback()
        }
    }

    fun setPaddingRightImage(size : Int) {
        rightImageView.setPadding(size, size, size, size)
    }

    fun setMargin(left : Int? = null, right : Int? = null, top : Int? = null, bottom : Int? = null) {
        val params = dateView.layoutParams as LayoutParams
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
        dateView.layoutParams = params
    }

    fun disableEllipsize() {
        titleTextView.maxLines = Int.MAX_VALUE
    }
}