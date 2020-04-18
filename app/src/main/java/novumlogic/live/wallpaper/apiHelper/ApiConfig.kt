package novumlogic.live.wallpaper.apiHelper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiConfig private constructor() {

    private object Holder {
        val INSTANCE = ApiConfig()
    }

    companion object {
        val HTTP_PREFIX = "http"
        val BASE_URL = "android.resource://novumlogic.live.wallpaper/drawable/"
        val instance: ApiConfig by lazy { Holder.INSTANCE }

    }
}