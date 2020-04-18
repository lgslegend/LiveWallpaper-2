package novumlogic.live.wallpaper.apiHelper

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Priya Sindkar.
 */
interface ApiService {
    @FormUrlEncoded
    @POST("android/getAllData.php")
    fun fetchApiData(@Field(value = "device_id") deviceId: String, @Field(value = "fcm_id") fcmId: String, @Field(value = "android_version") androidVersion: String): Call<APIResponseModel>
}