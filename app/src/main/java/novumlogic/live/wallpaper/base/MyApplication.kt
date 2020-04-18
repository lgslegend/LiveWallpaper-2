package novumlogic.live.wallpaper.base

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.facebook.drawee.backends.pipeline.Fresco


class MyApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(applicationContext)
    }

    protected override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}