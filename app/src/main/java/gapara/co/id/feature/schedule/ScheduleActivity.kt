package gapara.co.id.feature.schedule

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import gapara.co.id.R
import gapara.co.id.BR
import gapara.co.id.core.api.ApiRequest
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.response.BaseResponse
import gapara.co.id.core.api.response.CurrentScheduleEntity
import gapara.co.id.core.api.response.UserCurrentScheduleEntity
import gapara.co.id.core.base.*
import gapara.co.id.core.model.Urls
import gapara.co.id.core.model.UserModel
import gapara.co.id.databinding.ActivityScheduleBinding
import gapara.co.id.feature.brief.BriefViewModel
import gapara.co.id.feature.component.DateTabLayoutView
import gapara.co.id.feature.component.ShiftTabLayoutView
import gapara.co.id.feature.component.UserView
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleActivity :
    MvvmActivity<ActivityScheduleBinding, BriefViewModel>(BriefViewModel::class),
    CoroutineDeclare {

    private val calendar = Calendar.getInstance()
    private var styleRegular: Typeface? = null
    private var styleBold: Typeface? = null
    private var styleSemiBold: Typeface? = null
    private val todayServerDate = TimeHelper.convertToFormatDateSever(calendar.time)
    private val timestampNow = System.currentTimeMillis()

    override val layoutResource: Int = R.layout.activity_schedule

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        checkInOutView.isVisible = false
        showScrollView(false)
        registerObserver()
        setupAppBar()
        setupDateTabView()
        setupCheckInOutButton()
        viewModel.getCurrentMySchedule()
    }

    private fun setupCheckInOutButton() {
        checkOutButton.setOnClickListener {
            BaseDialogView(this).setMessage("Are you sure want to check out?")
                .showNegativeButton()
                .setOnClickPositive { patchCheckOut() }
                .show()
        }
        checkInButton.setOnClickListener {
            patchCheckIn()
        }
    }

    private fun patchCheckIn() {
        loadingCheckin.isVisible = true
        launch {
            val apiRequest = ApiRequest(Urls.PATCH_CHECK_IN + BaseApplication.sessionManager?.scheduleId)
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            if (response?.isSuccess() == true) {
                showLongToast(response.message ?: "Success check in")
            } else {
                showLongRedToast(response?.message ?: "Failed check in")
            }
            startActivity(onNewIntent(this@ScheduleActivity))
            finish()
            overridePendingTransition(0, 0)
        }
    }

    private fun patchCheckOut() {
        loadingCheckin.isVisible = true
        launch {
            val apiRequest = ApiRequest(Urls.PATCH_CHECK_OUT + BaseApplication.sessionManager?.scheduleId)
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            if (response?.isSuccess() == true) {
                showLongToast(response.message ?: "Success check out")
            } else {
                showLongRedToast(response?.message ?: "Failed check out")
            }
            startActivity(onNewIntent(this@ScheduleActivity))
            finish()
            overridePendingTransition(0, 0)
        }
    }

    private fun setupDateTabView() {
        scheduleDateTextView.text = getMonthAndYear(TimeHelper.getDate())
        val context = this@ScheduleActivity
        styleRegular = ResourcesCompat.getFont(context, R.font.font_regular)
        styleBold = ResourcesCompat.getFont(context, R.font.font_bold)
        styleSemiBold = ResourcesCompat.getFont(context, R.font.font_semi_bold)

        launch {
            setDateMinus3DaysTabView()
            setDateNowTabView()
            setDatePlus3DaysTabView()
        }
    }

    private fun getMonthAndYear(date: Date): String {
        val monthFormat = SimpleDateFormat("MMMM yyyy", TimeHelper.defaultLocale)
        return monthFormat.format(date)
    }

    private fun setDatePlus3DaysTabView() {
        for (index in 1 until 4) {
            val timestamp = timestampNow + (1000 * 60 * 60 * (24 * index))
            val date = TimeHelper.getDateByTimestamp(timestamp)
            dateTabView.addView(getDateTabView(date))
        }
    }

    private fun setDateNowTabView() {
        val date = TimeHelper.getDate()
        getSchedule(TimeHelper.convertToFormatDateSever(date))
        val itemView = getDateTabView(date)
        setDateSelectedTabView(itemView)
        dateTabView.addView(itemView)
    }

    private fun setDateMinus3DaysTabView() {
        for (index in 3 downTo 1) {
            val timestamp = timestampNow - (1000 * 60 * 60 * (24 * index))
            val date = TimeHelper.getDateByTimestamp(timestamp)
            dateTabView.addView(getDateTabView(date))
        }
    }

    private fun getDateTabView(date: Date): DateTabLayoutView {
        val dayTextFormat = SimpleDateFormat("EEE", TimeHelper.defaultLocale)
        val realTextDay = dayTextFormat.format(date)

        val dayFormat = SimpleDateFormat("dd", TimeHelper.defaultLocale)
        val realDay = dayFormat.format(date)

        val itemView = DateTabLayoutView(this)
        setDateUnselectedTabView(itemView)
        itemView.setTitle(realTextDay)
        itemView.setDesc(realDay)
        itemView.setClick {
            if (TimeHelper.convertToFormatDateSever(date) != viewModel.scheduleChooseDate) {
                scheduleDateTextView.text = getMonthAndYear(date)
                resetBackgroundDateTabView(itemView)
                getSchedule(TimeHelper.convertToFormatDateSever(date))
            }
        }
        return itemView
    }

    private fun setDateUnselectedTabView(itemView: DateTabLayoutView) {
        itemView.setTitleStyle(styleRegular)
        itemView.setDescStyle(styleSemiBold)
        itemView.setIsInvisibleLine(true)
        itemView.setTitleColor(R.color.grey6)
        itemView.setDescColor(R.color.grey6)
    }

    private fun setDateSelectedTabView(itemView: DateTabLayoutView) {
        itemView.setTitleStyle(styleBold)
        itemView.setDescStyle(styleBold)
        itemView.setIsInvisibleLine(false)
        itemView.setTitleColor(R.color.black)
        itemView.setDescColor(R.color.black)
    }

    private fun resetBackgroundDateTabView(itemViewSelected: DateTabLayoutView) {
        val count = dateTabView.childCount
        for (index in 0 until count) {
            if (dateTabView.getChildAt(index) is DateTabLayoutView) {
                (dateTabView.getChildAt(index) as DateTabLayoutView).apply {
                    setDateUnselectedTabView(this)
                }
            }
        }

        setDateSelectedTabView(itemViewSelected)
    }

    private fun showScrollView(isShow: Boolean) {
        scrollView.isVisible = isShow
    }

    private fun getSchedule(date : String = todayServerDate) {
        viewModel.getScheduleDate(date)
    }

    private fun setupAppBar() {
        detailAppBarView.setClickLeftImage {
            onBackPressed()
        }
        detailAppBarView.setClearImage()
        detailAppBarView.setClickRightImage {  }
        detailAppBarView.setTitleBar("Schedule")
    }

    private fun registerObserver() {
        viewModel.currentItemScheduleResponse.observe(this, {
            showScrollView(true)
            setupShiftTabView()
        })
        viewModel.currentScheduleUsersResponse.observe(this, {
            viewModel.currentItemScheduleResponse.value?.data?.schedules?.items?.forEachIndexed { index, model ->
                if (model.id == viewModel.schedule_id) {
                    setupUserView(model.users)
                }
            }
        })
        viewModel.message.observe(this, {
            it?.apply {
                showAlert(this@ScheduleActivity, this)
            }
        })
        viewModel.currentScheduleResponse.observe(this, { response ->
            response?.apply {
                val allSchedule = ArrayList<CurrentScheduleEntity>()
                val todaySchedule = ArrayList<CurrentScheduleEntity>()

                data?.schedules?.forEach {
                    if (it.real_start.isNullOrBlank().not() && it.real_end.isNullOrBlank()) {
                        allSchedule.add(it)
                    } else if (isScheduleTime(it.start, it.end)) {
                        todaySchedule.add(it)
                    }
                }

                val schedules = allSchedule.firstOrNull()
                val nowSchedule = todaySchedule.firstOrNull()
                if (schedules != null) {
                    BaseApplication.sessionManager?.scheduleId = schedules.id
                    setupCheckInOutView(schedules)
                } else if (nowSchedule != null) {
                    BaseApplication.sessionManager?.scheduleId = nowSchedule.id
                    setupCheckInOutView(nowSchedule)
                }
            }
        })
    }

    private fun setupCheckInOutView(currentSchedule: CurrentScheduleEntity) {
        checkInOutView.isVisible = isSupervisor()

        checkInButton.isEnabled = currentSchedule.real_start.isNullOrBlank()

        if (!currentSchedule.real_start.isNullOrBlank()) {
            checkInButton.setBackgroundResource(R.drawable.bg_btn_disable)
        } else {
            checkInButton.setBackgroundResource(R.drawable.bg_primary_button)
        }

        checkOutButton.isEnabled = currentSchedule.real_end.isNullOrBlank()

        if (!currentSchedule.real_end.isNullOrBlank()) {
            checkOutButton.setBackgroundResource(R.drawable.bg_red_disable)
        } else {
            checkOutButton.setBackgroundResource(R.drawable.bg_red_button)
        }

        checkInText.text = currentSchedule.real_start
        checkInText.isVisible = !currentSchedule.real_start.isNullOrBlank()
        checkOutView.isVisible = !currentSchedule.real_start.isNullOrBlank()
        checkOutText.isVisible = !currentSchedule.real_end.isNullOrBlank()
        checkOutText.text = currentSchedule.real_end
    }

    private fun setupShiftTabView() {
        launch {
            shiftTabView.removeAllViews()
            userView.removeAllViews()

            val items = viewModel.currentItemScheduleResponse.value?.data?.schedules?.items
            startView.isVisible = false
            endView.isVisible = false

            if (items.isNullOrEmpty()) {
                showLongRedToast("Schedule not found")
            }

            viewModel.currentItemScheduleResponse.value?.data?.schedules?.items?.forEachIndexed { index, model ->
                val itemView = ShiftTabLayoutView(this@ScheduleActivity)

                if (BaseApplication.sessionManager?.scheduleId == model.id) {
                    itemView.setTabBackground(R.drawable.bg_primary_button)
                    val start = TimeHelper.convertDateText(model.start, TimeHelper.FORMAT_DATE_FULL_TEXT, TimeHelper.FORMAT_DATE_FULL)
                    val end = TimeHelper.convertDateText(model.end, TimeHelper.FORMAT_DATE_FULL_TEXT, TimeHelper.FORMAT_DATE_FULL)

                    startView.isVisible = true
                    endView.isVisible = true
                    startDateTextView.text = start
                    endDateTextView.text = end
                    viewModel.getScheduleUsers(model.id)
                }

                if (model.id != viewModel.schedule_id) {
                    itemView.setTabBackground(0)
                }

                itemView.setClick {
                    val start = TimeHelper.convertDateText(model.start, TimeHelper.FORMAT_DATE_FULL_TEXT, TimeHelper.FORMAT_DATE_FULL)
                    val end = TimeHelper.convertDateText(model.end, TimeHelper.FORMAT_DATE_FULL_TEXT, TimeHelper.FORMAT_DATE_FULL)

                    startView.isVisible = true
                    endView.isVisible = true
                    startDateTextView.text = start
                    endDateTextView.text = end

                    resetBackgroundTabShift()
                    itemView.setTabBackground(R.drawable.bg_primary_button)
                    if (model.users?.size ?: 0 <= 1) {
                        viewModel.getScheduleUsers(model.id)
                    } else {
                        setupUserView(model.users)
                    }
                }
                itemView.setTitle(model.shift?.name)
                shiftTabView.addView(itemView)
            }
        }
    }

    private fun setupUserView(model: ArrayList<UserCurrentScheduleEntity>?) {
        launch {
            userView.removeAllViews()
            model?.forEachIndexed { index, it ->
                userView.addView(getUserView(it.user).hideLineView(index != model.size - 1))
            }
        }
    }

    private fun getTitleTextView(text: String): TextView {
        val textView = TextView(this@ScheduleActivity)
        textView.text = text
        textView.typeface = pickFont(R.font.font_bold)
        textView.setTextColor(pickColor(R.color.black))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, pickSize(R.dimen._4sdp).toFloat())
        val paddingLefRight = pickSize(R.dimen._8sdp)
        val paddingTop = pickSize(R.dimen._10sdp)
        val paddingBottom = pickSize(R.dimen._2sdp)
        textView.setPadding(paddingLefRight, paddingTop, paddingLefRight, paddingBottom)
        return textView
    }

    private fun getUserView(model: UserModel?): UserView {
        val itemView = UserView(this@ScheduleActivity)
        itemView.setUrl(model?.avatar)
        val spannable = SpannableStringBuilder("${model?.name}\n${model?.phone ?: model?.email ?: model?.role?.name}")

        val usernameLastIndex = spannable.toString().indexOf("\n")
        spannable.setSpan(ForegroundColorSpan(pickColor(R.color.blue2)), 0, usernameLastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(ForegroundColorSpan(pickColor(R.color.grey5)), usernameLastIndex, spannable.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        itemView.setUsernameWithDescription(spannable)
        itemView.hideRightImage()
        return itemView
    }

    private fun resetBackgroundTabShift() {
        val count = shiftTabView.childCount
        for (index in 0 until count) {
            if (shiftTabView.getChildAt(index) is ShiftTabLayoutView) {
                (shiftTabView.getChildAt(index) as ShiftTabLayoutView).setTabBackground(0)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_down)
    }

    companion object {
        fun onNewIntent(context: Context): Intent {
            val intent = Intent(context, ScheduleActivity::class.java)
            return intent
        }
    }
}