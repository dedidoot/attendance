package gapara.co.id.core.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.MediaModel
import kotlinx.android.parcel.Parcelize

data class ProfileResponse(@SerializedName("data") val data: ProfileModel? = null) : BaseResponse()

@Parcelize
data class ProfileModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("place_of_birth") var birthPlace: String? = null,
    @SerializedName("date_of_birth") var birthday: String? = null,
    @SerializedName("gender") var gender: String? = null,
    @SerializedName("house_number") var houseNumber: String? = null,
    @SerializedName("rt") var rt: String? = null,
    @SerializedName("postal_code") var postalCode: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("latitude") var latitude: Double? = null,
    @SerializedName("longitude") var longitude: Double? = null,
    @SerializedName("avatar") val avatar: MediaModel? = null,
    @SerializedName("hamlet") val hamlet: Hamlet? = null,
    @SerializedName("emergency_contact") val emergencyContact: EmergencyContact? = null,
    @SerializedName("citizen_card_image") val citizen_card_image: MediaModel? = null,
    @SerializedName("citizen_card_selfie") val citizen_card_selfie: MediaModel? = null,
    @SerializedName("can_create_report") val can_create_report: Boolean? = null,
    @SerializedName("citizen_card_id") val citizen_card_id: String? = null
): Parcelable

@Parcelize
data class EmergencyContact(
    @SerializedName("full_name") val full_name: String? = null,
    @SerializedName("relationship") val relationship: String? = null,
    @SerializedName("phone_number") val phone_number: String? = null
) : Parcelable

@Parcelize
data class Hamlet(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("village") val village: Village? = null
) : Parcelable

@Parcelize
data class Village(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("sub_district") val subDistrict: SubDistrict? = null
) : Parcelable

@Parcelize
data class SubDistrict(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("city") val city: City? = null
) : Parcelable

@Parcelize
data class City(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("province") val province: Province? = null
) : Parcelable

@Parcelize
data class Province(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null
) : Parcelable