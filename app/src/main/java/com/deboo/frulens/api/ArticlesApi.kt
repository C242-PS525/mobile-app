package com.deboo.frulens.api

import com.deboo.frulens.data.Recipes
import retrofit2.http.GET

interface ArticlesApi {
    @GET("api/recipes")
    suspend fun getRecipes(): List<Recipes>
}