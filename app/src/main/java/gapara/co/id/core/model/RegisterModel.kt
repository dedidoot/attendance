package gapara.co.id.core.model

import com.google.gson.annotations.SerializedName

data class RegisterModel(
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("password_confirmation") var passwordConfirmation: String? = null,

    @SerializedName("agreement") var agreement: Int? = null,

    @SerializedName("name") var name: String? = null,
    @SerializedName("place_of_birth") var birthPlace: String? = null,
    @SerializedName("date_of_birth") var birthday: String? = null,
    @SerializedName("gender") var gender: String? = null,
    @SerializedName("citizen_card_id") var citizenCardId: String? = null,

    @SerializedName("phone") var phone: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("house_number") var houseNumber: String? = null,
   // @SerializedName("rt_rw") var rtRw: String? = null,
    @SerializedName("postal_code") var postalCode: String? = null,

    @SerializedName("province") var province: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("sub_district_id") var subDistrictId: String? = null,
    @SerializedName("village_id") var villageId: String? = null,

    @SerializedName("emergency_contact_full_name") var emergencyContactFullName: String? = null,
    @SerializedName("emergency_contact_relationship") var emergencyContactRelationship: String? = null,
    @SerializedName("emergency_contact_phone_number") var emergencyContactPhoneNumber: String? = null,

    @SerializedName("avatar") var avatarId: String? = null,
    var avatarUrlSession: String? = null,

    @SerializedName("latitude") var latitude: Double? = null,
    @SerializedName("longitude") var longitude: Double? = null,
    @SerializedName("citizen_card_image") var citizenCardImageId: String? = null,
    @SerializedName("citizen_card_selfie") var citizenCardSelfieId: String? = null,

    // for case edit RegisterProfileActivity
    @SerializedName("rt") var rt: String? = null,
    @SerializedName("hamlet_id") var hamlet_id: String? = null
)