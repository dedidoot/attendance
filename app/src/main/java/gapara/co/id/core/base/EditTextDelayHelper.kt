package gapara.co.id.core.base

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ProgressBar

object EditTextDelayHelper {

    fun run(
        progressBar: ProgressBar? = null,
        editText: EditText,
        setCallback: (String) -> Unit
    ) {

        val typingDelayHelper = TypingDelayHelper()

        typingDelayHelper.setCallback{
            if (it) {
                if (editText.text.toString().trim().isNotEmpty()) {
                    setCallback(editText.text.toString())
                }
            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                typingDelayHelper.lastTextEdit = System.currentTimeMillis()

                typingDelayHelper.typingHandler?.postDelayed(
                    typingDelayHelper,
                    typingDelayHelper.delay
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                typingDelayHelper.typingHandler?.removeCallbacks(typingDelayHelper)

                if (s?.trim()?.isNotEmpty() == true) {
                    progressBar?.isVisible = true
                }
            }
        })
    }

}