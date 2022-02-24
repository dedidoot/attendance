package gapara.co.id.core.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

interface CoroutineDeclare : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}