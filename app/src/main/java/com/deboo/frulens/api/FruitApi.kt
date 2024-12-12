package com.deboo.frulens.api

import com.deboo.frulens.data.FruitResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FruitApi {
    @GET("nutrition")
    suspend fun getNutrition(
        @Query("fruit") fruit: String
    ):FruitResponse
}