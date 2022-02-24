package gapara.co.id.feature.component

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import gapara.co.id.R
import gapara.co.id.core.base.isVisible
import gapara.co.id.core.base.pickColor
import kotlinx.android.synthetic.main.view_report.view.*

class ReportView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    init {
        inflate(context, R.layout.view_report, this)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setTitle(title: String?) {
        titleTextView.text = title ?: "-"
    }

    fun setTime(time: String?) {
        timeTextView.isVisible = !time.isNullOrBlank()
        timeTextView.text = time ?: "-"
    }

    fun setDescription(description: String?) {
        descriptionTextView.text = description ?: ""
    }

    fun setUsername(username: String?) {
        usernameTextView.text = username ?: ""
    }

    fun setRightImage(icon: Int? = null) {
        if (icon != null) {
            rightImageView.setImageResource(icon)
        }
        rightImageView.isVisible = icon != null
    }

    fun setClick(callback: () -> Unit) {
        reportView.setOnClickListener {
            callback()
        }
    }

    fun setMargin(left : Int? = null, right : Int? = null, top : Int? = null, bottom : Int? = null) {
        val params = reportView.layoutParams as LayoutParams
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
        reportView.layoutParams = params
    }

    fun setTitleMaxLine(line : Int, ellipsize : TextUtils.TruncateAt = TextUtils.TruncateAt.END) {
        titleTextView.maxLines = line
        titleTextView.ellipsize = ellipsize
    }

    fun setDescriptionMaxLine(line : Int, ellipsize : TextUtils.TruncateAt = TextUtils.TruncateAt.END) {
        descriptionTextView.maxLines = line
        descriptionTextView.ellipsize = ellipsize
    }

    fun setOptionalText(text : String?) {
        optionalTextView.isVisible = !text.isNullOrBlank()
        optionalTextView.text = text
    }

    fun setOptionalColor(color : Int?) {
        optionalTextView.setTextColor(context.pickColor(color ?: R.color.black))
    }

    fun getOptionalTextView() : TextView {
        return optionalTextView
    }
}