package gapara.co.id.feature.announcement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.base.TimeHelper
import gapara.co.id.core.base.heightScreenSize
import gapara.co.id.core.base.isVisible
import gapara.co.id.core.base.pickSize
import gapara.co.id.core.model.GeneralModel
import kotlinx.android.synthetic.main.dialog_announcement.*

class AnnouncementDialog(private val eventClick: (GeneralModel) -> Unit) :
    DialogFragment(), CoroutineDeclare {

    private var uniqueId: String? = null
    private var title: String? = null
    private var description: String? = null
    private var time: String? = null
    private var level: String? = null
    private val constraintSet = ConstraintSet()

    fun setUniqueId(uniqueId: String?): AnnouncementDialog {
        this.uniqueId = uniqueId
        return this
    }

    fun setTitle(title: String?): AnnouncementDialog {
        this.title = title
        return this
    }

    fun setDescription(description: String?): AnnouncementDialog {
        this.description = description
        return this
    }

    fun setTime(time: String?): AnnouncementDialog {
        this.time = time
        return this
    }

    fun setLevel(level: String?): AnnouncementDialog {
        this.level = level
        return this
    }

    override fun getTheme(): Int {
        return R.style.Dialog1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_announcement, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isCancelable = false
        scrollView.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, heightScreenSize(context) / 2)

        constraintSet.clone(constraintLayout)

        constraintSet.connect(acceptButton.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, context?.pickSize(R.dimen._10sdp) ?: 0)
        constraintSet.connect(acceptButton.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT,0)
        constraintSet.connect(acceptButton.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,0)

        constraintSet.connect(scrollView.id, ConstraintSet.TOP, timeTextView.id, ConstraintSet.BOTTOM, context?.pickSize(R.dimen._10sdp) ?: 0)

        constraintSet.applyTo(constraintLayout)

        timeTextView.isVisible = !time.isNullOrBlank()
        timeTextView.text = level//time//TimeHelper.convertDateText(time, TimeHelper.FORMAT_DATE_TEXT)

        titleTextView.isVisible = !title.isNullOrBlank()
        titleTextView.text = title ?: ""

        descTextView.isVisible = !description.isNullOrBlank()
        descTextView.text = description ?: ""

        acceptButton.setOnClickListener {
            eventClick(GeneralModel(id = uniqueId))
            dismiss()
        }
    }
}