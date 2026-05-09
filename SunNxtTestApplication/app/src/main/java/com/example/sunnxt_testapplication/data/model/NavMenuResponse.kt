package com.example.sunnxt_testapplication.data.model

import com.google.gson.annotations.SerializedName

data class NavMenuResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("results") val results: List<NavMenuResult>,
    @SerializedName("country") val country: String
)

data class NavMenuResult(
    @SerializedName("name") val name: String,
    @SerializedName("title") val title: String,
    @SerializedName("weightage") val weightage: Int,
    @SerializedName("layoutType") val layoutType: String,
    @SerializedName("actionUrl") val actionUrl: String,
    @SerializedName("images") val images: List<NavMenuImage>
)

data class NavMenuImage(
    @SerializedName("type") val type: String,
    @SerializedName("profile") val profile: String,
    @SerializedName("resolution") val resolution: String,
    @SerializedName("link") val link: String
)
