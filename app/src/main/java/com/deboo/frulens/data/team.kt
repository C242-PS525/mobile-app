package com.deboo.frulens.data


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class team(
    val name: String,
    val description: String,
    val photo: Int
): Parcelable