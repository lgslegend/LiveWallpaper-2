package novumlogic.live.wallpaper.apiHelper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by Priya Sindkar.
 */
class ApiConfig private constructor() {

    private object Holder {
        val INSTANCE = ApiConfig()
    }

    companion object {
        val HTTP_PREFIX = "http"
        val BASE_URL = "android.resource://novumlogic.live.wallpaper/drawable/"
        val instance: ApiConfig by lazy { Holder.INSTANCE }

        fun getRetrofit(baseURL: String): Retrofit {

            return if (baseURL.contains("http")) {
                Retrofit.Builder()
                        .baseUrl(baseURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            } else {
                val url = HTTP_PREFIX.plus(baseURL)
                Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

            }
        }

        private fun hasNetwork(context: Context): Boolean {
            var isConnected = false // Initial Value
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
            return isConnected
        }
    }
}