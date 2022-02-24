package gapara.co.id.core.base

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.ReportRequest
import kotlinx.coroutines.launch

object FirebaseApplication : CoroutineDeclare {

    fun init(context: Context) {
        FirebaseApp.initializeApp(context)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        setFirebaseUserId()
    }

    private fun setFirebaseUserId() {
        val userId = "${BaseApplication.sessionManager?.userId} ${BaseApplication.sessionManager?.accessToken}"
        if (userId.isNotBlank()) {
            FirebaseCrashlytics.getInstance().setUserId(userId)
        }
    }

    fun recordException(e: Throwable, message : String) {
        setFirebaseUserId()
        FirebaseCrashlytics.getInstance().log(message)
        FirebaseCrashlytics.getInstance().recordException(e)
        launch { ReportRequest.postWithDeviceDetail("$e $message") }
    }
}