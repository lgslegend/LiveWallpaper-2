package novumlogic.live.wallpaper.apiHelper

import com.google.gson.annotations.SerializedName

data class AppDataModel(
        @SerializedName("Wallpapers") val wallpapers: List<Wallpaper>,
        @SerializedName("MoreApps") val moreApps: List<MoreApp>
)