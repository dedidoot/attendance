package gapara.co.id.feature.component

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.FrameLayout
import androidx.databinding.*
import androidx.databinding.adapters.ListenerUtil
import androidx.databinding.adapters.TextViewBindingAdapter
import gapara.co.id.R
import gapara.co.id.core.base.isVisible
import gapara.co.id.databinding.ViewInputBinding
import kotlinx.android.synthetic.main.view_input.view.*

@InverseBindingMethods(
    value = [InverseBindingMethod(
        type = InputView::class,
        attribute = "app:inputText",
        method = "inputValue",
        event = "android:inputTextAttrChanged"
    )]
)

class InputView : FrameLayout {

    private lateinit var mBinding: ViewInputBinding

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_input, this, true)
    }

    var inputValue: String
        get() = inputEditText.text.toString()
        set(value) {
            inputEditText.setText(value)
        }

    fun setRightIcon(icon : Int) {
        iconRightImageView.isVisible = true
        iconRightImageView.setImageResource(icon)
    }

    fun setLeftIcon(icon : Int) {
        iconLeftImageView.isVisible = true
        iconLeftImageView.setImageResource(icon)
    }

    fun setOnClickRightIcon(callback : () -> Unit) {
        iconRightImageView.setOnClickListener {
            callback()
        }
    }

    fun setLastSelection() {
        inputEditText.text?.apply {
            inputEditText.setSelection(this.length)
        }
    }

    fun setHint(hint : String) {
        inputEditText.hint = hint
    }

    fun setHintSpannable(hint : SpannableStringBuilder) {
        inputEditText.hint = hint
    }

    fun setMinLines(count : Int) {
        inputEditText.minLines = count
    }

    fun setMaxLines(count : Int) {
        inputEditText.maxLines = count
    }

    fun setMultiLineDone() {
        inputEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)
    }

    var inputType: Int
        get() = inputEditText.inputType
        set(value) {
            inputEditText.inputType = value
        }

    var imeiOptions: Int
        get() = inputEditText.imeOptions
        set(value) {
            inputEditText.imeOptions = value
        }

    fun getInputEditText() : EditText {
        return inputEditText
    }

    companion object {

        @BindingAdapter("inputText")
        @JvmStatic
        fun setInputText(view: InputView, value: String?) {
            value?.apply {
                if (view.inputValue != value) {
                    view.inputValue = value
                }
            }
        }

        @BindingAdapter(value = ["android:afterTextChanged", "android:inputTextAttrChanged"], requireAll = false)
        @JvmStatic
        fun setTextWatcher(inputView: InputView, after: TextViewBindingAdapter.AfterTextChanged?, textAttrChanged: InverseBindingListener?) {
            val newValue: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    after?.afterTextChanged(s)
                    textAttrChanged?.onChange()
                }
            }

            val oldValue = ListenerUtil.trackListener(inputView.mBinding.inputEditText, newValue, R.id.textWatcher)
            if (oldValue != null) {
                inputView.mBinding.inputEditText.removeTextChangedListener(oldValue)
            }
            inputView.mBinding.inputEditText.addTextChangedListener(newValue)
        }
    }
}