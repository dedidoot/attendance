package gapara.co.id.core.base

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import gapara.co.id.R
import gapara.co.id.core.api.log
import gapara.co.id.feature.splash.SplashActivity
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module

class BaseApplication : Application() {

    var isShowTokenExpiredDialog = MutableLiveData<Boolean>()
    var isTryAgainConnectionDialog = MutableLiveData<Boolean>()

    val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
        context = this
        baseApplication = this
        isShowTokenExpiredDialog.value = false
        isTryAgainConnectionDialog.value = false
        FirebaseApplication.init(this)
        initializeKoin()
        registerFcmToken()
        setupFirebaseRemoteConfig()
    }

    fun setupFirebaseRemoteConfig(listener: (() -> Unit?)? = null) {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            //.setMinimumFetchIntervalInSeconds(300) // 5 minutes
            .setMinimumFetchIntervalInSeconds(1) // 5 minutes
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        val cacheExpiration: Long = 0
        remoteConfig.fetch(cacheExpiration).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                log("Fetch Succeeded")
                remoteConfig.activate()
            } else {
                log("Fetch failed")
            }

            listener?.let { it() }
        }
    }

    fun registerFcmToken() {
        if (sessionManager?.fcmToken.isNullOrEmpty()) {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                log("status register fcm token ${it.isSuccessful}")
                if (it.isSuccessful) {
                    sessionManager?.fcmToken = it.result?.token
                    log("fcm token ${sessionManager?.fcmToken}")
                } else {
                    log("${it.exception}")
                }
            }
        } else {
            log("fcm token still have : ${sessionManager?.fcmToken}")
        }
    }

    private fun initializeKoin() {
        val modules = arrayListOf<Module>()
        modules.addAll(Modules.getModules())
        startKoin(this, modules)
    }

    fun logout() {
        sessionManager?.clearSession()
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    companion object {
        var sessionManager: SessionManager? = null
        var context: Context? = null
        var baseApplication: BaseApplication? = null
        var permissions : Permissions?=null

        fun showToast(message: String) {
            context?.apply {
                showAlert(this, message)
            }
        }
    }
}