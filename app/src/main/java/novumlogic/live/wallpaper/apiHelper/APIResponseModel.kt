package novumlogic.live.wallpaper.apiHelper

import com.google.gson.annotations.SerializedName

data class APIResponseModel(
        @SerializedName("AppData") val appDatumModels: List<AppDataModel>?,
        @SerializedName("success") val success: Int
)