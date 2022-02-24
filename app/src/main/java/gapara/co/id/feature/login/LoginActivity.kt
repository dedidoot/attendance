package gapara.co.id.feature.login

import android.content.Intent
import android.text.InputType
import android.view.inputmethod.EditorInfo
import gapara.co.id.core.base.MvvmActivity
import gapara.co.id.databinding.ActivityLoginBinding
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.base.checkVersion
import gapara.co.id.core.base.hideKeyboard
import gapara.co.id.feature.feedback.CreateFeedbackActivity
import gapara.co.id.feature.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : MvvmActivity<ActivityLoginBinding, LoginViewModel>(LoginViewModel::class) {

    override val layoutResource: Int = R.layout.activity_login

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        setupInputEmailView()
        setupInputPasswordView()
        registerObserver()
        checkVersion(this)
    }

    private fun registerObserver() {
        observerLoginResponse()
    }

    private fun observerLoginResponse() {
        viewModel.loginApiResponse.observe(this, {
            it?.apply {
                openHomeActivity()
            }
        })
    }

    private fun openHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun setupInputEmailView() {
        username.setHint("Username")
        username.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        username.setLeftIcon(R.drawable.ic_black_user)
        username.imeiOptions = EditorInfo.IME_ACTION_NEXT
    }

    private fun setupInputPasswordView() {
        password.apply {
            setHint("Password")
            setLeftIcon(R.drawable.ic_lock)
            setRightIcon(R.drawable.ic_eyes_close)
            val passwordType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            inputType = passwordType
            imeiOptions = EditorInfo.IME_ACTION_DONE
            setOnClickRightIcon {
                if (inputType == passwordType) {
                    setRightIcon(R.drawable.ic_visible_password)
                    inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    setLastSelection()
                } else {
                    setRightIcon(R.drawable.ic_eyes_close)
                    inputType = passwordType
                    setLastSelection()
                }
            }
        }
    }

    fun onFeedback() {
        startActivity(CreateFeedbackActivity.onNewIntent(this))
    }

    fun onLogin() {
        hideKeyboard(this)
        viewModel.onLogin()
    }

    fun onForgotPassword() {

    }
}