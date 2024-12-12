package com.deboo.frulens.api

import com.deboo.frulens.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FruitClient {
    private const val BASE_URL = BuildConfig.FRUIT_URL

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiInstance: FruitApi = retrofit.create(FruitApi::class.java)

}