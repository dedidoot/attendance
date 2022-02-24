package gapara.co.id.feature.incident

import android.content.Context
import android.view.ViewGroup
import gapara.co.id.R
import gapara.co.id.core.base.RecyclerAdapter
import gapara.co.id.core.base.TimeHelper
import gapara.co.id.core.base.setDueDateColor
import gapara.co.id.core.model.IncidentModel
import gapara.co.id.feature.component.ReportView

class IncidentAdapter(currentContext: Context, items: ArrayList<IncidentModel>, var onLoadMore : () -> Unit) :
    RecyclerAdapter<IncidentModel, IncidentAdapter.ReportItem>(currentContext, items) {

    private var listenerClick : ((IncidentModel) -> Unit?)? = null

    fun setupEventListener(listenerClick : (IncidentModel) -> Unit) {
        this.listenerClick = listenerClick
    }

    override fun loadMore() {
        onLoadMore()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportItem {
        return ReportItem(ReportView(parent.context))
    }

    inner class ReportItem(private var reportView: ReportView) : BaseViewHolder(reportView) {
        override fun bind(item: IncidentModel) {
            val size = context.resources?.getDimensionPixelSize(R.dimen._10sdp)
            if (adapterPosition == itemCount) {
                reportView.setMargin(left = size, right = size, top = size, bottom = size)
            } else {
                reportView.setMargin(left = size, right = size, top = size)
            }
            reportView.setTitleMaxLine(1)
            reportView.setDescriptionMaxLine(2)
            reportView.setTitle(item.title)
            reportView.setDescription(item.content)

            reportView.setTime(item.createdAt)
            reportView.setUsername(item.creator?.name)
            reportView.setOptionalText("Deadline\n${getFullDate(item.deadline)}")
            reportView.getOptionalTextView().setDueDateColor(item.deadline, item.isPending(), item.isComplete(), item.isRequestComplete())

            if (item.isRed()) {
                reportView.setRightImage(R.drawable.ic_red_urgent)
            } else {
                reportView.setRightImage(null)
            }

            reportView.setClick { listenerClick?.let { it(item) } }
        }

        private fun getFullDate(createdAt: String?) : String {
            createdAt?.apply {
                return TimeHelper.convertDateText(this, TimeHelper.FORMAT_DATE_FULL_TEXT, TimeHelper.FORMAT_DATE_FULL)
            }
            return ""
        }
    }
}