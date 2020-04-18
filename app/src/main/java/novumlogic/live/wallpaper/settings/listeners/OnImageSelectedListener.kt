package novumlogic.live.wallpaper.settings.listeners

/**
 * Created by Priya Sindkar.
 */
interface OnImageSelectedListener {
    fun onImageSelected(imageURI: String, imageId: String)
    fun onAdSelected(redirectURL: String)
}