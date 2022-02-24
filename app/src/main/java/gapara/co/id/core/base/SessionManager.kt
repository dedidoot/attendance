package gapara.co.id.core.base

import android.content.Context
import android.content.SharedPreferences

open class SessionManager(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences("app_session", 0)
    private val editor: SharedPreferences.Editor = pref.edit()

    private val userIdSession = "user_id"
    private val accessTokenSession = "access_token"
    private val latitudeSession = "latitude_session"
    private val longitudeSession = "longitude_session"
    private val fcmTokenSession = "fcm_token_session"
    private val emailSession = "email"
    private val fullNameSession = "full_name"
    private val loginParamsSession = "login_params_session"
    private val avatarSession = "avatar_session"
    private val roleSession = "role_session"
    private val baseUrlSession = "base_url_session"
    private val scheduleIdSession = "schedule_id_session"
    private val branchIdSession = "branch_id_session"

    var fullName: String?
        get() = pref.getString(fullNameSession, "")
        set(value) = editor.putString(fullNameSession, value).apply()

    var avatar: String?
        get() = pref.getString(avatarSession, "")
        set(value) = editor.putString(avatarSession, value).apply()

    var role: String?
        get() = pref.getString(roleSession, "")
        set(value) = editor.putString(roleSession, value).apply()

    var loginParams: String?
        get() = pref.getString(loginParamsSession, "")
        set(value) = editor.putString(loginParamsSession, value).apply()

    var email: String?
        get() = pref.getString(emailSession, "")
        set(value) = editor.putString(emailSession, value).apply()

    var userId: String?
        get() = pref.getString(userIdSession, "")
        set(value) = editor.putString(userIdSession, value).apply()

    var accessToken: String?
        get() = pref.getString(accessTokenSession, "")
        set(value) = editor.putString(accessTokenSession, value).apply()

    var latitude: String?
        get() = pref.getString(latitudeSession, "")
        set(value) = editor.putString(latitudeSession, value).apply()

    var longitude: String?
        get() = pref.getString(longitudeSession, "")
        set(value) = editor.putString(longitudeSession, value).apply()

    var fcmToken: String?
        get() = pref.getString(fcmTokenSession, "")
        set(value) = editor.putString(fcmTokenSession, value).apply()

    var baseUrl: String?
        get() = pref.getString(baseUrlSession, null)
        set(value) = editor.putString(baseUrlSession, value).apply()

    var scheduleId: String?
        get() = pref.getString(scheduleIdSession, null)
        set(value) = editor.putString(scheduleIdSession, value).apply()

    var branchId: String?
        get() = pref.getString(branchIdSession, null)
        set(value) = editor.putString(branchIdSession, value).apply()

    fun isLastLocationNotEmpty() : Boolean {
        return !latitude.isNullOrEmpty() && !longitude.isNullOrEmpty()
    }

    fun clearSession() {
        editor.clear()
        editor.commit()
    }

    fun isChief() : Boolean {
        return role?.equals("chief", ignoreCase = true) == true
    }

    fun isClient() : Boolean {
        return role?.equals("client", ignoreCase = true) == true
    }

    fun isSupervisor() : Boolean {
        return role?.equals("supervisor", ignoreCase = true) == true
    }

    fun isIntel() : Boolean {
        return role?.equals("intel", ignoreCase = true) == true
    }

    fun isGuard() : Boolean {
        return role?.equals("Guard", ignoreCase = true) == true
    }

    fun isPatrol() : Boolean {
        return role?.equals("Patrol", ignoreCase = true) == true
    }

    fun isDropOff() : Boolean {
        return role?.equals("drop off", ignoreCase = true) == true || role?.equals("drop_off", ignoreCase = true) == true
    }
}