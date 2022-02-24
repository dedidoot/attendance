package gapara.co.id.feature.component

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import gapara.co.id.R
import gapara.co.id.core.base.isVisible
import gapara.co.id.core.base.loadUrl
import kotlinx.android.synthetic.main.view_user.view.*

class UserView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    init {
        inflate(context, R.layout.view_user, this)
    }

    fun setUsername(username: String?) {
        usernameTextView.text = username ?: "-"
    }

    fun setRightImage(icon: Int?) {
        rightImageView.setImageResource(icon ?: 0)
    }

    fun setUrl(url: String?) {
        profileImageView.loadUrl(url)
    }

    fun setClick(callback: () -> Unit) {
        userView.setOnClickListener {
            callback()
        }
    }

    fun setTextColor(colorId: Int) {
        usernameTextView.setTextColor(ContextCompat.getColor(context, colorId))
    }

    fun setAlphaImage(alpha : Float) {
        profileImageView.alpha = alpha
    }

    fun hideRightImage(isShow : Boolean = false) {
        rightImageView.isVisible = isShow
    }

    fun hideLineView(isShow : Boolean = false) : UserView {
        lineView.isVisible = isShow
        return this
    }

    fun setUsernameWithDescription(spannable : SpannableStringBuilder, maxLine : Int = 2) {
        usernameTextView.maxLines = maxLine
        usernameTextView.text = spannable
    }

    fun setTime(text : String?) {
        timeTextView.isVisible = !text.isNullOrBlank()
        timeTextView.text = text
    }

    fun setIsShowReply(isShow: Boolean) {
        replyTextView.isVisible = isShow
    }

    fun setClickReply(click : () -> Unit) {
        replyTextView.setOnClickListener {
            click()
        }
    }
}