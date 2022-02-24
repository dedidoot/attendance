package gapara.co.id.feature.component.card

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import gapara.co.id.R
import gapara.co.id.core.base.isVisible
import gapara.co.id.core.base.loadUrl
import kotlinx.android.synthetic.main.view_item_card_v2.view.*
import java.util.*

class ItemV2CardView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    init {
        inflate(context, R.layout.view_item_card_v2, this)
        setTriTitleBT()
        setLeftButton()
        setRightButton()
        setRight1Image()
        setRight2Image()
        setRight3Image()
    }

    fun setRoundUrl(url: String?) {
        roundImageView2.loadUrl(url)
    }

    fun setIsShowRound(isShow: Boolean) {
        roundImageView2.isVisible = isShow
    }

    fun setTitleTv(title: String?) {
        titleTV.isVisible = !title.isNullOrBlank()
        titleTV.text = title ?: "-"
    }

    fun setSubtitleTv(subtitle: String?) {
        subtitleTV.isVisible = !subtitle.isNullOrBlank()
        subtitleTV.text = subtitle?.capitalize(Locale.getDefault()) ?: "-"
    }

    fun setTriTitleBT(text: String? = null, callback: (() -> Unit?)? = null) {
        triTitleBT.isVisible = !text.isNullOrBlank()
        triTitleBT.text = text ?: "-"
        triTitleBT.setOnClickListener { callback?.let { it1 -> it1() } }
    }

    fun setColorSubtitleTV(color: Int) {
        subtitleTV.setTextColor(ContextCompat.getColor(context, color))
    }

    fun setLeftButton(text: String? = null, color: Int? = null, callback: (() -> Unit?)? = null) {
        leftButton.isVisible = !text.isNullOrBlank()
        leftButton.text = text
        color?.apply { leftButton.setBackgroundResource(this) }
        leftButton.setOnClickListener { callback?.let { it1 -> it1() } }
        buttonView.isVisible = leftButton.isVisible || rightButton.isVisible
    }

    fun setRightButton(text: String? = null, color: Int? = null, callback: (() -> Unit?)? = null) {
        rightButton.isVisible = !text.isNullOrBlank()
        rightButton.text = text
        color?.apply { rightButton.setBackgroundResource(this) }
        rightButton.setOnClickListener { callback?.let { it1 -> it1() } }
        buttonView.isVisible = leftButton.isVisible || rightButton.isVisible
    }

    fun setRight1Image(icon: Int? = null, callback: (() -> Unit?)? = null) {
        right1ImageView.isVisible = icon != null
        right1ImageView.setImageResource(icon ?: 0)
        right1ImageView.setOnClickListener { callback?.let { it1 -> it1() } }
    }

    fun setRight2Image(icon: Int? = null, callback: (() -> Unit?)? = null) {
        right2ImageView.isVisible = icon != null
        right2ImageView.setImageResource(icon ?: 0)
        right2ImageView.setOnClickListener { callback?.let { it1 -> it1() } }
    }

    fun setRight3Image(icon: Int? = null, callback: (() -> Unit?)? = null) {
        right3ImageView.isVisible = icon != null
        right3ImageView.setImageResource(icon ?: 0)
        right3ImageView.setOnClickListener { callback?.let { it1 -> it1() } }
    }
}