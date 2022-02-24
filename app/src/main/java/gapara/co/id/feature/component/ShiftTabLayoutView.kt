package gapara.co.id.feature.component

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import gapara.co.id.R
import kotlinx.android.synthetic.main.view_tab_layout_shift.view.*

class ShiftTabLayoutView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    init {
        inflate(context, R.layout.view_tab_layout_shift, this)
    }

    fun setTitle(title: String?) {
        titleTextView.text = title ?: "-"
    }

    fun setClick(callback: () -> Unit) {
        tabView.setOnClickListener {
            callback()
        }
    }

    fun setTabBackground(background : Int) {
        tabView.setBackgroundResource(background)
    }

    fun setMargin(left : Int? = null, right : Int? = null, top : Int? = null, bottom : Int? = null) {
        val params = tabView.layoutParams as LayoutParams
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
        tabView.layoutParams = params
    }

    fun setWidthMatchParentTab() {
        tabView.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}