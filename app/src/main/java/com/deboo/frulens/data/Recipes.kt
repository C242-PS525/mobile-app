package com.deboo.frulens.data

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipes(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("ingredients")
    val ingredients: List<String>,

    @field:SerializedName("steps")
    val steps: List<String>,

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("category")
    val category: List<String>,

    @field:SerializedName("serving")
    val serving: Int,

    @field:SerializedName("prep_time")
    val prep_time: String
):Parcelable