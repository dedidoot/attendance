package gapara.co.id.feature.component

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.databinding.BindingAdapter
import gapara.co.id.R
import kotlinx.android.synthetic.main.view_main_menu.view.*

class MainMenuView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    init {
        inflate(context, R.layout.view_main_menu, this)
    }

    fun setImage(icon : Int) {
        image.setImageResource(icon)
    }

    fun setText(text : String) {
        textView.text = text
    }

    fun setClickMenu(callback : () -> Unit) {
        menuView.setOnClickListener {
            callback()
        }
    }

    companion object {

        @BindingAdapter("menuIcon")
        @JvmStatic
        fun setMenuIcon(view: MainMenuView, value: Int?) {
            value?.apply {
                view.setImage(value)
            }
        }

        @BindingAdapter("menuText")
        @JvmStatic
        fun setMenuText(view: MainMenuView, text: String?) {
            text?.apply {
                view.setText(text)
            }
        }
    }
}