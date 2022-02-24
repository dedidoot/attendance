package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class GeneralModel(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("index")
    var index: Int? = null,
    @SerializedName("is_checked")
    var isChecked: Boolean? = null,
    @SerializedName("branch_id")
    var branch_id: String? = null
) : Parcelable {

    fun isPatrol() : Boolean {
        return name?.equals("patrol", ignoreCase = true) == true
    }

    fun isGuard() : Boolean {
        return name?.equals("guard", ignoreCase = true) == true
    }

    fun isSupervisor() : Boolean {
        return name?.equals("supervisor", ignoreCase = true) == true
    }

}