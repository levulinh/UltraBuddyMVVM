package andrew.studio.com.ultrabuddymvvm.data.network

import andrew.studio.com.ultrabuddymvvm.data.network.response.AllMessageResponse
import andrew.studio.com.ultrabuddymvvm.data.network.response.AllUserResponse
import andrew.studio.com.ultrabuddymvvm.data.network.response.GroundResponse
import andrew.studio.com.ultrabuddymvvm.data.network.response.MessageResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface UltraBuddyApiService {
    @GET("message/get")
    fun getAllMessageAsync(
        @Query("from") from: String,
        @Query("to") to: String
    ): Deferred<AllMessageResponse>

    @GET("user/all")
    fun getAllUserAsync(): Deferred<AllUserResponse>

    @FormUrlEncoded
    @POST("message/add")
    fun addNewMessageAsync(
        @Field("from") from: String,
        @Field("to") to: String,
        @Field("content") content: String,
        @Field("code") code: Int
    ): Deferred<MessageResponse>

    @GET("ground/get")
    fun getGroundAsync(
        @Query("userId") userId: String
    ): Deferred<GroundResponse>

    @FormUrlEncoded
    @POST("ground/add")
    fun addGroundAsync(
        @Field("userId") userId: String,
        @Field("width") width: Int,
        @Field("height") height: Int,
        @Field("obstacles") obstacles: String
    ): Deferred<GroundResponse>

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): UltraBuddyApiService {
            val requestInterceptor = Interceptor { chain ->

                val url = chain.request()
                    .url()
                    .newBuilder()
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://ultra-buddy-server.herokuapp.com/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UltraBuddyApiService::class.java)
        }
    }
}