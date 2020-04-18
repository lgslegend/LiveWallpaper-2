package novumlogic.live.wallpaper.settings

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dialog_auto_wallpaper_source.*
import novumlogic.live.wallpaper.R
import novumlogic.live.wallpaper.settings.listeners.OnAutoWallpaperSourceSelected
import novumlogic.live.wallpaper.utility.AppConstants
import novumlogic.live.wallpaper.utility.PermissionsUtil

/**
 * Created by Priya Sindkar.
 */
class AutoWallpaperSourceBottomDialogFragment : BottomSheetDialogFragment() {
    lateinit var onAutoWallpaperSourceSelected: OnAutoWallpaperSourceSelected

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_auto_wallpaper_source, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtChooseGallery.setOnClickListener {
//            if (PermissionsUtil.checkPermissions(activity!!, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
                onAutoWallpaperSourceSelected.onSourceSelected(AppConstants.WallpaperType.AUTO_ROTATE_GALLERY.typeId)
                dismiss()
//            }
        }

        txtChooseOnline.setOnClickListener {
            onAutoWallpaperSourceSelected.onSourceSelected(AppConstants.WallpaperType.AUTO_ROTATE_ONLINE_IMAGE.typeId)
            dismiss()
        }
    }
}