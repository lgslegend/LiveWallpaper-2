package novumlogic.live.wallpaper.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import novumlogic.live.wallpaper.R
import novumlogic.live.wallpaper.base.SplashActivity
import novumlogic.live.wallpaper.settings.GallerySourceImageListActivity


/**
 * Created by Priya Sindkar.
 */
class PermissionsUtil {

    companion object {
        val FILES_READ_PERMISSION_REQUEST = 101

        fun checkPermissions(context: Context, permissionsArray: Array<String>): Boolean {
            val currentAPIVersion = Build.VERSION.SDK_INT
            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                permissionsArray.forEach {
                    if (ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED) {
                        //allowed
//                            return true
                    } else {
                        if (!shouldShowRequestPermissionRationale(context as Activity, it)) {
                            //denied
                            requestPermissions(context, permissionsArray, FILES_READ_PERMISSION_REQUEST)
                            return false
                        } else {
                            //set to never ask again
                            val dialog = AlertDialog.Builder(context, R.style.MyDialogTheme).create()
                            dialog.setMessage(context.getString(R.string.msg_need_permission_access))
                            dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.label_allow)) { _, _ ->
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package", context.packageName, null)
                                intent.data = uri
                                context.startActivity(intent)
                            }
                            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.label_deny)) { _, _ ->
                                if (context is SplashActivity || context is GallerySourceImageListActivity)
                                    context.finish()
                                dialog.dismiss()
                            }
                            dialog.show()
                            return false
                        }
                    }
                }
                return true
            } else {
                return true
            }
        }

    }
}