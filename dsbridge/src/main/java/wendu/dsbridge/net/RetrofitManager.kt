package wendu.dsbridge.net

import com.alibaba.ailabs.iot.gattlibrary.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import wendu.dsbridge.net.config.ServiceConfig
import wendu.dsbridge.util.LogPet
import wendu.dsbridge.util.SignUtil
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit

object RetrofitManager {

    private const val CONNECT_TIME_OUT = 60
    private const val READ_TIME_OUT = 60
    private const val WRITE_TIME_OUT = 60

    private var retrofit: Retrofit? = null

    fun initRetrofit() {
        retrofit = Retrofit.Builder()
            .baseUrl(ServiceConfig.getConfig()?.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getOkHttpClient())
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT.toLong(), TimeUnit.SECONDS)

        // add header interceptor
        builder.addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
//            var url = "/${originalRequest.url().encodedPathSegments().joinToString("/")}"
            val url = originalRequest.url().url().path
            val ts = (System.currentTimeMillis() / 1000).toString()
            LogPet.e("url = $url ,ts = $ts ")
            requestBuilder.addHeader("platform", "android")
            requestBuilder.addHeader("appid", ServiceConfig.getConfig()?.appKey)
            requestBuilder.addHeader("version", "1.0.0")
            requestBuilder.addHeader("ts", ts)
            val signStr = bit32("${ServiceConfig.getConfig()?.appKey}$url$ts")
            requestBuilder.addHeader("sign", signStr)
            val token = ServiceConfig.getLoginRes()?.token
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("authorization", token)
            }
            chain.proceed(requestBuilder.build())
        }

        //add log interceptor
        builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

        if (BuildConfig.DEBUG) {
        } else {
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
        }

        return builder.build()
    }

    fun bit32(str: String): String {
        val digest = MessageDigest.getInstance("MD5")
        digest.update(str.toByteArray())
        val messageDigest = digest.digest()
        return SignUtil.toHexString(messageDigest).toLowerCase()
    }


    fun <T> getService(service: Class<T>): T? {

        return retrofit?.create(service)
    }


    private const val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"
    private const val sizeOfRandomString = 32

    fun getRandomString(): String {
        val random = Random()

        val sb = StringBuilder(sizeOfRandomString)

        for (i in 0 until sizeOfRandomString)

            sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])

        return sb.toString()

    }


}

