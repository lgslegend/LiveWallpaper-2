package novumlogic.live.wallpaper.base

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import novumlogic.live.wallpaper.R

import novumlogic.live.wallpaper.utility.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class SplashActivity : AppCompatActivity() {
    var startTime: Long = 0

    private var isFirstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        startTime = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        if (isFirstTime) {
            isFirstTime = false

            GlobalScope.launch {

                    openLiveWallpaperPreview()

            }


        }
    }

    /**
     * Result callback from permission
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionsUtil.FILES_READ_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openLiveWallpaperPreview()
                } else {
                    // code for deny
                    finish()
                }
            }
        }
    }


    private fun openLiveWallpaperPreview() {
        if (fetchWallpaperType() == AppConstants.WallpaperType.AUTO_ROTATE_GALLERY.typeId || fetchWallpaperType() == AppConstants.WallpaperType.GALLERY.typeId) {
            if (PermissionsUtil.checkPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        ComponentName(this@SplashActivity, MyLiveWallPaperService::class.java))
                startActivity(intent)
                finish()
            }
        } else {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(this@SplashActivity, MyLiveWallPaperService::class.java))
            startActivity(intent)
            finish()
        }
    }





}
