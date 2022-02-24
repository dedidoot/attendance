package gapara.co.id.core.base

import androidx.lifecycle.ViewModelStoreOwner
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelStores
import org.koin.android.viewmodel.ext.android.ViewModelStoreOwnerDefinition
import org.koin.android.viewmodel.ext.android.viewModelByClass
import kotlin.reflect.KClass

abstract class MvvmFragment<T : ViewDataBinding, V : BaseViewModel>(clazz: KClass<V>) : Fragment() {

    protected abstract val layoutResource: Int
    protected abstract val bindingVariable: Int
    private val ownerDefinition: ViewModelStoreOwnerDefinition = getOwnerDefinition()
    val viewModel: V by viewModelByClass(clazz, from = ownerDefinition)

    lateinit var binding: T

    protected abstract fun viewLoaded()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, layoutResource, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performDataBinding()
        viewLoaded()
    }

    private fun performDataBinding() {
        binding.setVariable(bindingVariable, viewModel)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun getOwnerDefinition(): ViewModelStoreOwnerDefinition = {
        ownerDefinitionFromActivity()
    }

    private fun ownerDefinitionFromActivity(): ViewModelStoreOwner {
        return ViewModelStoreOwner {
            ViewModelStores.of(activity!!)
        }
    }
}