package gapara.co.id.feature.incident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.model.GeneralModel
import kotlinx.android.synthetic.main.dialog_incident_type.*
import kotlinx.coroutines.launch

class IncidentTypeDialog(private val eventClick: (ArrayList<GeneralModel>) -> Unit) :
    DialogFragment(), CoroutineDeclare {

    private var incidentTypeAdapter: IncidentTypeAdapter? = null
    private var incidentData = ArrayList<GeneralModel>()
    var title = ""

    fun setIncidentData(data: ArrayList<GeneralModel>) {
        incidentData.clear()
        incidentData = data
        incidentTypeAdapter?.notifyDataSetChanged()
    }

    override fun getTheme(): Int {
        return R.style.Dialog1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_incident_type, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        titleX.text = title
        acceptButton.setOnClickListener {
            eventClick(incidentData)
            dismiss()
        }

        incidentTypeAdapter = IncidentTypeAdapter(context!!, incidentData)
        incidentTypeAdapter?.setupEventListener { modelSelected ->
            launch {
                incidentData.forEachIndexed { index, it ->
                    if (it == modelSelected) {
                        incidentData[index] = it
                    }
                }
                incidentTypeAdapter?.notifyDataSetChanged()
            }
        }
        recyclerView.adapter = incidentTypeAdapter
    }
}