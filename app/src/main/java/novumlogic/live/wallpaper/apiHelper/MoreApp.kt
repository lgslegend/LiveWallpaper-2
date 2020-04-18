package novumlogic.live.wallpaper.apiHelper

import com.google.gson.annotations.SerializedName

data class MoreApp(
        @SerializedName("id") val id: String,
        @SerializedName("title") val title: String,
        @SerializedName("imagePath") val imagePath: String,
        @SerializedName("url") val url: String
)