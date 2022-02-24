package gapara.co.id.feature.component.card

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import gapara.co.id.R
import gapara.co.id.core.base.isVisible
import gapara.co.id.core.base.loadUrl
import gapara.co.id.core.base.setHtmlText
import gapara.co.id.core.model.GeneralModel
import kotlinx.android.synthetic.main.view_item_card.view.*

class ItemCardView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    private var right2ImageViewUrl: String? = null

    init {
        inflate(context, R.layout.view_item_card, this)
        setShowRight3ImageView(false)
    }

    fun setRoundText(title: String?) {
        roundTextView.text = title ?: "-"
    }

    fun setRound2Text(title: String?) {
        round2TextView.isVisible = !title.isNullOrBlank()
        round2TextView.setHtmlText(title ?: "-")
    }

    fun setRoundUrl(url: String?) {
        roundImageView.loadUrl(url)
    }

    fun setLeftButtonText(text: String?) {
        leftButton.text = text ?: "-"
    }

    fun setRightButtonText(text: String?) {
        rightButton.text = text ?: "-"
    }

    fun setRight1Image(icon: Int?) {
        right1ImageView.setImageResource(icon ?: 0)
    }

    fun setRight2Image(icon: Int?) {
        right2ImageView.setImageResource(icon ?: 0)
    }

    fun setRight3Image(icon: Int?) {
        right3ImageView.setImageResource(icon ?: 0)
    }

    fun setLeftButtonClick(callback: () -> Unit) {
        leftButton.setOnClickListener {
            callback()
        }
    }

    fun setRightButtonClick(callback: () -> Unit) {
        rightButton.setOnClickListener {
            callback()
        }
    }

    fun setBackgroundLeftButton(backgroundId: Int) {
        leftButton.setBackgroundResource(backgroundId)
    }

    fun setBackgroundRightButton(backgroundId: Int) {
        rightButton.setBackgroundResource(backgroundId)
    }

    fun hideLeftButton() {
        leftButton.isVisible = false
    }

    fun hideRightButton() {
        rightButton.isVisible = false
    }

    fun hideRight2ImageView() {
        right2ImageView.isVisible = false
    }

    fun setShowRight3ImageView(isShow: Boolean) {
        right3ImageView.isVisible = isShow
    }

    fun hideRight1ImageView() {
        right1ImageView.isVisible = false
    }

    fun setAlphaItemCardView() {
        itemCardView.alpha = 0.5.toFloat()
    }

    fun setLoadRight2Image(url: String?) {
        right2ImageViewUrl = url
        setRight2Image(R.drawable.ic_thumb_image)
    }

    fun setRight2ImageClick(clicked: (String?) -> Unit ){
        right2ImageView.setOnClickListener { clicked(right2ImageViewUrl) }
    }

    fun setRight3ImageClick(clicked: () -> Unit ){
        right3ImageView.setOnClickListener { clicked() }
    }

    fun setShowRoundImageView(isShow : Boolean) {
        roundImageView.isVisible = isShow
    }

    fun setShowRight2Button(isShow : Boolean) {
        right2Button.isVisible = isShow
    }

    fun setRight2ButtonClick(clicked: () -> Unit ){
        right2Button.setOnClickListener { clicked() }
    }

    fun setWidthRoundImageView(width : Int) {
        roundImageView.layoutParams.width = width
    }

    fun setTextRight2Button(text : String) {
        right2Button.text = text
    }
}