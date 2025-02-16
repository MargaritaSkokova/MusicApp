package com.maran.musicapp.data.online.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        private var gson = GsonBuilder()
            .setLenient()
            .create()

        fun retrofitClient(): Retrofit = Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .client(okHttpClient().build())
            .build()


        private fun okHttpClient() = OkHttpClient().newBuilder()
            .addInterceptor { chain ->
                val request: Request = chain.request()
                    .newBuilder()
                    .header("accept", "*/*")
                    .build()
                chain.proceed(request)
            }
    }
}