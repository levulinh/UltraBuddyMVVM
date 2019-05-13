package andrew.studio.com.ultrabuddymvvm.data.network

import andrew.studio.com.ultrabuddymvvm.data.network.response.AllMessageResponse
import andrew.studio.com.ultrabuddymvvm.data.network.response.MessageResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface UltraBuddyApiService {
    @GET("message/get")
    fun getAllMessageAsync(
        @Query("from") from: String,
        @Query("to") to: String
    ): Deferred<AllMessageResponse>

    @FormUrlEncoded
    @POST("message/add")
    fun addNewMessageAsync(
        @Field("from") from: String,
        @Field("to") to: String,
        @Field("content") content: String
    ): Deferred<MessageResponse>


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