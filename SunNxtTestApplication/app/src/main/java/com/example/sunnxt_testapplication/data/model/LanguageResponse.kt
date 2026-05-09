package com.example.sunnxt_testapplication.data.model

import com.google.gson.annotations.SerializedName

data class LanguageResponse(
    @SerializedName("status") val status: String,
    @SerializedName("languages") val languages: List<LanguageGroup>,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int
)

data class LanguageGroup(
    @SerializedName("terms") val terms: List<LanguageTerm>
)

data class LanguageTerm(
    @SerializedName("term") val term: String,
    @SerializedName("image") val image: String,
    @SerializedName("selectedImage") val selectedImage: String,
    @SerializedName("translatedText") val translatedText: String,
    @SerializedName("humanReadable") val humanReadable: String
)
