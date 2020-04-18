package novumlogic.live.wallpaper.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.WindowManager

import novumlogic.live.wallpaper.R




fun Activity.enableUserTouch() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun Activity.disableUserTouch() {
    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun Activity.setLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = window.decorView.systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flags
    }
}

fun Activity.checkInternetConnection(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
}

fun Activity.showNoInternetDialog() {
    val alertDialogBuilder = AlertDialog.Builder(this)
    alertDialogBuilder.setMessage(getString(R.string.msg_check_internet))
    alertDialogBuilder.setCancelable(false)

    alertDialogBuilder.setPositiveButton(getString(R.string.label_go_to_settings)) { dialog, _ ->
        startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        dialog.dismiss()
    }

    alertDialogBuilder.setNegativeButton(getString(R.string.label_cancel)) { dialog, _ ->
        dialog.dismiss()
    }

    val alertDialog = alertDialogBuilder.create()
    alertDialog.show()
}

