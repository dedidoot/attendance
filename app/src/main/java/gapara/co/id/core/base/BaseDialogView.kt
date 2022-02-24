package gapara.co.id.core.base

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import gapara.co.id.R
import kotlinx.android.synthetic.main.base_view_dialog.view.*

class BaseDialogView : FrameLayout {

    var builder: AlertDialog.Builder?

    var dialog: AlertDialog? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)

    init {
        inflate(context, R.layout.base_view_dialog, this)
        builder = AlertDialog.Builder(context, R.style.TransparentDialog)
        builder?.setCancelable(false)
        builder?.setView(this)

        dialog = builder?.create()

        negativeButton.setOnClickListener {
            dismiss()
        }
    }

    fun setTitle(title: String): BaseDialogView {
        titleDialogTextView?.run {
            isVisible = true
            text = title
        }
        return this
    }

    fun setMessage(message: String): BaseDialogView {
        messageDialogTextView?.text = message
        return this
    }

    fun setPositiveString(positiveString: String): BaseDialogView {
        positiveButton?.text = positiveString
        return this
    }

    fun setNegativeString(negativeString: String): BaseDialogView {
        negativeButton?.text = negativeString
        negativeButton.isVisible = negativeString.isNotBlank()
        return this
    }

    fun setOnClickPositive(listener: () -> Unit): BaseDialogView {
        positiveButton.setOnClickListener {
            listener()
            dismiss()
        }
        return this
    }

    fun setOnClickNegative(listener: () -> Unit): BaseDialogView {
        negativeButton.setOnClickListener {
            listener()
            dismiss()
        }
        return this
    }

    fun showNegativeButton(): BaseDialogView {
        negativeButton.isVisible = true
        return this
    }

    fun hideNegativeButton(): BaseDialogView {
        negativeButton.isVisible = false
        return this
    }

    fun setCancelable(isCancelable: Boolean): BaseDialogView {
        dialog?.setCancelable(isCancelable)
        return this
    }

    fun show(): BaseDialogView {
        if (!(context as Activity).isFinishing) {
            dialog?.show()
        }
        return this
    }

    fun dismiss(): BaseDialogView {
        dialog?.dismiss()
        return this
    }
}