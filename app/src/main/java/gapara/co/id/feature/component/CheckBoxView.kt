package gapara.co.id.feature.component

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import gapara.co.id.R
import gapara.co.id.core.base.widthScreenSize
import kotlinx.android.synthetic.main.view_check_box.view.*

class CheckBoxView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    private var checkedListener : ((Boolean) -> Unit?)? = null

    init {
        inflate(context, R.layout.view_check_box, this)
        layoutParams = LayoutParams(widthScreenSize(context), ViewGroup.LayoutParams.WRAP_CONTENT)

        checkBox.setOnClickListener {
            checkedListener?.let { it(checkBox.isChecked) }
        }
    }

    fun setText(text : String?) {
        checkBox.text = text ?: "-"
    }

    fun setupIsCheckBox(isChecked : Boolean?) {
        checkBox.isChecked = isChecked == true
    }

    fun setListenerCheckBox(checkedListener : (Boolean) -> Unit) {
        this.checkedListener = checkedListener
    }
}