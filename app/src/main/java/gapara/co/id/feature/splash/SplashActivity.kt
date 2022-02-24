package gapara.co.id.feature.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gapara.co.id.core.base.notification.NotificationServiceHelper
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.*
import gapara.co.id.feature.home.HomeActivity
import gapara.co.id.feature.login.LoginActivity

class SplashActivity : AppCompatActivity(), CoroutineDeclare {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            NotificationServiceHelper(this).setCurrentTimer(60000).run()
        }
        setRefreshRemoteConfig {
            BaseApplication.sessionManager?.baseUrl = RemoteConfigHelper.get(RemoteConfigKey.BASE_URL)
            openHomeActivity()
        }
    }

    private fun openHomeActivity() {
        makeHandler(2000) {
            val intent = Intent()
            if (BaseApplication.sessionManager?.accessToken.isNullOrEmpty()) {
                intent.setClass(this, LoginActivity::class.java)
            } else {
                intent.setClass(this, HomeActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}