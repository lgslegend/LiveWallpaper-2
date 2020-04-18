package novumlogic.live.wallpaper.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.service.wallpaper.WallpaperService;

import novumlogic.live.wallpaper.backends.AndroidApplicationConfiguration;
import novumlogic.live.wallpaper.backends.AndroidLiveWallpaperService;
import novumlogic.live.wallpaper.gdx.ApplicationListener;
import novumlogic.live.wallpaper.utility.ExtensionsKt;

public class MyLiveWallPaperService extends AndroidLiveWallpaperService {
    String Tag = "MyLiveWallPaperService——";
    LiveWallpaperApplicationListener myWallpaperListener;
    SharedPreferences sharedPreferences;
    AndroidApplicationConfiguration mConfig;

    public void onCreate() {
        super.onCreate();
        this.sharedPreferences = getSharedPreferences(getPackageName(), 0);
//        ExtensionsKt.initAd(getApplicationContext());
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public WallpaperService.Engine onCreateEngine() {
        return super.onCreateEngine();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreateApplication() {
        super.onCreateApplication();
        initialize(createListener(false), createConfig());
    }

    public AndroidApplicationConfiguration createConfig() {
        mConfig = new AndroidApplicationConfiguration();
        return mConfig;
    }

    public ApplicationListener createListener(boolean arg0) {
        myWallpaperListener = new LiveWallpaperApplicationListener(app.getContext());
        return myWallpaperListener;
    }
}
