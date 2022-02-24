package gapara.co.id.core.api

import gapara.co.id.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RestClient {

    fun <S> createStringService(serviceClass: Class<S>, baseUrl: String): S {

        val logging = HttpLoggingInterceptor()

        logging.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        httpClient.connectTimeout(1, TimeUnit.MINUTES)
        httpClient.readTimeout(2, TimeUnit.MINUTES)
        httpClient.writeTimeout(2, TimeUnit.MINUTES)
        val client = httpClient.build()

        val retrofit = Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(StringConverterFactory())
            .client(client)
            .build()
        return retrofit.create(serviceClass)
    }

}