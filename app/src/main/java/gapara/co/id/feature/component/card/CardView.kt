package gapara.co.id.feature.component.card

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.isVisible
import kotlinx.android.synthetic.main.view_card.view.*
import kotlinx.coroutines.launch

class CardView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle), CoroutineDeclare {

    init {
        inflate(context, R.layout.view_card, this)
    }

    fun setTitle(title: String?) {
        titleTextView.text = title ?: "-"
    }

    fun setRightImage(icon: Int?) {
        rightImageView.setImageResource(icon ?: 0)
    }

    fun setRightImageClick(callback: () -> Unit) {
        rightImageView.setOnClickListener {
            callback()
        }
    }

    fun setCardViewClick(callback: () -> Unit) {
        cardView.setOnClickListener {
            callback()
        }
    }

    fun setCardItemView(itemView : ItemCardView) {
        launch { itemCardView.addView(itemView) }
    }

    fun setCardV2ItemView(itemView : ItemV2CardView) {
        launch { itemCardView.addView(itemView) }
    }

    fun hideRightImage() {
        rightImageView.isVisible = false
    }
}