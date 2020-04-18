package novumlogic.live.wallpaper.utility

import android.content.Context
import com.google.gson.Gson
import novumlogic.live.wallpaper.apiHelper.APIResponseModel

/**
 * Created by Priya Sindkar.
 */

fun Context.saveAPIInfo(response: APIResponseModel) {
    getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).edit().putString(AppConstants.APP_IMAGE_INFO, Gson().toJson(response)).apply()
}

fun Context.fetchAPIInfo(): APIResponseModel? {
    val jsonList = getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).getString(AppConstants.APP_IMAGE_INFO, "")
    return Gson().fromJson(jsonList, APIResponseModel::class.java)
}

fun Context.saveImageToPreview(imagePath: String) {
    getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).edit().putString(AppConstants.IMAGE_TO_PREVIEW, imagePath).apply()
}

fun Context.removeImageToPreview() {
    getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).edit().remove(AppConstants.IMAGE_TO_PREVIEW).apply()
}

fun Context.fetchImageToPreview(): String {
    return getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).getString(AppConstants.IMAGE_TO_PREVIEW, "")
}

fun Context.saveBackgroundMusicSetting(isChecked: Boolean) {
    getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).edit().putBoolean(AppConstants.ENABLE_WALLPAPER_MUSIC, isChecked).apply()
}

fun Context.fetchBackgroundMusicSetting(): Boolean {
    return getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).getBoolean(AppConstants.ENABLE_WALLPAPER_MUSIC, false)
}

fun Context.saveRippleSetting(isChecked: Boolean) {
    getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).edit().putBoolean(AppConstants.ENABLE_RIPPLE_EFFECT, isChecked).apply()
}

fun Context.fetchRippleSetting(): Boolean {
    return getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).getBoolean(AppConstants.ENABLE_RIPPLE_EFFECT, true)
}

fun Context.saveRainDropSetting(isChecked: Boolean) {
    getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).edit().putBoolean(AppConstants.ENABLE_RAIN_DROPS, isChecked).apply()
}

fun Context.fetchRainDropSetting(): Boolean {
    return getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).getBoolean(AppConstants.ENABLE_RAIN_DROPS, true)
}

fun Context.saveAutoWallpaperOnlineList(listOfImages: HashMap<String, String>) {
    getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).edit().putString(AppConstants.AUTO_WALLPAPER_ONLINE_IMAGE_LIST, Gson().toJson(listOfImages)).apply()
}

fun Context.fetchAutoWallpaperOnlineList(): HashMap<String, String> {
    val jsonList = getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).getString(AppConstants.AUTO_WALLPAPER_ONLINE_IMAGE_LIST, "")
    if (jsonList.isNullOrEmpty())
        return HashMap<String, String>()
    return Gson().fromJson(jsonList, HashMap::class.java) as HashMap<String, String>
}

fun Context.fetchAutoWallpaperOnlineImageList(): ArrayList<String> {
    val hashMap = fetchAutoWallpaperOnlineList()
    return if (hashMap.isNotEmpty()) {
        hashMap.asIterable().map { it.value } as ArrayList<String>
    } else {
        emptyList<String>() as ArrayList<String>
    }
}

fun Context.saveAutoWallpaperGalleryList(listOfImages: ArrayList<String>) {
    getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).edit().putString(AppConstants.AUTO_WALLPAPER_GALLERY_IMAGE_LIST, Gson().toJson(listOfImages)).apply()
}

fun Context.removeAutoWallpaperGalleryList() {
    getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).edit().remove(AppConstants.AUTO_WALLPAPER_GALLERY_IMAGE_LIST).apply()
}

fun Context.fetchAutoWallpaperGalleryList(): ArrayList<String> {
    val jsonList = getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).getString(AppConstants.AUTO_WALLPAPER_GALLERY_IMAGE_LIST, "")
    if (jsonList.isNullOrEmpty())
        return ArrayList<String>()
    return Gson().fromJson(jsonList, ArrayList::class.java) as ArrayList<String>
}

fun Context.saveWallpaperType(type: Int) {
    getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).edit().putInt(AppConstants.WALLPAPER_TYPE, type).apply()
}

fun Context.removeWallpaperType() {
    getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).edit().remove(AppConstants.WALLPAPER_TYPE).apply()
}

fun Context.fetchWallpaperType(): Int {
    return getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).getInt(AppConstants.WALLPAPER_TYPE, -1)
}