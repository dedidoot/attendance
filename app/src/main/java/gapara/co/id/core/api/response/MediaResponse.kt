package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.Media2Model
import gapara.co.id.core.model.MediaModel
import java.io.File

data class MediaResponse(@SerializedName("data") val mediaModel: MediaModel? = null) : BaseResponse()

data class Media2Response(@SerializedName("data") val data: Media2Model? = null) : BaseResponse()