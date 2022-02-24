package gapara.co.id.core.base

import android.os.Handler

class TypingDelayHelper : Runnable {

    var delay: Long = 500
    var lastTextEdit: Long = 0
    var typingHandler: Handler? = null
    private var onCompletedTyping: ((Boolean) -> Unit)? =null

    init {
        typingHandler = Handler()
    }

    fun setCallback(onCompletedTyping: ((Boolean) -> Unit)) {
        this.onCompletedTyping = onCompletedTyping
    }

    override fun run() {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            onCompletedTyping?.let { it(true) }
        } else {
            onCompletedTyping?.let { it(false) }
        }
    }
}
