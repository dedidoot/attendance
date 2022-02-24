package gapara.co.id.core.base

import android.content.Intent
import android.os.Bundle
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import gapara.co.id.R
import gapara.co.id.core.api.log
import org.koin.android.viewmodel.ext.android.viewModelByClass
import kotlin.reflect.KClass

abstract class MvvmActivity<T : ViewDataBinding, V : BaseViewModel>(clazz: KClass<V>) : AppCompatActivity() {

    lateinit var binding: T
    val viewModel: V by viewModelByClass(clazz)

    protected abstract val layoutResource: Int

    protected abstract fun viewLoaded()

    protected abstract val bindingVariable: Int

    private var tokenExpiredDialog: BaseDialogView? = null
    private var tryAgainConnectionDialog: BaseDialogView? = null
    private var scrollView : ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResource)
        binding.setVariable(bindingVariable, viewModel)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        setupDialogView()
        registerObserver()
        scrollView = findViewById(R.id.scrollView)
        viewLoaded()
        log("class name ${this::class.java.name}")
    }

    private fun setupDialogView() {
        tokenExpiredDialog = BaseDialogView(this)
        tryAgainConnectionDialog = BaseDialogView(this)
    }

    private fun registerObserver() {
        observeTokenExpiredDialog()
        observeTryAgainConnectionDialog()
        observeMessage()
    }

    private fun observeTryAgainConnectionDialog() {
        BaseApplication.baseApplication?.isTryAgainConnectionDialog?.observe(this, {
            if (it == true && tryAgainConnectionDialog?.dialog?.isShowing == false) {
                tryAgainConnectionDialog?.setMessage("Connection time out, please try again?")
                    ?.setPositiveString("Oke")
                    ?.setOnClickPositive {
                        BaseApplication.baseApplication?.isTryAgainConnectionDialog?.value = false
                        val newIntent = Intent(this, this::class.java)
                        newIntent.putExtras(intent)
                        startActivity(newIntent)
                        finish()
                    }
                    ?.setNegativeString("No")
                    ?.setOnClickNegative { BaseApplication.baseApplication?.isTryAgainConnectionDialog?.value = false }
                    ?.show()
            }
        })
    }

    private fun observeTokenExpiredDialog() {
        BaseApplication.baseApplication?.isShowTokenExpiredDialog?.observe(this, {
            if (it == true && tokenExpiredDialog?.dialog?.isShowing == false) {
                tokenExpiredDialog?.setMessage("Token has expired, re-login please!")
                    ?.setPositiveString("Oke")
                    ?.setOnClickPositive {
                        BaseApplication.baseApplication?.isShowTokenExpiredDialog?.value = false
                        BaseApplication.baseApplication?.logout()
                    }?.show()
            }
        })
    }

    private fun observeMessage() {
        viewModel.message.observe(this, {
            if (!it.isNullOrBlank()) {
                scrollView?.postDelayed({
                    scrollView?.fullScroll(ScrollView.FOCUS_DOWN)
                }, 500)
            }
        })
    }
}