package com.deboo.frulens.data

data class FruitResponse(
    val food_id: String,
    val food_name: String,
    val food_type: String,
    val food_url: String,
    val servings: List<Serving>
)