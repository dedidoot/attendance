package gapara.co.id.feature.component

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import gapara.co.id.R
import gapara.co.id.core.base.isInvisible
import gapara.co.id.core.base.isVisible
import kotlinx.android.synthetic.main.view_date_tablayout_view.view.*

class DateTabLayoutView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    init {
        inflate(context, R.layout.view_date_tablayout_view, this)
    }

    fun setTitle(title: String?) {
        titleTextView.text = title ?: "-"
    }

    fun setDesc(title: String?) {
        descTextView.text = title ?: "-"
    }

    fun setIsInvisibleLine(isInvisible: Boolean) {
        lineView.isInvisible = isInvisible
    }

    fun setTitleStyle(typeFace : Typeface?) {
        typeFace?.apply {
            titleTextView.typeface = this
        }
    }

    fun setDescStyle(typeFace : Typeface?) {
        typeFace?.apply {
            descTextView.typeface = this
        }
    }

    fun setClick(callback: () -> Unit) {
        tabLayoutView.setOnClickListener {
            callback()
        }
    }

    fun setTitleColor(color : Int) {
        titleTextView.setTextColor(ContextCompat.getColor(context, color))
    }

    fun setDescColor(color : Int) {
        descTextView.setTextColor(ContextCompat.getColor(context, color))
    }
}