package gapara.co.id.core.base

import android.view.View

class SingleClickListener : View.OnClickListener {

    private val minimumClickTime = 2000
    private var lastClickTime: Long = 0
    private var singleClickListener: ((View) -> Unit)? = null

    fun setSingleClickListener(singleClickListener: ((View) -> Unit)?): SingleClickListener {
        this.singleClickListener = singleClickListener
        return this
    }

    override fun onClick(view: View) {
        val currentTime = System.currentTimeMillis()

        singleClickListener?.takeIf { lastClickTime == 0.toLong() || (currentTime - lastClickTime) > minimumClickTime }?.run {
            this(view)
            lastClickTime = System.currentTimeMillis()
        }
    }
}