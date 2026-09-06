package com.emretastekin.akillimutfakasistani.data.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Emülatör için IP adresimiz
    private const val BASE_URL = "http://10.0.2.2:8000"

    // YENİ: Yapay Zeka işlemleri uzun sürdüğü için bekleme süresini (Timeout) 60 saniyeye çıkarıyoruz
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val backendApiService: BackendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // <-- Hazırladığımız sabırlı client'ı buraya ekledik
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApiService::class.java)
    }
}