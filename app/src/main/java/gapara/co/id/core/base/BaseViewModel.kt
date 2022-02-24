package gapara.co.id.core.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    val isLoading = mutableLiveDataOf<Boolean>()
    val message = mutableLiveDataOf<String>()

    fun showLoading(isShow : Boolean = true) {
        isLoading.value = isShow
    }

    fun showToast(message: String) {
        BaseApplication.showToast(message)
    }
}