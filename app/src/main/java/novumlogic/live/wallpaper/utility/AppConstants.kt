package novumlogic.live.wallpaper.utility

/**
 * Created by Priya Sindkar.
 */

class AppConstants {
    companion object {
        val APP_PREFS_CONTEXT = "APP_PREFS_CONTEXT"
        val APP_IMAGE_INFO = "APP_IMAGE_INFO"
        val ENABLE_RIPPLE_EFFECT = "ENABLE_RIPPLE_EFFECT"
        val ENABLE_WALLPAPER_MUSIC = "ENABLE_WALLPAPER_MUSIC"
        val ENABLE_RAIN_DROPS = "ENABLE_RAIN_DROPS"
        val INTERNAL_IMAGE_DIR = "imageDir"
        val IMAGE_TO_PREVIEW = "IMAGE_TO_PREVIEW"
        var WALLPAPER_CHANGER_IMAGE_LIST = "WALLPAPER_CHANGER_IMAGE_LIST"
        var AUTO_WALLPAPER_ONLINE_IMAGE_LIST = "AUTO_WALLPAPER_ONLINE_IMAGE_LIST"
        var AUTO_WALLPAPER_GALLERY_IMAGE_LIST = "AUTO_WALLPAPER_GALLERY_IMAGE_LIST"
        var WALLPAPER_TYPE = "WALLPAPER_TYPE"
    }

    enum class WallpaperType(val typeId: Int) {
        GALLERY (0) , ONLINE_IMAGE(1), AUTO_ROTATE_GALLERY(2), AUTO_ROTATE_ONLINE_IMAGE(3)
    }
}
