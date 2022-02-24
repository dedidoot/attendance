package gapara.co.id.feature.component

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import gapara.co.id.R
import kotlinx.android.synthetic.main.view_bottom_bar.view.*

class BottomBarView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    init {
        inflate(context, R.layout.view_bottom_bar, this)
    }

    fun setClickHome(callback: () -> Unit) {
        homeView.setOnClickListener {
            callback()
        }
    }

    fun setClickAccount(callback: () -> Unit) {
        accountView.setOnClickListener {
            callback()
        }
    }
}