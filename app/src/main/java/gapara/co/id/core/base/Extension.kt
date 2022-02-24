package gapara.co.id.core.base

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Vibrator
import android.text.Html
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import gapara.co.id.BuildConfig
import gapara.co.id.R
import gapara.co.id.core.api.log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun hideKeyboard(context: Context) {
    val view = (context as Activity).currentFocus
    if (view != null) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun showKeyboard(context: Context) {
    val view = (context as Activity).currentFocus
    view?.apply {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun showKeyboardFocusEditText(editText: EditText) {
    editText.requestFocus()
    val imm = editText.context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun showAlert(context: Context?, message: String, backgroundId : Int = R.color.colorPrimaryDark, duration : Int = Toast.LENGTH_SHORT) {
    context?.apply {
        val toast = Toast.makeText(this, message, duration)
        val view = View.inflate(this, R.layout.view_toast, null)
        view.findViewById<TextView>(R.id.textToast).text = message
        view.findViewById<CardView>(R.id.toastCardView).setCardBackgroundColor(pickColor(backgroundId))
        toast.view = view
        toast.show()
    }
}

fun String.isValidEmail(): Boolean = this.isNotEmpty() && this.isNotBlank() &&
        Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isNumber(): Boolean {
    return matches(Regex("^[0-9]+$"))
}

fun isDigitAndUpperLowerCaseWithSymbol(input: String): Boolean {
    val specialChars = "~`!@#$%^&*()-_=+\\|[{]};:'\",<.>/?"
    var currentCharacter: Char
    var numberPresent = false
    var upperCasePresent = false
    var lowerCasePresent = false
    var specialCharacterPresent = false
    for (i in 0 until input.length) {
        currentCharacter = input[i]
        if (Character.isDigit(currentCharacter)) {
            numberPresent = true
        } else if (Character.isUpperCase(currentCharacter)) {
            upperCasePresent = true
        } else if (Character.isLowerCase(currentCharacter)) {
            lowerCasePresent = true
        } else if (specialChars.contains(currentCharacter.toString())) {
            specialCharacterPresent = true
        }
    }
    return numberPresent && upperCasePresent && lowerCasePresent && specialCharacterPresent
}

fun isDigitAndUpperLowerCase(input: String): Boolean {
    var currentCharacter: Char
    var numberPresent = false
    var upperCasePresent = false
    var lowerCasePresent = false
    for (i in 0 until input.length) {
        currentCharacter = input[i]
        if (Character.isDigit(currentCharacter)) {
            numberPresent = true
        } else if (Character.isUpperCase(currentCharacter)) {
            upperCasePresent = true
        } else if (Character.isLowerCase(currentCharacter)) {
            lowerCasePresent = true
        }
    }
    return numberPresent && upperCasePresent && lowerCasePresent
}

fun ImageView.loadGlide(urlOri: String?, isSuccess: ((Boolean) -> Unit)? = null) {

    val url = urlOri ?: ""
    if (context != null && !(context as Activity).isFinishing) {
        Glide.with(this.context)
                .load(url)
                .apply(RequestOptions().placeholder(R.drawable.place_holder))
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        isSuccess?.let { it(false) }
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        isSuccess?.let { it(true) }
                        return false
                    }
                }).into(this)
    }
}

@BindingAdapter("loadUrl")
fun ImageView.loadUrl(url: String?) {
    if (context != null && !(context as Activity).isFinishing) {
        url?.let {
            Glide.with(context)
                .load(it)
                .apply(RequestOptions())
                .placeholder(R.drawable.place_holder)
                .into(this)
        }
    }
}

@set:BindingAdapter("isVisible")
inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

fun String.isIndonesianMobilePhoneNumber(): Boolean {
    return this.matches(Regex("^[0][8][0-9]{6,13}\$")) || this.matches(Regex("^[6][2][0-9]{6,13}\$"))
}

fun Context.inflate(layoutId: Int, viewGroup: ViewGroup?): View {
    return LayoutInflater.from(this).inflate(layoutId, viewGroup, false)
}

fun Activity.getScreenInches(): Double {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    val widthPixels = displayMetrics.widthPixels
    val heightPixels = displayMetrics.heightPixels
    val calculateXdpi = widthPixels.toDouble() / displayMetrics.xdpi.toDouble()
    val calculateYdpi = heightPixels.toDouble() / displayMetrics.ydpi.toDouble()
    val xdpi = Math.pow(calculateXdpi, 2.0)
    val ydpi = Math.pow(calculateYdpi, 2.0)
    return Math.sqrt(xdpi + ydpi)
}


fun widthScreenSize(context: Context?): Int {
    if (context == null) return 0
    val metrics = DisplayMetrics()
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    wm.defaultDisplay.getMetrics(metrics)
    return metrics.widthPixels
}

fun heightScreenSize(context: Context?): Int {
    if (context == null) return 0
    val metrics = DisplayMetrics()
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    wm.defaultDisplay.getMetrics(metrics)
    return metrics.heightPixels
}

fun setVibrator(context: Context) {
    val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (v.hasVibrator()) {
        v.vibrate(1000)
    }
}

fun makeHandler(duration: Long, runnable: () -> Unit) {
    val handler = Handler()
    handler.postDelayed(runnable, duration)
}

fun Activity.pickDrawable(value: Int): Drawable? {
    return AppCompatResources.getDrawable(this, value)
}

fun getTodayDateString(format: String = "EEEE, dd MMMM yyyy", setupCalendar: Calendar? = null, locale : Locale = Locale("id")): String {
    val formatter = SimpleDateFormat(format, locale)

    var calendar = setupCalendar
    if (calendar == null) {
        calendar = Calendar.getInstance()
    }

    return formatter.format(calendar?.time ?: Date(System.currentTimeMillis()))
}

fun <T> ViewModel.mutableLiveDataOf() = MutableLiveData<T>()

object RemoteConfigHelper {

    fun get(key: String): String {
        setRefreshRemoteConfig {}
        return BaseApplication.baseApplication?.remoteConfig?.getString(key) ?: ""
    }

    fun getLong(key: String): Long {
        return BaseApplication.baseApplication?.remoteConfig?.getLong(key) ?: 0
    }

    fun getBoolean(key: String): Boolean {
        return BaseApplication.baseApplication?.remoteConfig?.getBoolean(key) ?: false
    }

    fun getApiUrl() : String {
        if (BuildConfig.FLAVOR == FLAVOR_STAGING) { return BuildConfig.BASE_URL }
        if (BuildConfig.FLAVOR == FLAVOR_KARIS) { return BuildConfig.BASE_URL }
        return BaseApplication.sessionManager?.baseUrl.takeIf { !it.isNullOrBlank() }?.run {
            this
        } ?: kotlin.run {
            BuildConfig.BASE_URL
        }
    }
}

fun TextView.setHtmlText(text: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
    } else {
        this.text = Html.fromHtml(text)
    }
}

@set:BindingAdapter("isInvisible")
inline var View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) {
        visibility = if (value) View.INVISIBLE else View.VISIBLE
    }

fun SwipeRefreshLayout.setThemePrimary() {
    setColorSchemeResources(R.color.colorPrimaryDark,
        R.color.colorPrimaryDark,
        R.color.colorPrimaryDark)
}

fun Context.pickColor(color : Int) : Int {
    return ContextCompat.getColor(this, color)
}
fun Context.pickFont(font : Int) : Typeface {
    return ResourcesCompat.getFont(this, font) ?: Typeface.DEFAULT
}

fun Context.pickSize(dimen : Int) : Int {
    return resources.getDimensionPixelSize(dimen)
}

fun removeSymbol(message: String?) : String? {
    return message?.replace("[", "")?.replace("]", "")
}

fun getErrorMessageServer(message: String?) : String {
    var msg = ""
    removeSymbol(message)?.split(",")?.forEach {
        msg += "*${it.trim()}\n\n"
    }
    return msg
}

fun isChief() : Boolean {
    return BaseApplication.sessionManager?.isChief() == true
}

fun isClient() : Boolean {
    return BaseApplication.sessionManager?.isClient() == true
}

fun isSupervisor() : Boolean {
    return BaseApplication.sessionManager?.isSupervisor() == true
}

fun isIntel() : Boolean {
    return BaseApplication.sessionManager?.isIntel() == true
}

fun isDropOff() : Boolean {
    return BaseApplication.sessionManager?.isDropOff() == true
}

fun isGuard() : Boolean {
    return BaseApplication.sessionManager?.isGuard() == true
}

fun isPatrol() : Boolean {
    return BaseApplication.sessionManager?.isPatrol() == true
}

fun setRefreshRemoteConfig(callback: () -> Unit) {
    BaseApplication.baseApplication?.setupFirebaseRemoteConfig {
        BaseApplication.baseApplication?.setupFirebaseRemoteConfig {
            callback()
        }
    }
}

const val FLAVOR_STAGING = "staging"
const val FLAVOR_KARIS = "karis"

fun checkVersion(activity : Activity) {
    if (BuildConfig.FLAVOR == FLAVOR_STAGING) { return }
    val versionApps = RemoteConfigHelper.getLong(RemoteConfigKey.VERSION_APPS).toInt()
    val isForceUpdate = RemoteConfigHelper.getBoolean(RemoteConfigKey.IS_FORCE_UPDATE)
    if (versionApps > 0 && (BuildConfig.VERSION_CODE != versionApps)) {
        showUpdateAppsDialog(activity, isForceUpdate)
    }
}

fun showUpdateAppsDialog(activity: Activity, isForceUpdate : Boolean) {
    try {
        val updateAppsDialog = BaseDialogView(activity)
            .setTitle("New version available")
            .setMessage("Please, update ${activity.getString(R.string.app_name)} apps to new version!")
            .setPositiveString("Yes")
            .setNegativeString("No")
            .setOnClickNegative {}
            .setOnClickPositive {
                if (isForceUpdate) {
                    activity.finishAffinity()
                }
                openPlayStore(activity)
            }

        if (isForceUpdate) {
            updateAppsDialog.hideNegativeButton()
        }

        updateAppsDialog.show()
    } catch (exception : java.lang.Exception) {
        log("exception $exception")
    }
}

fun openPlayStore(context: Context) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=${context.packageName}")))
}

fun Context.showLongToast(message: String) {
    showAlert(this, message, R.color.green2, Toast.LENGTH_LONG)
}

fun Context.showLongRedToast(message: String) {
    showAlert(this, message, R.color.red1, Toast.LENGTH_LONG)
}

val cameraPermission = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE)

val fullPermission = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_FINE_LOCATION)

fun setTryCatch(callback: () -> Unit) {
    try {
        callback()
    } catch (e : Exception) {
        e.printStackTrace()
        FirebaseApplication.recordException(e.cause ?: Throwable("Error bro $e"), "$e ===== ${e.message}")
    }
}

fun TextView.setDueDateColor(dueDate : String?, isPending: Boolean?, isComplete : Boolean?, isRequestComplete : Boolean?) {
    setTryCatch {
        val serverFormat = SimpleDateFormat(TimeHelper.FORMAT_DATE_FULL, TimeHelper.defaultLocale)
        val dateFormatApi = serverFormat.parse(dueDate)
        val timestampApi = dateFormatApi?.time ?: 0
        val timestampNow = System.currentTimeMillis()
        if (timestampNow > timestampApi && (isPending == true || isRequestComplete == true)) {
            setTextColor(context.pickColor(R.color.red1))
        } else if ((isPending == true && timestampNow < timestampApi) || isComplete == true) {
            setTextColor(context.pickColor(R.color.green1))
        }
    }
}

fun getDetailReportDevice() : String {
    val LINE_SEPARATOR = "\n"
    val errorReport = StringBuilder()
    errorReport.append("\n************ DEVICE INFORMATION ***********\n")
    errorReport.append("Brand: ")
    errorReport.append(Build.BRAND)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Device: ")
    errorReport.append(Build.DEVICE)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Model: ")
    errorReport.append(Build.MODEL)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Id: ")
    errorReport.append(Build.ID)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Product: ")
    errorReport.append(Build.PRODUCT)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("\n************ FIRMWARE ************\n")
    errorReport.append("SDK: ")
    errorReport.append(Build.VERSION.SDK)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Release: ")
    errorReport.append(Build.VERSION.RELEASE)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Incremental: ")
    errorReport.append(Build.VERSION.INCREMENTAL)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Time: ")
    val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    val cal = Calendar.getInstance()
    errorReport.append(dateFormat.format(cal.time))
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Version Application : ${BuildConfig.VERSION_CODE} ${BuildConfig.VERSION_NAME}")
    errorReport.append(LINE_SEPARATOR)
    return errorReport.toString()
}

fun isScheduleTime(start: String?, end: String?) : Boolean {
    return try {
        val todayTime = Calendar.getInstance().time.time / 1000
        val serverFormat = SimpleDateFormat(TimeHelper.FORMAT_DATE_FULL, TimeHelper.defaultLocale)
        val startDate = serverFormat.parse(start).time / 1000
        val endDate = serverFormat.parse(end).time / 1000

        return todayTime > startDate && todayTime < endDate
    } catch (e : java.lang.Exception) {
        e.printStackTrace()
        false
    }
}