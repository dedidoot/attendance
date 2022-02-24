package gapara.co.id.feature.incident

import android.content.Context
import android.view.ViewGroup
import gapara.co.id.core.base.RecyclerAdapter
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.feature.component.CheckBoxView

class IncidentTypeAdapter(currentContext: Context, items: ArrayList<GeneralModel>) :
    RecyclerAdapter<GeneralModel, IncidentTypeAdapter.CheckBoxItem>(currentContext, items) {

    private var listenerCheckBox : ((GeneralModel) -> Unit?)? = null

    fun setupEventListener(listenerCheckBox : (GeneralModel) -> Unit) {
        this.listenerCheckBox = listenerCheckBox
    }

    override fun loadMore() {
        needToLoadMore = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckBoxItem {
        return CheckBoxItem(CheckBoxView(parent.context))
    }

    inner class CheckBoxItem(private var checkBoxView: CheckBoxView) : BaseViewHolder(checkBoxView) {
        override fun bind(item: GeneralModel) {
            checkBoxView.setText(item.name)
            checkBoxView.setupIsCheckBox(item.isChecked)
            checkBoxView.setListenerCheckBox {
                item.isChecked = it
                listenerCheckBox?.let { it1 -> it1(item) }
            }
        }
    }
}